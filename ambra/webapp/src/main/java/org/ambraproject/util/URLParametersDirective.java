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

package org.ambraproject.util;

import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.NumberModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.core.Environment;
import freemarker.template.TemplateModelIterator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: josowski
 * Date: Apr 23, 2010
 * Time: 11:25:58 AM
 *
 * This class can be used to generate URL strings from the set of variables stored in the
 * template model.
 *
 * If you specify a name/value pair as parameters to this directive you'll replace
 * existing values with the passed in value.
 *
 * If the name does not exist as part of the original collection, the new name/value pair
 * will not be added to the returned URL
 *
 * This currently handle arrays in a not intuitive way.  If you specify an array/collection
 * variable name, the whole of the value applied will replace the array by default.
 * If you specifiy method='add' the new value will be appended to the list.
 *
 * The first parameter can also be a comma delimited list.
 *
 * The second parameter can also be a comma delimeted list.  In such case an
 * attempt is made to match each value with the name of the same index.
 * If there are less values then names the last value will be applied to all
 * subsequent names
 */
public class URLParametersDirective implements TemplateDirectiveModel {
  private static final String UTF8 = "UTF-8";

  public void execute(Environment environment, Map params,
    TemplateModel[] loopVars,
    TemplateDirectiveBody body)
    throws TemplateException, IOException {

    Object searchParams;
    Object values;
    String names;
    String method;

    if (params.isEmpty()) {
      throw new TemplateModelException(
          "URLParameterDirective is missing required parameters of parameters.");
    } else {
      searchParams = params.get("parameters");
      names = params.get("names")==null?null:String.valueOf(params.get("names"));
      method = params.get("method")==null?null:String.valueOf(params.get("method"));
      values = params.get("values");

      if(searchParams == null) {
        throw new TemplateModelException(
          "URLParameterDirective is missing required parameters of parameters.");
      }

      if(!(searchParams instanceof StringModel))
      {
        throw new TemplateModelException(
          "URLParameterDirective searchParams parameter is not instance of StringModel.");
      }

      if(method == null) {
        method = "replace";
      } else {
        if(!(method.equals("add") || method.equals("replace"))) {
          throw new TemplateModelException(
            "URLParameterDirective only accepts 'add' and 'replace' as variable methods.");
        }
      }
    }

    if (loopVars.length != 0) {
      throw new TemplateModelException(
          "URLParameterDirective doesn't allow loop variables.");
    }

    environment.getOut().write(makeURLParameters((StringModel)searchParams, names, values, method));
  }

  /**
   * Takes two strings.  If the second string is a comma delimited set, compare
   * each value of that set. 
   * @param key
   * @param paramName
   * @return -1 if no match is found. 0 or greater if there is a match.  If there is a match
   * this return value will reflect the index on which it is found.  Will return 0 if the
   * parameter is not a string
   */
  private int propertyNameCheck(String key, String paramName) {
    int res = -1;
    if(paramName != null) {
      String[] params = paramName.split(",");

      if(params.length > 0) {
        for(String p : params) {
          res++;
          if(key.equals(p)) {
            return res;
          }
        }
        return -1;
      } else {
        if(String.valueOf(key).equals(paramName)) {
          return 0;
        } else {
          return -1;
        }
      }
    }
    return -1;
  }

  /**
   * If this function is passed an array to get a value
   * Use that instead of the value parameter
   * Pair the value element with the passed index
   * If index is larger then the values list, use the last value in the list
   * @param values
   * @return
   */
  private String getValue(int index, Object values)
      throws TemplateModelException
  {
    if(values != null) {
      if(values instanceof SimpleSequence) {
        String val = null;
        if(index < ((SimpleSequence)values).size()) {
          return String.valueOf(((SimpleSequence)values).get(index));
        } else {
          return String.valueOf(((SimpleSequence)values).get(((SimpleSequence)values).size()));
        }
      }

      if((values instanceof NumberModel) || (values instanceof StringModel)
          || (values instanceof SimpleScalar)) {
        return String.valueOf(values);
      }

      throw new TemplateModelException("A bad value was given, values must be of type string, number" +
          " or collection in a format of: [\"value\",1,5]");
      
    } else {
      return "";
    }
  }

  public String makeURLParameters(StringModel params, String names, Object values, String method)
      throws TemplateModelException
  {
    TemplateCollectionModel tc = params.keys();
    TemplateModelIterator keysIterator = params.keys().iterator();
    StringBuilder sb = new StringBuilder();

    while(keysIterator.hasNext()) {
      TemplateModel key = keysIterator.next();

      //Lets ignore the class key
      if(!key.toString().equals("class")) {
        Object o = params.get(key.toString());

        //It's either a collection or value
        if(o instanceof ArrayModel) {
          sb.append(key.toString());
          sb.append("=");

          TemplateModelIterator arrayIt = ((ArrayModel)o).iterator();

          int keyIndex = propertyNameCheck(String.valueOf(key),names);

          if(keyIndex > -1) {
            try {
              if(method.equals("replace")) {
                String val = getValue(keyIndex, values);
                sb.append(URLEncoder.encode(val,UTF8));
              }

              if(method.equals("add")) {
                while(arrayIt.hasNext()) {
                  sb.append(String.valueOf(arrayIt.next()));
                  sb.append(",");
                }
                String val = getValue(keyIndex, values);
                sb.append(URLEncoder.encode(val,UTF8));
              }
            } catch (UnsupportedEncodingException ex) {
              throw new TemplateModelException("UnsupportedEncodingException, " + UTF8 + " not supported: ", ex);
            }
          } else {
            while(arrayIt.hasNext()) {
              sb.append(String.valueOf(arrayIt.next()));

              if(arrayIt.hasNext()) {
                sb.append(",");
              }
            }
          }
        } else if((o instanceof NumberModel) || (o instanceof StringModel)) {
          sb.append(String.valueOf(key));
          sb.append("=");

          try {
            int keyIndex = propertyNameCheck(String.valueOf(key),names);

            if(keyIndex > -1) {
              String val = getValue(keyIndex, values);
              sb.append(URLEncoder.encode(val,UTF8));
            } else {
              sb.append(URLEncoder.encode(String.valueOf(o),UTF8));
            }
          } catch (UnsupportedEncodingException ex) {
            throw new TemplateModelException("UnsupportedEncodingException, " + UTF8 + " not supported: ", ex);
          }
        }

        //If the object matches any of the preceding cases, add a "&"
        if((o instanceof ArrayModel) || (o instanceof NumberModel) || (o instanceof StringModel)) {
          if(keysIterator.hasNext()) {
            sb.append("&");
          }
        }
      }
    }

    return sb.toString();
  }
}
