package com.howtojboss.gridlogz.services.mapper;

import com.howtojboss.gridlogz.common.LogMessage;
import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.distexec.mapreduce.Mapper;

/**
 *
 * @author Shane K Johnson
 */
public class LoggerNameMapper implements Mapper<String, LogMessage, String, Integer> {

    public void map(String key, LogMessage lm, Collector<String, Integer> collector) {
        
        collector.emit(lm.getLoggerName(), 1);
    }
}
