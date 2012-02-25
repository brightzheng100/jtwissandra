package bright.zheng.jtwissandra.service;

import java.util.UUID;

import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import bright.zheng.jtwissandra.bean.User;

/**
 * User service
 * 
 * @author bright_zheng
 *
 */
public class UserService extends BaseService{
	
	/**
	 * Sample CLI cmd:
	 * set USER['550e8400-e29b-41d4-a716-446655440000']['user_name'] = 'itstarting';
	 * set USER['550e8400-e29b-41d4-a716-446655440000']['password'] = '111222';
	 * set USER['550e8400-e29b-41d4-a716-446655440000']['create_timestamp'] = 1329836819890000;
	 * 
	 * @param user
	 */
    public String addUser(User user) {
    	Mutator<String> mutator = HFactory.createMutator(
    			KEYSPACE, SERIALIZER_STRING);
    	UUID uuid = this.getUUID();
    	String user_uuid = uuid.toString();   
    	Long create_timestamp = this.getTimestamp(uuid);
    	logger.debug("user_uuid={}", user_uuid);  
    	logger.debug("user_name={}", user.getUser_name());
    	logger.debug("password={}", user.getUser_password());
    	logger.debug("create_timestamp={}", create_timestamp);
        
        mutator.addInsertion(user_uuid, CF_USER, 
        		this.createColumn("user_name", user.getUser_name()));
        mutator.addInsertion(user_uuid, CF_USER, 
        		this.createColumn("password", user.getUser_password()));
        mutator.addInsertion(user_uuid, CF_USER, 
        		this.createColumn("create_timestamp", create_timestamp));
        
        mutator.execute();
        
        //return the generated uuid
        return user_uuid;
    }   
    
}
