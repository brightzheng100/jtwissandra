package bright.zheng.jtwissandra.service;

import java.util.UUID;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.clock.MicrosecondsSyncClockResolution;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.ClockResolution;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bright.zheng.jtwissandra.HFactoryHelper;

/**
 * Base service which all business services should extend
 * 
 * @author bright_zheng
 *
 */
public class BaseService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected static Keyspace KEYSPACE = HFactoryHelper.getKeyspace();
	protected static final String CF_USER = "USER";
	protected static final String CF_FRIEND = "FRIEND";
	protected static final String CF_FOLLOWER = "FOLLOWER";
	protected static final String CF_TWEET = "TWEET";
	protected static final String CF_TIMELINE = "TIMELINE";

	protected static final StringSerializer SERIALIZER_STRING 
		= StringSerializer.get();
	protected static final LongSerializer SERIALIZER_LONG 
		= LongSerializer.get();
	
	protected static final int TWEETS_LIMIT_DEFAULT = 10;
	protected static final int TWEETS_LIMIT_MAX = 50;
	
	protected HColumn<String, String> createColumn(String name, String value) {		
		return HFactory.createColumn(name, value, SERIALIZER_STRING, SERIALIZER_STRING);
	}

	protected HColumn<String, Long> createColumn(String name, Long value) {		
		return HFactory.createColumn(name, value, SERIALIZER_STRING, SERIALIZER_LONG);
	}

	protected HColumn<Long, String> createColumn(Long name, String value) {		
		return HFactory.createColumn(name, value, SERIALIZER_LONG, SERIALIZER_STRING);
	}
	
	/**
	 * REF: 
	 * 1. FAQ
	 * 		http://wiki.apache.org/cassandra/FAQ#working_with_timeuuid_in_java
	 * 2. DISCUSSION:
	 * 		https://groups.google.com/forum/?fromgroups#!topic/hector-users/IfABWOh0HLg
	 * 
	 * @return UUID
	 */
	public UUID getUUID(){
		ClockResolution clock = new MicrosecondsSyncClockResolution();
		return TimeUUIDUtils.getTimeUUID(clock);
	}
	
	protected Long getTimestamp(UUID uuid){
		return uuid.timestamp();
		//return TimeUUIDUtils.getTimeFromUUID(uuid);
	}
	
	protected Long generateTimestamp(){
		return getTimestamp(getUUID());
	}
}
