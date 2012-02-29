package bright.zheng.jtwissandra.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import bright.zheng.jtwissandra.bean.Tweet;

/**
 * Tweet service
 * 
 * @author bright_zheng
 *
 */
public class TweetService extends BaseService{
	
	/**
	 * Adding a tweet has following logic:
	 * 1. Save the tweet to CF of TWEET
	 * 2. Add the new tweet to my TIMELINE
	 * 3. Add the new tweet to all my followers' TIMELINE
	 * 
	 * @param me
	 * @param friend
	 */
    public String addTweet(String user_uuid, String tweet_content) {
    	Mutator<String> mutator = HFactory.createMutator(
    			KEYSPACE, SERIALIZER_STRING);
    	//the tweet uuid
    	UUID uuid = this.getUUID();
    	String tweet_uuid = uuid.toString();
    	logger.debug("tweet_uuid={}", tweet_uuid);
    	
    	//the timestamp to build the timeline
    	Long timestamp = this.getTimestamp(uuid);
    	logger.debug("timestamp={}", timestamp);
        
        mutator.addInsertion(tweet_uuid, CF_TWEET, 
        		this.createColumn("user_uuid", user_uuid));
        mutator.addInsertion(tweet_uuid, CF_TWEET, 
        		this.createColumn("tweet_content", tweet_content));
        
        mutator.addInsertion(user_uuid, CF_TIMELINE, 
        		this.createColumn(timestamp, tweet_uuid));
        
        // get back all my followers and insert the tweet to his/her TIMELINE one by one
        SliceQuery<String, Long, String> sliceQuery = 
            HFactory.createSliceQuery(KEYSPACE, SERIALIZER_STRING, SERIALIZER_LONG, SERIALIZER_STRING);        
        sliceQuery.setColumnFamily(CF_FOLLOWER);
        sliceQuery.setKey(user_uuid);
        sliceQuery.setRange(Long.MIN_VALUE, Long.MAX_VALUE, false, 500); //TODO: 500 followers hard code here?
        QueryResult<ColumnSlice<Long, String>> result = sliceQuery.execute();
        Iterator<HColumn<Long, String>> followers = result.get().getColumns().iterator();
        while(followers.hasNext()) {
        	HColumn<Long, String> follower = followers.next();
        	String follower_uuid = follower.getValue();
        	logger.debug("follower's uuid={}", follower_uuid);
        	logger.debug("timestamp={}", follower.getName());
            
        	//insert the tweet to the follower's TIMELINE
            mutator.addInsertion(follower_uuid, CF_TIMELINE, 
            		this.createColumn(timestamp, tweet_uuid));
        }
        
        mutator.execute();
        
        //return the new generated tweet's uuid
        return tweet_uuid;
    }
    
    /**
     * Get one specified tweet by uuid
     * 
     * @param tweet_uuid
     * @return
     */
    public Tweet getTweet(String tweet_uuid){
    	SliceQuery<String, String, String> sliceQuery = 
            HFactory.createSliceQuery(KEYSPACE, SERIALIZER_STRING, SERIALIZER_STRING, SERIALIZER_STRING);        
        sliceQuery.setColumnFamily(CF_TWEET);
        sliceQuery.setKey(tweet_uuid);
        sliceQuery.setColumnNames("user_uuid", "tweet_content");
        QueryResult<ColumnSlice<String, String>> result = sliceQuery.execute();
        List<HColumn<String, String>> list = result.get().getColumns();
        
        HColumn<String,String> user = list.get(0);		// column of user_uuid
        HColumn<String,String> content = list.get(1);	// column of tweet_content
        
    	return new Tweet(tweet_uuid, content.getValue(), user.getValue());
    }
    
    /**
     * Using this method to multi-get will cause sequencial issue
     * because the tweets won't be sorted in chronological order 
     * 
     * @param tweet_uuids
     * @return
     * 
     * @deprecated
     */
    public List<Tweet> getTweets(List<String> tweet_uuids){
    	MultigetSliceQuery<String, String, String> multigetSlicesQuery =
            HFactory.createMultigetSliceQuery(KEYSPACE, SERIALIZER_STRING, SERIALIZER_STRING, SERIALIZER_STRING);
        multigetSlicesQuery.setColumnFamily(CF_TWEET);
        multigetSlicesQuery.setColumnNames("user_uuid","tweet_content");        
        multigetSlicesQuery.setKeys(tweet_uuids);
        QueryResult<Rows<String, String, String>> results = multigetSlicesQuery.execute();
    	return convertRowsToTweets(results.get());
    }
    
    private List<Tweet> convertRowsToTweets(Rows<String, String, String> rows){
    	List<Tweet> list = new ArrayList<Tweet>();
    	Iterator<Row<String, String, String>> iterator = rows.iterator();
    	while(iterator.hasNext()){
    		Row<String, String, String> row = iterator.next();
    		ColumnSlice<String, String> cs = row.getColumnSlice();
        	list.add(new Tweet(row.getKey(), 
        					   cs.getColumnByName("tweet_content").getValue(),
        					   cs.getColumnByName("user_uuid").getValue()));
    	}
    	return list;
    }
    
}
