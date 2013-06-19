package com.howtojboss.gridlogz.services;

import com.howtojboss.gridlogz.common.LogMessage;
import com.howtojboss.gridlogz.services.model.NestedResult;
import com.howtojboss.gridlogz.services.model.Result;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.infinispan.stats.Stats;

/**
 *
 * @author Shane K Johnson
 */
@Path("/services")
public class ApplicationServices {
    
    @EJB
    CacheBean cacheBean;
    
    @POST
    @Consumes("application/json")
    @Path("/logs")
    public Response log(LogMessage lm) {
        
        cacheBean.log(lm);
        
        return Response.ok().build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/logs/count/group/by/name/class")
    public List<NestedResult> countGroupByClassName() {
        
        Map<String, Map<String, Integer>> entries = cacheBean.countGroupByClassName();
        
        List<NestedResult> nestedResults = new ArrayList<NestedResult>();
        
        for (Entry<String, Map<String, Integer>> e1: entries.entrySet()) {
            
            NestedResult nestedResult = new NestedResult();
            
            nestedResult.setKey(e1.getKey());
            
            List<Result> results = new ArrayList<Result>();
            
            for (Entry<String, Integer> e2: e1.getValue().entrySet()) {
                
                Result result = new Result();
                
                result.setKey(e2.getKey());
                result.setValue(e2.getValue());
                
                results.add(result);
            }
            
            nestedResult.setValues(results);
            
            nestedResults.add(nestedResult);
        }
        
        return nestedResults;
    }
    
    @GET
    @Produces("application/json")
    @Path("/logs/count/group/by/name/level")
    public List<Result> countGroupByLevelName() {
        
        Map<String, Integer> entries = cacheBean.countGroupByLevelName();
        
        List<Result> results = getObjects(entries);
        
        return results;
    }
    
    @GET
    @Produces("application/json")
    @Path("/logs/count/group/by/name/logger")
    public List<Result> countGroupByLoggerName() {
        
        Map<String, Integer> entries = cacheBean.countGroupByLoggerName();
        
        List<Result> results = getObjects(entries);
        
        return results;
    }
    
    @GET
    @Produces("application/json")
    @Path("/logs/count/group/by/name/server")
    public List<Result> countGroupByServerName() {
        
        Map<String, Integer> entries = cacheBean.countGroupByServerName();
        
        List<Result> results = getObjects(entries);
        
        return results;
    }
    
    @GET
    @Produces("application/json")
    @Path("/logs/last/{num}/minutes")
    public List<LogMessage> since(@PathParam("num") Integer minutes) {
        
        long now = System.currentTimeMillis();
        
        long since = now - (minutes * 60 * 1000);
        
        return cacheBean.since(since);
    }
    
    @GET
    @Produces("application/json")
    @Path("/logs/stats")
    public Stats stats() {
        
        return cacheBean.getStats();
    }
    
    private List<Result> getObjects(Map<String, Integer> entries) {
        
        List<Result> results = new ArrayList<Result>();
        
        for (Entry<String, Integer> entry: entries.entrySet()) {
            
            Result result = new Result();
            
            result.setKey(entry.getKey());
            result.setValue(entry.getValue());
            
            results.add(result);
        }
        
        Collections.sort(results, new Comparator<Result>() {

            public int compare(Result r1, Result r2) {
                
                return r1.getKey().compareTo(r2.getKey());
            }
        });
        
        return results;
    }
}
