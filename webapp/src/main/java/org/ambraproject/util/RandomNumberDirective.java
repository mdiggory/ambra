/*
 * $HeadURL$
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

package org.ambraproject.util;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: josowski
 * Date: May 18, 2010
 * Time: 11:48:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class RandomNumberDirective implements TemplateDirectiveModel {
  public void execute(Environment environment, Map params,
    TemplateModel[] loopVars,
    TemplateDirectiveBody body)
    throws TemplateException, IOException {

    int maxValue = 100;

    if (!params.isEmpty()) {
      if(params.get("maxValue") != null) {
        maxValue = Integer.valueOf(String.valueOf(params.get("maxValue")));
      }
    }

    if (loopVars.length != 0) {
      throw new TemplateModelException(
          "URLParameterDirective doesn't allow loop variables.");
    }

    Random r = new Random();
    
    environment.getOut().write(String.valueOf(r.nextInt(maxValue)));
  }
}
