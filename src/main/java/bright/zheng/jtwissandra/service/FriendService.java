package bright.zheng.jtwissandra.service;

import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.MutationResult;
import me.prettyprint.hector.api.mutation.Mutator;

/**
 * Friend service
 * 
 * @author bright_zheng
 *
 */
public class FriendService extends BaseService{
	
	/**
	 * Adding a friend has two business logic:
	 * 1. Add the friend's uuid to the Friend CF under my uuid
	 * 2. Add my uuid to the friend's uuid as follower
	 * 
	 * set FRIEND['550e8400-e29b-41d4-a716-446655440000']['1329836819859000']
	 * 	= '550e8400-e29b-41d4-a716-446655440001;
	 * 
	 * set FOLLOWER['550e8400-e29b-41d4-a716-446655440001']['1329836819859000'']
	 * 	= '550e8400-e29b-41d4-a716-446655440000;
	 * 
	 * @param me
	 * @param friend
	 */
    public MutationResult followFriend(String me, String friend) {
    	Mutator<String> mutator = HFactory.createMutator(
    			KEYSPACE, SERIALIZER_STRING);
    	
    	Long timestamp = this.generateTimestamp();
    	logger.debug("timestamp={}", timestamp);
        
        mutator.addInsertion(me, CF_FRIEND, 
        		this.createColumn(timestamp, friend));
        
        mutator.addInsertion(friend, CF_FOLLOWER, 
        		this.createColumn(timestamp, me));
        
        return mutator.execute();
    }   
    
}
