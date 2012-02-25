package bright.zheng.jtwissandra.bean;

/**
 * Tweet
 * 
 * @author bright_zheng
 *
 */
public class Tweet implements java.io.Serializable{
	private static final long serialVersionUID = 4574737047356855258L;

	private String tweet_uuid;
	private String tweet_content;
	private String user_uuid;
	//private Long tweet_createTime; //TODO: The tweet should have the timestamp?
	
	public Tweet(){}
	
	public Tweet(String tweet_uuid, String tweet_content, String user_uuid){
		this.tweet_uuid = tweet_uuid;
		this.tweet_content = tweet_content;
		this.user_uuid = user_uuid;
	}
	
	public String getTweet_uuid() {
		return tweet_uuid;
	}

	public void setTweet_uuid(String tweet_uuid) {
		this.tweet_uuid = tweet_uuid;
	}

	public String getTweet_content() {
		return tweet_content;
	}

	public void setTweet_content(String tweet_content) {
		this.tweet_content = tweet_content;
	}

	public void setUser_uuid(String user_uuid) {
		this.user_uuid = user_uuid;
	}

	public String getUser_uuid() {
		return user_uuid;
	}
}
