package com.howtojboss.gridlogz.services;

import com.howtojboss.gridlogz.common.LogMessage;
import com.howtojboss.gridlogz.services.distexec.LastCallable;
import com.howtojboss.gridlogz.services.mapper.ClassNameMapper;
import com.howtojboss.gridlogz.services.mapper.ClassNameReducer;
import com.howtojboss.gridlogz.services.mapper.LevelNameMapper;
import com.howtojboss.gridlogz.services.mapper.LevelNameReducer;
import com.howtojboss.gridlogz.services.mapper.LoggerNameMapper;
import com.howtojboss.gridlogz.services.mapper.LoggerNameReducer;
import com.howtojboss.gridlogz.services.mapper.ServerNameMapper;
import com.howtojboss.gridlogz.services.mapper.ServerNameReducer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.infinispan.AdvancedCache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.distexec.mapreduce.MapReduceTask;
import org.infinispan.stats.Stats;

/**
 *
 * @author Shane K Johnson
 */
@Stateless
public class CacheBean {
    
    @Inject
    @MessageCache
    AdvancedCache<String, LogMessage> cache;
    
    public void log(LogMessage lm) {
        
        String serverName = lm.getServerName();
        
        long sequenceNumber = lm.getSequenceNumber();
        
        String seqNumStr = String.valueOf(sequenceNumber);
        
        String key = serverName.concat(seqNumStr);
        
        cache.put(key, lm);
    }
    
    public Map<String, Map<String, Integer>> countGroupByClassName() {
        
        MapReduceTask<String, LogMessage, String, Map<String, Integer>> task = 
                new MapReduceTask<String, LogMessage, String, Map<String, Integer>>(cache);
        
        task.mappedWith(new ClassNameMapper()).reducedWith(new ClassNameReducer());
        
        Map<String, Map<String, Integer>> countGroupByClass = task.execute();
        
        return countGroupByClass;
    }
    
    public Map<String, Integer> countGroupByLevelName() {
        
        MapReduceTask<String, LogMessage, String, Integer> task = 
                new MapReduceTask<String, LogMessage, String, Integer>(cache);
        
        task.mappedWith(new LevelNameMapper()).reducedWith(new LevelNameReducer());
        
        Map<String, Integer> countGroupByLevel = task.execute();
        
        return countGroupByLevel;
    }
    
    public Map<String, Integer> countGroupByLoggerName() {
        
        MapReduceTask<String, LogMessage, String, Integer> task = 
                new MapReduceTask<String, LogMessage, String, Integer>(cache);
        
        task.mappedWith(new LoggerNameMapper()).reducedWith(new LoggerNameReducer());
        
        Map<String, Integer> countGroupByLoggerName = task.execute();
        
        return countGroupByLoggerName;
    }
    
    public Map<String, Integer> countGroupByServerName() {
        
        MapReduceTask<String, LogMessage, String, Integer> task = 
                new MapReduceTask<String, LogMessage, String, Integer>(cache);
        
        task.mappedWith(new ServerNameMapper()).reducedWith(new ServerNameReducer());
        
        Map<String, Integer> countGroupByServerName = task.execute();
        
        return countGroupByServerName;
    }
    
    public List<LogMessage> since(Long millis) {
        
        List<LogMessage> logsSince = new ArrayList<LogMessage>();
        
        DistributedExecutorService des = new DefaultExecutorService(cache);
        
        LastCallable callable = new LastCallable(millis);

        List<Future<List<LogMessage>>> results = des.submitEverywhere(callable);
        
        for (Future<List<LogMessage>> future: results) {
            
            try {
            
                List<LogMessage> logs = future.get();
                
                logsSince.addAll(logs);
            }
            catch (InterruptedException ex) {
                
                ex.printStackTrace(System.err);
            }
            catch (ExecutionException ex) {
                
                ex.printStackTrace(System.err);
            }
        }
        
        Collections.sort(logsSince, new Comparator<LogMessage>() {
            
            public int compare(LogMessage lm1, LogMessage lm2) {
                
                return Long.valueOf(lm1.getMillis()).compareTo(Long.valueOf(lm2.getMillis()));
                //return Long.compare(lm1.getMillis(), lm2.getMillis());
            }
        });
        
        return logsSince;
    }
    
    public Stats getStats() {
        
        return cache.getStats();
    }
}
