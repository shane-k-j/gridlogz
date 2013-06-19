package com.howtojboss.gridlogz.services.mapper;

import java.util.Iterator;
import org.infinispan.distexec.mapreduce.Reducer;

/**
 *
 * @author Shane K Johnson
 */
public class LoggerNameReducer implements Reducer<String, Integer> {

    public Integer reduce(String key, Iterator<Integer> iter) {
        
        Integer count = 0;
        
        while (iter.hasNext()) {
            
            Integer i = iter.next();
            
            count++;
        }
        
        return count;
    }
}
