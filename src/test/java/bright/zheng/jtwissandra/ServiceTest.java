package bright.zheng.jtwissandra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bright.zheng.jtwissandra.bean.Timeline;
import bright.zheng.jtwissandra.bean.Tweet;
import bright.zheng.jtwissandra.bean.User;
import bright.zheng.jtwissandra.service.FriendService;
import bright.zheng.jtwissandra.service.TimelineService;
import bright.zheng.jtwissandra.service.TimelineService.TimelineWrapper;
import bright.zheng.jtwissandra.service.TweetService;
import bright.zheng.jtwissandra.service.UserService;

/**
 * Test cases for all services currently provided.
 * Please drop and create schema first and then run all cases as one round
 * The 'me' and 'friend' will be created each round dynamically for easier testing
 * 
 * @author bright_zheng
 *
 */
public class ServiceTest{
	Logger logger = LoggerFactory.getLogger(ServiceTest.class);
	
	private static UserService SERVICE_USER = new UserService();
	private static FriendService SERVICE_FRIEND = new FriendService();
	private static TweetService SERVICE_TWEET = new TweetService();
	private static TimelineService SERVICE_TIMELINE = new TimelineService();
	
	private static String me;
	private static String friend;
	
	private static long nextTimeline = 0L;
	
	@BeforeClass
	public static void setUp(){
		//
	}

	@Test
    public void addUser() {
		logger.debug("=====================addUser{====================");
		//add user 1
		me = SERVICE_USER.addUser(new User("itstarting","1234"));
		logger.debug("This round of tesing, ME={}", me);
		Assert.assertNotNull(me);

		//add user 2
		friend = SERVICE_USER.addUser(new User("test1","1234"));	
		logger.debug("This round of tesing, FRIEND={}", friend);
		Assert.assertNotNull(friend);	
		logger.debug("=====================}//addUser====================");
    }   
    
	/**
	 * I'm following a friend
	 */
    @Test
    public void followFriend() {
		logger.debug("=====================followFriend{====================");
		SERVICE_FRIEND.followFriend(me, friend);
		logger.debug("=====================}//followFriend====================");
    }
    
    /**
     * I'm followed by a follower
     */
    @Test
    public void followedByFollower() {
		logger.debug("=====================followedByFollower{====================");
		SERVICE_FRIEND.followFriend(friend, me);		
		logger.debug("=====================}//followedByFollower====================");
    }
    
    /**
     * I'm twittering
     */
    @Test
    public void addTweetByMe() {
		logger.debug("=====================addTweetByMe{====================");
		for(int i=0; i<100; i++){
			String tweet_uuid = SERVICE_TWEET.addTweet(me, "Hellow JTWISSANDRA -- by itstarting:" + i);
			Assert.assertNotNull(tweet_uuid);
		}
		logger.debug("=====================}//addTweetByMe====================");
    }
    
    /**
     * My friend is twittering
     * 
     */
    @Test
    public void addTweetByFriend() {
		logger.debug("=====================addTweetByFriend{====================");
		for(int i=0; i<100; i++){
	    	String tweet_uuid = SERVICE_TWEET.addTweet(friend, "Hellow JTWISSANDRA -- by test1:" + i);
			Assert.assertNotNull(tweet_uuid);
		}
		logger.debug("=====================}//addTweetByFriend====================");
    }
    
    /**
     * Get tweets for me
     */
    @Test
    public void getTweetsByMe(){
		logger.debug("=====================getTweetsByMe{====================");
    	getTweets(me, 0);
		logger.debug("=====================}//getTweetsByMe====================");
    }
    
    /**
     * Get tweets at next Timeline (if any)
     */
    @Test
    public void getTweetsByMeForNextTimeline(){
		logger.debug("=====================getTweetsByMeForNextTimeline{====================");
		if(nextTimeline>0L){
			getTweets(me, nextTimeline);
		}
		logger.debug("=====================}//getTweetsByMeForNextTimeline====================");
    }
    
    /**
     * Get tweets for my friend
     */
    @Test
    public void getTweetsByMyFriend(){
		logger.debug("=====================getTweetsByMyFriend{====================");
    	getTweets(friend, 0);
		logger.debug("=====================}//getTweetsByMyFriend====================");
    }
    
    /**
     * 
     */
    @Test
    public void getTweetsByMyFriendForNextTimeline(){
		logger.debug("=====================getTweetsByMyFriendForNextTimeline{====================");
    	getTweets(friend, nextTimeline);
		logger.debug("=====================}//getTweetsByMyFriendForNextTimeline====================");
    }
    
    private void getTweets(String user_uuid, long start){
    	TimelineWrapper wrapper = SERVICE_TIMELINE.getTimeline(user_uuid, start);
    	Assert.assertNotNull(wrapper);
    	List<Timeline> list = wrapper.getTimelines();
    	List<String> tweet_uuids = new ArrayList<String>();
    	for(Timeline timeline: list){
    		String tweet_uuid = timeline.getTweet_uuid();
    		logger.debug("From Timeline: tweet_uuid={}, tweet_timestamp={}", 
    				tweet_uuid, timeline.getTweet_timestamp());
    		tweet_uuids.add(tweet_uuid);
    	}
    	List<Tweet> tweets = SERVICE_TWEET.getTweets(tweet_uuids);
    	Iterator<Tweet> it = tweets.iterator();
    	while(it.hasNext()){
    		Tweet tweet = it.next();
    		logger.debug("From Tweet: tweet_uuid={}, tweet_content={}, user_uuid={}", 
    				new Object[]{tweet.getTweet_uuid(), 
    							 tweet.getTweet_content(),
    							 tweet.getUser_uuid()
    							});
    	}
    	if(wrapper.getNextTimeline() > 0L){
    		logger.debug("The start timeline of next page is: {}", wrapper.getNextTimeline());
    		nextTimeline = wrapper.getNextTimeline();
    	}else{
    		logger.debug("No next page available");
    		nextTimeline = 0L;
    	}
    }
    
    @AfterClass
    public static void shutdown(){
    	//cluster.getConnectionManager().shutdown();
    }
}
