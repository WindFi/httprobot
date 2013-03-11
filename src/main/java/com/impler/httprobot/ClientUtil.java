package com.impler.httprobot;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public abstract class ClientUtil {

	private static HttpParams httpParams;  
    private static PoolingClientConnectionManager connectionManager;  
    private static DefaultHttpClient client;
  
    /** 
     * 最大连接数 
     */  
    public final static int MAX_TOTAL_CONNECTIONS = 800;  
    /** 
     * 每个路由最大连接数 
     */  
    public final static int MAX_ROUTE_CONNECTIONS = 400;  
    /** 
     * 连接超时时间 
     */  
    public final static int CONNECT_TIMEOUT = 10000;  
    /** 
     * 读取超时时间 
     */  
    public final static int READ_TIMEOUT = 10000;  
  
    static {  
        httpParams = new BasicHttpParams();  
        // 设置连接超时时间  
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);  
        // 设置读取超时时间  
        HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);  
  
        SchemeRegistry registry = new SchemeRegistry();  
        registry.register(new Scheme("http",  80, PlainSocketFactory.getSocketFactory() ));
        registry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory() ));  
  
        connectionManager = new PoolingClientConnectionManager(registry);  
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);// 设置最大连接数  
		connectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);// 设置每个路由最大连接数  
    }  
  
    public static HttpClient getHttpClient() {  
    	if(client==null){
    		synchronized (ClientUtil.class) {
    			if(client==null)
            		client = new DefaultHttpClient(connectionManager, httpParams);
			}
    	}
        return client;  
    }  
}
