package bright.zheng.jtwissandra;

import java.io.IOException;
import java.util.Properties;

import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for Cassandra initialization
 * 
 * @author bright_zheng
 *
 */
public class HFactoryHelper {
	private static Logger logger = LoggerFactory.getLogger(HFactoryHelper.class);
	
	private static Cluster cluster;
	private static Keyspace keyspace = initKeyspace();
	private static Properties properties;
    
    private HFactoryHelper(){}
    
    public static Keyspace getKeyspace(){
    	return keyspace;
    }
    
    private static Keyspace initKeyspace() {
        properties = new Properties();
        try {
            properties.load(HFactoryHelper.class.getResourceAsStream("/config.properties"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        String cluster_name = properties.getProperty("cluster.name", "Test Cluster");
        logger.debug("cluster.name={}", cluster_name);
        String cluster_hosts = properties.getProperty("cluster.hosts", "127.0.0.1:9160");
        logger.debug("cluster.hosts={}", cluster_hosts);
        String active_keyspace = properties.getProperty("keyspace", "JTWISSANDRA");
        logger.debug("keyspace={}", active_keyspace);
        
        cluster = HFactory.getOrCreateCluster(cluster_name, cluster_hosts);
        
        ConfigurableConsistencyLevel ccl = new ConfigurableConsistencyLevel();
        ccl.setDefaultReadConsistencyLevel(HConsistencyLevel.ONE);
        
        return HFactory.createKeyspace(
        		active_keyspace,
        		cluster, 
        		ccl);
    }
}
