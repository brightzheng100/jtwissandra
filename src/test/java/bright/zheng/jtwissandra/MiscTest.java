package bright.zheng.jtwissandra;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import junit.framework.Assert;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bright.zheng.jtwissandra.service.BaseService;

/**
 * Other test cases if any
 * 
 * @author bright_zheng
 *
 */
public class MiscTest{
	Logger logger = LoggerFactory.getLogger(MiscTest.class);	
	
	/**
	 * Ref to:
	 * 	http://www.mail-archive.com/user@cassandra.apache.org/msg08417.html
	 */
	@Test
	public void uuid(){
		final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;

		UUID u1 = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
		final long t1 = u1.timestamp();

		long tmp = (t1 - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000;

		UUID u2 = TimeUUIDUtils.getTimeUUID(tmp);
		long t2 = u2.timestamp();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

		logger.debug("uuid 1={}", u1);
		logger.debug("uuid 2={}", u2);
		logger.debug("timestamp 1={}", t1);
		logger.debug("timestamp 2={}", t2);
		System.out.println(sdf.format(new Date(tmp)));
		Assert.assertTrue(u1.equals(u2));
		Assert.assertTrue(t1==t2);
	}
	
	@Test
	public void getTimestampFromUUID(){
		BaseService service = new BaseService();
		UUID uuid = service.getUUID();
		//UUID uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		logger.debug("The Date from UUID is: {}", 
				sdf.format(new Date(TimeUUIDUtils.getTimeFromUUID(uuid))));
	}
	
	@Test
    public void timeBasedType1UUID() throws InterruptedException {
		long previousUUID = 0L;
		BaseService service = new BaseService();
		for(int i=0; i<60*10; i++){
			UUID uuid = service.getUUID();
			long timestamp = uuid.timestamp();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
			logger.debug("UUID {} = {}; timestamp={}, date time={}", 
					new Object[]{
					i+1, 
					uuid.toString(), 
					uuid.timestamp(), 
					sdf.format(new Date(TimeUUIDUtils.getTimeFromUUID(uuid)))
					});
			Assert.assertTrue(timestamp>previousUUID);
			previousUUID = timestamp;
			Thread.sleep(1 * 1000);
		}
    }
}
