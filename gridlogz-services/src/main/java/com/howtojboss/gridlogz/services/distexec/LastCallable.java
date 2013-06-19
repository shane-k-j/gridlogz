package com.howtojboss.gridlogz.services.distexec;

import com.howtojboss.gridlogz.common.LogMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import org.infinispan.Cache;
import org.infinispan.cdi.Input;

/**
 *
 * @author Shane K Johnson
 */
public class LastCallable implements Callable<List<LogMessage>>, Serializable {

    Long millis;
    
    @Inject
    @Input
    private Cache<Integer, LogMessage> cache;
    
    public LastCallable(Long millis) {
        
        this.millis = millis;
    }

    @Override
    public List<LogMessage> call() throws Exception {
        
        List<LogMessage> logs = new ArrayList<LogMessage>();
        
        for (LogMessage lm: cache.values()) {
            
            if (lm.getMillis() > millis)
                logs.add(lm);
        }
        
        return logs;
    }
}
