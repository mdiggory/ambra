/* $HeadURL::                                                                            $
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
package org.ambraproject.annotation.action;

/**
 * Get annotation for a given id.
 *
 * We can't use BaseGetAnnotationAction directly as spring runs into this problem
 * org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name
 * 'org.ambraproject.annotation.action.AnnotationActionsTest': Unsatisfied dependency
 * expressed through bean property 'getAnnotationAction': There are 2 beans of type [class
 * org.ambraproject.annotation.action.GetAnnotationAction] for autowire by type. There should
 * have been exactly 1 to be able to autowire property 'getAnnotationAction' of bean
 * 'org.ambraproject.annotation.action.AnnotationActionsTest'.
 *
 * Consider using autowire by name instead.
 */
@SuppressWarnings("serial")
public class GetAnnotationAction extends BaseGetAnnotationAction {
}
