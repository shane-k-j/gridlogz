package com.howtojboss.gridlogz.services.mapper;

import com.howtojboss.gridlogz.common.LogMessage;
import java.util.HashMap;
import java.util.Map;
import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.distexec.mapreduce.Mapper;

/**
 *
 * @author Shane K Johnson
 */
public class ClassNameMapper implements Mapper<String, LogMessage, String, Map<String, Integer>> {

    public void map(String key, LogMessage lm, Collector<String, Map<String, Integer>> collector) {
        
        Map<String, Integer> classNameCount = new HashMap<String, Integer>();
        
        classNameCount.put(lm.getSourceClassName(), 1);
        
        collector.emit(lm.getLoggerName(), classNameCount);
    }
}
