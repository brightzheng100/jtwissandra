package bright.zheng.jtwissandra.bean;

/**
 * Timeline
 * 
 * @author bright_zheng
 *
 */
public class Timeline implements java.io.Serializable{
	private static final long serialVersionUID = 6640316955312206243L;
	private String tweet_uuid;
	private Long tweet_timestamp;
	
	public Timeline(String tweet_uuid, Long tweet_timestamp){
		this.tweet_uuid = tweet_uuid;
		this.tweet_timestamp = tweet_timestamp;
	}

	public String getTweet_uuid() {
		return tweet_uuid;
	}
	public void setTweet_uuid(String tweet_uuid) {
		this.tweet_uuid = tweet_uuid;
	}
	public Long getTweet_timestamp() {
		return tweet_timestamp;
	}
	public void setTweet_timestamp(Long tweet_timestamp) {
		this.tweet_timestamp = tweet_timestamp;
	}
}
