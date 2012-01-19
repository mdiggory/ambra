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

package org.ambraproject.struts2;

import freemarker.cache.MruCacheStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts2.ServletActionContext;
import org.ambraproject.web.VirtualJournalContext;

/**
 * AmbraTemplateStorage
 *
 * By default the freemarker cache stores templates using just their filename
 * Under most circumstances this would be ok, but being we use the same filenames across
 * Journals in include files, the first file to be cached will then hence be the
 * file served for all journals
 *
 * This cache is journal aware and stores the journal name as part of the key for the
 * freemarker template 
 *
 * @author Joe Osowski 
 */
public class AmbraTemplateStorage extends MruCacheStorage
{
  private static final Logger log = LoggerFactory.getLogger(AmbraTemplateStorage.class);

  AmbraTemplateStorage(int maxStrongSize, int maxSoftSize)
  {
    super(maxStrongSize, maxSoftSize);
  }

  /**
   * Get a template from the cache
   *
   * @param key key
   * @return a freemarker template
   */
  @Override
  public Object get(Object key) {
    VirtualJournalContext journalContext = (VirtualJournalContext) ServletActionContext.getRequest().
        getAttribute(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT);
    
    return super.get(new TemplateKey(key, journalContext.getJournal()));
  }

  /**
   * Add a template to the cache
   *
   * @param key key
   * @param value template
   */
  @Override
  public void put(Object key, Object value)
  {
    VirtualJournalContext journalContext = (VirtualJournalContext) ServletActionContext.getRequest().
        getAttribute(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT);

    super.put(new TemplateKey(key, journalContext.getJournal()), value);
  }

  /**
   * TemplateCache has a private class for keys.  I would have just overridden that,
   * But well, it's private.  This class is a wrapper for TemplateCache.TemplateKey 
   */
  private class TemplateKey
  {
    private Object originalKey;
    private String journal;

    TemplateKey(Object originalKey, String journal)
    {
      if(originalKey == null) throw new RuntimeException("TemplateKey can not be created with a " +
          "orignalKey value of null");

      if(journal == null) throw new RuntimeException("TemplateKey can not be created with a " +
          "journal value of null");

      this.originalKey = originalKey;
      this.journal = journal;
    }
    
    public boolean equals(Object o)
    {
      if(this == o) return true;
      
      if(o instanceof TemplateKey) {
        return (this.journal.equals(((TemplateKey)o).journal) &&
            this.originalKey.equals(((TemplateKey)o).getKey()));
      } else {
        return false;
      }
    }

    public int hashCode()
    {
      return this.originalKey.hashCode() * this.journal.hashCode();
    }

    public String getJournal()
    {
      return this.journal;
    }

    public Object getKey()
    {
      return this.originalKey;
    }
  }
}
