package org.ambraproject.filestore.impl;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

/**
 * Object Factory for {@link MogileFSImpl} instances to be put in JNDI.
 * <p/>
 * Configure in the context.xml as follows:
 * <p/>
 * <code> &lt;Resource name="ambra/FileStore" type="org.ambraproject.filestore.FileStoreService"
 * factory="org.ambraproject.filestore.impl.MogileFileStoreFactory" domain="plos-new" trackers="sfdev01:6001,sfweb02:6001"
 * maxTrackerConnections="-1" maxIdleConnections="2" maxIdleTimeMillis="10000" reproxyEnable="false"
 * reproxyCacheSettings="" /&gt; </code>
 *
 * @author Alex Kudlick 10/17/11
 */
public class MogileFileStoreFactory implements ObjectFactory {

  public static final String DOMAIN = "domain";
  public static final String TRACKERS = "trackers";
  public static final String MAX_TRACKER_CONNECTIONS = "maxTrackerConnections";
  public static final String MAX_IDLE_CONNECTIONS = "maxIdleConnections";
  public static final String MAX_IDLE_TIME_MILLIS = "maxIdleTimeMillis";
  public static final String REPROXY_ENABLE = "reproxyEnable";
  public static final String REPROXY_CACHE_SETTINGS = "reproxyCacheSettings";

  @Override
  public Object getObjectInstance(Object o, Name name, Context context, Hashtable<?, ?> hashtable) throws Exception {
    String domain = (String) ((Reference) o).get(DOMAIN).getContent();
    String trackers = (String) ((Reference) o).get(TRACKERS).getContent();
    Integer maxTrackerConnections = Integer.parseInt((String) ((Reference) o).get(MAX_TRACKER_CONNECTIONS).getContent());
    Integer maxIdleConnections = Integer.parseInt((String) ((Reference) o).get(MAX_IDLE_CONNECTIONS).getContent());
    Integer maxIdleTimeMillis = Integer.parseInt((String) ((Reference) o).get(MAX_IDLE_TIME_MILLIS).getContent());
    Boolean reproxyEnable = Boolean.valueOf((String) ((Reference) o).get(REPROXY_ENABLE).getContent());
    String reproxyCacheSettings = (String) ((Reference) o).get(REPROXY_CACHE_SETTINGS).getContent();

    return new MogileFSImpl(domain, trackers, maxTrackerConnections,
        maxIdleConnections, maxIdleTimeMillis, reproxyEnable, reproxyCacheSettings);
  }
}
