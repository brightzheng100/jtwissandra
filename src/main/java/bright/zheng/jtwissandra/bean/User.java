package bright.zheng.jtwissandra.bean;

/**
 * User
 * 
 * @author bright_zheng
 *
 */
public class User implements java.io.Serializable{
	
	private static final long serialVersionUID = 3445749928130725546L;
	
	private String user_id;
	private String user_name;
	private String user_password;
	private String create_timestamp;
	
	public User(){}
	
	public User(String user_name, String user_password){
		this.user_name = user_name;
		this.user_password = user_password;
	}
	
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_password() {
		return user_password;
	}
	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
	public String getCreate_timestamp() {
		return create_timestamp;
	}
	public void setCreate_timestamp(String create_timestamp) {
		this.create_timestamp = create_timestamp;
	}	
}
