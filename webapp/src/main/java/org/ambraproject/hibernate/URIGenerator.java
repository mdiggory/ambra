/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.hibernate;
import org.ambraproject.models.AnnotationType;
import org.topazproject.ambra.configuration.ConfigurationStore;
import org.ambraproject.models.Annotation;
import java.net.URI;
import java.util.UUID;

/**
 * Id generator create object identifiers.  Assigns a randomly generated URI
 * with the appropriate URI prefix per class. (For Replies / Annotations and UserProfiles)
 *
 * @author Joe Osowski
 *
 */
public class URIGenerator {
  private static String prefix = System.getProperty(ConfigurationStore.SYSTEM_OBJECT_ID_PREFIX);

  /**
   * Get the currently defined URI prefix
   * @return the current URI prefix
   */
  public static String getPrefix()
  {
    return prefix;
  }
  
  /**
   * Generates a URI for the given class passed in
   * @param object the object to create a URI for.
   * @return a new URI
   * @throws RuntimeException If the system property can't be found, or if a bad URI is generated
   */
  public static String generate(Object object) throws RuntimeException {

    String className = object.getClass().getSimpleName();
    String objectPrefix = null;

    if(prefix == null) {
      throw new RuntimeException(ConfigurationStore.SYSTEM_OBJECT_ID_PREFIX + " is not defined as a system property.");
    }
    
    if(className.equals("Rating")) {
      objectPrefix = "annotation";
    } else if(className.equals("Annotation")) {
      AnnotationType annotationType = ((Annotation)object).getType();
      switch(annotationType) {
        case REPLY:
          objectPrefix = "reply";
          break;
        default:
          objectPrefix = "annotation";
      }
    } else if(className.equals("UserProfile")) {
      objectPrefix = "profile";
    } else {
      throw new RuntimeException("Unsupported class of type:" + className);
    }

    StringBuilder id = new StringBuilder();

    id.append(prefix)
      .append(objectPrefix)
      .append("/")
      .append(UUID.randomUUID().toString());

    //This may seem a bit weird, but I wanted to confirm that it actually is a URI before returning
    //a string
    return URI.create(id.toString()).toString();
  }
}
