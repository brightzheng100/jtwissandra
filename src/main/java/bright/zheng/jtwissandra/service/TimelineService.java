package bright.zheng.jtwissandra.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import bright.zheng.jtwissandra.bean.Timeline;

/**
 * Timeline service
 * 
 * @author bright_zheng
 *
 */
public class TimelineService extends BaseService{
	
	/**
	 * get specified user's first Timeline
	 * 
	 * @param user_uuid
	 * @return
	 */
	public TimelineWrapper getTimeline(String user_uuid){
		return getTimeline(user_uuid, 0L, TWEETS_LIMIT_DEFAULT);
	}
	
	/**
	 * get specified user's Timeline with start point
	 * 
	 * @param user_uuid
	 * @param start
	 * @return
	 */
	public TimelineWrapper getTimeline(String user_uuid, long start){
		return getTimeline(user_uuid, start, TWEETS_LIMIT_DEFAULT);
	}
	
	/**
	 * get specified user's Timeline with start point and limit
	 * 
	 * @param user_uuid
	 * @param start
	 * @param limit
	 * @return
	 */
    public TimelineWrapper getTimeline(String user_uuid, long start, int limit){
    	if (start<0) start = 0;
    	if (limit<0) limit = TWEETS_LIMIT_DEFAULT;
    	if (limit>TWEETS_LIMIT_MAX) limit = TWEETS_LIMIT_MAX;
    	
    	SliceQuery<String, Long, String> sliceQuery = 
            HFactory.createSliceQuery(KEYSPACE, SERIALIZER_STRING, SERIALIZER_LONG, SERIALIZER_STRING);        
        sliceQuery.setColumnFamily(CF_TIMELINE);
        sliceQuery.setKey(user_uuid);
        sliceQuery.setRange(start, Long.MAX_VALUE, false, limit+1);
        QueryResult<ColumnSlice<Long, String>> result = sliceQuery.execute();
        List<HColumn<Long, String>> list = result.get().getColumns();
        
        long next = 0L;
        if(list==null){
        	return new TimelineWrapper(null, next);
        }else if (list.size()<=limit){        	
        	return new TimelineWrapper(convertToTimeline(list), 0L);
        }else{
        	HColumn<Long,String> last = list.get(list.size()-1);
        	next = last.getName(); //the name is the timestamp as the "next" start
        	list.remove(list.size()-1);
        	
        	return new TimelineWrapper(convertToTimeline(list), next);
        }
    }
    
    private List<Timeline> convertToTimeline(List<HColumn<Long,String>> cols){
    	Iterator<HColumn<Long,String>> it = cols.iterator();
    	List<Timeline> result = new ArrayList<Timeline>();
    	while(it.hasNext()){
    		HColumn<Long,String> col = it.next();
    		result.add(new Timeline(col.getValue(), col.getName()));
    	}
    	return result;
    }
    
    public class TimelineWrapper{
    	private List<Timeline> timelines;
    	private long nextTimeline;
    	
    	public TimelineWrapper(List<Timeline> timelines, long nextTimeline){
    		this.timelines = timelines;
    		this.nextTimeline = nextTimeline;
    	}

		public long getNextTimeline() {
			return nextTimeline;
		}

		public List<Timeline> getTimelines() {
			return timelines;
		}
    	
    }
}
