/* $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.struts2;

import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.beans.factory.annotation.Required;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

/**
 * Interceptor to wrap an action invocation in a transaction.  All action invocations will be wrapped; to provide
 * further parameters for the transaction, annotate the action method with @{@link
 * org.springframework.transaction.annotation.Transactional}. Transactions will be wrapped around the whole invocation,
 * ensuring that each action request is modular.
 * <p/>
 * Transactions will take parameters from the Transactional annotation on the action method; they will also be rolled
 * back for any action result which isn't {@link Action#SUCCESS}
 * <p/>
 * Annotating the action method with {@link org.ambraproject.struts2.ManualTransactionManagement} will tell this
 * interceptor not to wrap a transaction around the request.
 * <p/>
 * This is the ONLY form of transaction management active at the time of this writing (July 2011).  If we wanted to use
 * Spring's declarative transaction management (see <a href="http://static.springsource.org/spring/docs/2.0.x/reference/transaction.html">the
 * spring docs</a> for more info), we would need to do one of two things:
 * <p/>
 * <ol> <li>Refactor action classes so that business logic which is desired to be transactional occurs within one
 * service bean call.  This means that we shouldn't have action methods which call two service bean methods, hoping that
 * a failure in the second would rollback the work from the first.  That doesn't work</li>
 * <p/>
 * <li>Don't handle rollback-worthy exceptions in the action class.  We can configure struts to display error pages for
 * uncaught exceptions.  In this solution, exceptions would pass through the action class unhandled, a transaction
 * manager would rollback the transaction, and struts would display an error page.  The problem is that if you handle
 * the error in the action class, then the transaction manager has no way of knowing there was a problem.  That's why we
 * check the result of the action invocation here.</li>
 * <p/>
 * Of the two, option one is the more architecturally "pure" solution. </ol>
 *
 * @author Dragisa Krsmanovic
 * @author Alex Kudlick
 */
public class TransactionInterceptor extends AbstractInterceptor {
  private static final Logger log = LoggerFactory.getLogger(TransactionInterceptor.class);

  private PlatformTransactionManager transactionManager;

  public String intercept(final ActionInvocation actionInvocation) throws Exception {

    final Action action = (Action) actionInvocation.getAction();
    final ActionProxy actionProxy = actionInvocation.getProxy();
    final String methodName = actionProxy.getMethod();

    if (getAnnotation(action.getClass(), methodName, ManualTransactionManagement.class) != null) {
      //Method is annotated tellling us not to manage a transaction for it
      log.debug("Not managing transaction for "
          + action.getClass().getSimpleName() + "." + methodName + "()");
      return actionInvocation.invoke();
    }

    if (log.isDebugEnabled()) {
      log.debug("Intercepted " + action.getClass().getSimpleName() + "." + methodName + "()");
    }

    final Transactional transactionalAnnotation = getAnnotation(action.getClass(), methodName, Transactional.class);
    TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
    if (transactionalAnnotation != null) {
      txTemplate.setReadOnly(transactionalAnnotation.readOnly());
      txTemplate.setTimeout(transactionalAnnotation.timeout());
      txTemplate.setIsolationLevel(transactionalAnnotation.isolation().value());
      txTemplate.setPropagationBehavior(transactionalAnnotation.propagation().value());
    }

    CallbackResult callbackResult = (CallbackResult) txTemplate.execute(new TransactionCallback() {
      public CallbackResult doInTransaction(TransactionStatus transactionStatus) {
        CallbackResult result = new CallbackResult();
        try {
          String actionResult = actionInvocation.invoke();
          result.setResult(actionResult);
          //Rollback for Action responses indicating failure
          for (String response : new String[]{Action.ERROR, Action.INPUT, Action.LOGIN}) {
            if (response.equalsIgnoreCase(actionResult) && ! transactionStatus.isRollbackOnly()) {
              log.debug("Rolling back action " + action.getClass().getSimpleName()
                  + " due to result: " + actionResult);
              transactionStatus.setRollbackOnly();
              break;
            }
          }
        } catch (Exception e) {
          /*
          * Callback does not throw exception. We need to pass Exception object in the return
          * parameter so we can throw it in the calling method.
          */
          boolean noRollback = false;

          if (transactionalAnnotation != null && transactionalAnnotation.noRollbackFor() != null) {
            for (Class<? extends Throwable> exception : transactionalAnnotation.noRollbackFor()) {
              if (exception.isInstance(e)) {
                noRollback = true;
                break;
              }
            }
          }

          if (!noRollback && transactionalAnnotation != null &&  transactionalAnnotation.rollbackFor() != null) {
            for (Class<? extends Throwable> exception : transactionalAnnotation.rollbackFor()) {
              if (exception.isInstance(e)) {
                log.debug("Caught exception, rolling back action invocation " + action.getClass().getSimpleName());
                transactionStatus.setRollbackOnly();
                break;
              }
            }
          }
          result.setException(e);
        }
        return result;
      }
    });

    if (callbackResult.getException() != null)
      throw callbackResult.getException();

    return callbackResult.getResult();
  }

  private <A extends Annotation> A getAnnotation(Class<? extends Action> actionClass,
                                                 String methodName, Class<A> annotationType) throws Exception {
    A annotation = actionClass.getAnnotation(annotationType);
    if (annotation == null) {
      annotation = getMethodAnnotation(actionClass, methodName, annotationType);
    }

    return annotation;
  }

  private <A extends Annotation> A getMethodAnnotation(Class<? extends Action> actionClass,
                                                       String methodName, Class<A> annotationType) {
    try {
      Method method = actionClass.getMethod(methodName);
      A annotation = method.getAnnotation(annotationType);
      if (annotation == null) {
        Class parent = actionClass.getSuperclass();
        if (Action.class.isAssignableFrom(parent)) {
          annotation = getMethodAnnotation((Class<? extends Action>) parent,
              methodName, annotationType);
        }
      }
      return annotation;
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  /**
   * Spring setter method. Sets Spring transaction manager
   *
   * @param transactionManager Transaction manager
   */
  @Required
  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  /**
   * Return value from TransactionTemplate callback. Encapsulates possible Exception.
   */
  private static class CallbackResult {

    private String result;
    private Exception exception;

    public String getResult() {
      return result;
    }

    public void setResult(String result) {
      this.result = result;
    }

    public Exception getException() {
      return exception;
    }

    public void setException(Exception exception) {
      this.exception = exception;
    }
  }
}
