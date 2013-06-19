package com.howtojboss.gridlogz.services.mapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.infinispan.distexec.mapreduce.Reducer;

/**
 *
 * @author Shane K Johnson
 */
public class ClassNameReducer implements Reducer<String, Map<String, Integer>> {

    public Map<String, Integer> reduce(String key, Iterator<Map<String, Integer>> iter) {
        
        Map<String, Integer> classNameCounts = new HashMap<String, Integer>();
        
        while (iter.hasNext()) {
            
            Map<String, Integer> classNameCount = iter.next();
            
            String className = classNameCount.keySet().iterator().next();
            
            if (classNameCounts.containsKey(className)) {
                
                Integer count = classNameCounts.get(className);
                
                count++;
                
                classNameCounts.put(className, count);
            }
            else
                classNameCounts.put(className, 1);
        }
        
        return classNameCounts;
    }
}
