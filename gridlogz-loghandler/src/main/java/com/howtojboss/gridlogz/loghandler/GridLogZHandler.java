package com.howtojboss.gridlogz.loghandler;

import com.howtojboss.gridlogz.common.LogMessage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.logmanager.ExtLogRecord;

/**
 *
 * @author Shane K Johnson
 */
public class GridLogZHandler extends Handler {
    
    ObjectMapper mapper = new ObjectMapper();
    
    String serverName;
    
    String host;
    String port;

    @Override
    public void publish(LogRecord lr) {
        
        HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(params, 1 * 1000);
        HttpConnectionParams.setSoTimeout(params, 1 * 1000);
        
        HttpClient httpClient = new DefaultHttpClient(params);
        
        HttpPost httpPost = new HttpPost("http://" + host + ":" + port + "/gridlogz-services/application/services/logs");
        
        LogMessage lm = getLogMessage(lr);
        
        try {

            String json = mapper.writeValueAsString(lm);

            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

            httpPost.setEntity(entity);

            HttpResponse httpResponse = httpClient.execute(httpPost);

            int statusCode = httpResponse.getStatusLine().getStatusCode();
        } 
        catch (IOException ex) {

            System.err.println(ex);
        }
    }
    
    private String getFormattedMessage(LogRecord lr) {
        
        ExtLogRecord ext = ExtLogRecord.wrap(lr);
        
        String message = ext.getFormattedMessage();
        
        return message;
    }
    
    private LogMessage getLogMessage(LogRecord lr) {
        
        LogMessage lm = new LogMessage();
        
        String message = getFormattedMessage(lr);
        String thrown = getThrown(lr);
        
        if (lr.getLevel() != null)
            lm.setLevel(lr.getLevel().getLocalizedName());
        
        lm.setLoggerName(lr.getLoggerName());
        lm.setMessage(message);
        lm.setMillis(lr.getMillis());
        lm.setSequenceNumber(lr.getSequenceNumber());
        lm.setServerName(serverName);
        lm.setSourceClassName(lr.getSourceClassName());
        lm.setSourceMethodName(lr.getSourceMethodName());
        lm.setThreadID(lr.getThreadID());
        lm.setThrown(thrown);
        
        return lm;
    }
    
    private String getThrown(LogRecord lr) {
        
        Throwable throwable = lr.getThrown();
        
        if (throwable != null) {

            StringWriter sw = new StringWriter();

            PrintWriter pw = new PrintWriter(sw);

            throwable.printStackTrace(pw);

            pw.close();

            String thrown = sw.toString();

            return thrown;
        }
        
        return null;
    }

    // Handler
    @Override
    public void flush() {
        // N/A
    }

    @Override
    public void close() throws SecurityException {
        // N/A
    }

    // Setters
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
