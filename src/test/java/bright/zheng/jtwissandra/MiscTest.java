package bright.zheng.jtwissandra;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;
import me.prettyprint.cassandra.service.clock.MicrosecondsSyncClockResolution;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.ClockResolution;

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
	
	@Test
	public void testDuplicateUUID1(){
		Map<String, String> uuids = new HashMap<String, String>();
		for(int i=0; i<100; i++){
			UUID o = getUUID1();
			Assert.assertNotNull("UUID is null", o);
			String uuid = o.toString();
			Assert.assertNotNull("UUID string is null", uuid);
			logger.debug("No.{}, uuid={}", i, uuid);
			Assert.assertFalse("Duplicated UUID found!", uuids.containsKey(uuid));
			uuids.put(uuid, uuid);
		}
	}
	
	@Test
	public void testDuplicateUUID2(){
		Map<String, String> uuids = new HashMap<String, String>();
		ClockResolution clock = new MicrosecondsSyncClockResolution();
		for(int i=0; i<100; i++){
			UUID o = getUUID2(clock);
			Assert.assertNotNull("UUID is null", o);
			String uuid = o.toString();
			Assert.assertNotNull("UUID string is null", uuid);
			logger.debug("No.{}, uuid={}", i, uuid);
			Assert.assertFalse("Duplicated UUID found!", uuids.containsKey(uuid));
			uuids.put(uuid, uuid);
		}
	}
	
	private UUID getUUID1(){
		ClockResolution clock = new MicrosecondsSyncClockResolution();
		return TimeUUIDUtils.getTimeUUID(clock);
	}
	
	private UUID getUUID2(ClockResolution clock){
		return TimeUUIDUtils.getTimeUUID(clock);
	}
	
	/**
	 * TODO: How to get back the Date from TimeUUID in Hactor? 
	 * Still under discussion in user forum ...
	 */
	@Test
	public void getDateFromTimeUUID(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");
		UUID uuid = getUUID1();
		logger.debug("Date from uuid.timestamp() is:{}", sdf.format(new Date(uuid.timestamp())));
		logger.debug("Date2 from TimeUUIDUtils.getTimeFromUUID(uuid) is:{}", sdf.format(new Date(TimeUUIDUtils.getTimeFromUUID(uuid))));
		
		ClockResolution clock = new MicrosecondsSyncClockResolution();
		uuid = getUUID2(clock);
		logger.debug("Date from uuid.timestamp() is:{}", sdf.format(new Date(uuid.timestamp())));
		logger.debug("Date2 from TimeUUIDUtils.getTimeFromUUID(uuid) is:{}", sdf.format(new Date(TimeUUIDUtils.getTimeFromUUID(uuid))));		
	}
}
