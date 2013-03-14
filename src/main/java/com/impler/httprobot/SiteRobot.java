package com.impler.httprobot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.httprobot.model.BalanceRequest;
import com.impler.httprobot.model.BalanceResponse;
import com.impler.httprobot.model.BaseResponse;
import com.impler.httprobot.model.PushMessageRequest;
import com.impler.httprobot.model.PushMessageResponse;

public  class SiteRobot {
	
	private static final Logger log = LoggerFactory.getLogger(SiteRobot.class);
	
	/**
	 * 信息推送
	 * @param url
	 * @param msg
	 * @param key
	 * @return
	 */
	public static PushMessageResponse pushMessage(String url, PushMessageRequest msg, String key){
		Map<String, Object> param = bean2Map(msg);
		sign(param, msg, key);
		String xml = post(url, param);
		PushMessageResponse bean = new PushMessageResponse();
		xml2bean(xml, bean);
		return bean;
	}
	
	/**
	 * 查询余额
	 * @param url
	 * @param msg
	 * @param key
	 * @return
	 */
	public static BalanceResponse queryBalance(String url, BalanceRequest msg, String key){
		Map<String, Object> param = bean2Map(msg);
		sign(param, msg, key);
		String xml = get(url, param);
		BalanceResponse bean = new BalanceResponse();
		xml2bean(xml, bean);
		return bean;
	}
	
	/**
	 * get 请求
	 * @param url
	 * @param param
	 * @return
	 */
	public static String get(String url, Map<String,Object> param){
		try {
			StringBuilder sb = new StringBuilder(url);
			sb.append(sb.indexOf("?")>0 ? '&' : '?');
			if(param!=null&&param.size()>0){
				for(Map.Entry<String, Object> e : param.entrySet())
					sb.append(URLEncoder.encode(e.getKey(), "UTF-8"))
						.append('=')
						.append(URLEncoder.encode((String) e.getValue(), "UTF-8"))
						.append('&');
			}
			url = sb.toString();
			return toHTML(creep(new HttpGet(url)));
		} catch (ParseException e) {
			log.error(url+":"+param,e);
		} catch (ClientProtocolException e) {
			log.error(url+":"+param,e);
		} catch (IOException e) {
			log.error(url+":"+param,e);
		}
		return null;
	}
	
	/**
	 * post 请求
	 * @param url
	 * @param param
	 * @return
	 */
	public static String post(String url, Map<String,Object> param){
		try {
			HttpPost req = new HttpPost(url);
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			if(param!=null&&param.size()>0){
				for(Map.Entry<String, Object> e : param.entrySet())
					parameters.add(new BasicNameValuePair(e.getKey(),(String) e.getValue()));
			}
			try {
				req.setEntity(new UrlEncodedFormEntity(parameters));
			} catch (UnsupportedEncodingException e) {
				log.error(url+":"+param,e);
			}
			return toHTML(creep(req));
		} catch (ParseException e) {
			log.error(url+":"+param,e);
		} catch (ClientProtocolException e) {
			log.error(url+":"+param,e);
		} catch (IOException e) {
			log.error(url+":"+param,e);
		}
		return null;
	}
	
	/**
	 * 执行http请求
	 * @param req
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static HttpResponse creep(HttpRequestBase req) throws ClientProtocolException, IOException{
	    HttpClient client = ClientUtil.getHttpClient();
		HttpResponse resp = null;
		resp = client.execute(req);
		return resp;
	}
	
	/**
	 * 获取请求字串
	 * @param resp
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	private static String toHTML(HttpResponse resp) throws ParseException, IOException{
	    if(resp==null)return "";
        HttpEntity entity = resp.getEntity();
        if(entity==null)return "";
        String result = EntityUtils.toString(entity);
        EntityUtils.consume(entity);
        return result;
	}
	
	/**
	 * 产生签名
	 * @param param
	 * @param obj
	 * @param key
	 */
	private static void sign(Map<String,Object> param, Object obj, String key){
		param.put("verification", SignCoder.makeSign(param, key));
	}
	
	/**
	 * 请求bean转map
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String,Object> bean2Map(Object obj){
		Map<String, Object> param = null;
		try {
			param = BeanUtils.describe(obj);
			param.remove("class");
		} catch (IllegalAccessException e) {
			log.error("",e);
		} catch (InvocationTargetException e) {
			log.error("",e);
		} catch (NoSuchMethodException e) {
			log.error("",e);
		}
		if(param == null)
			throw new IllegalArgumentException("bean2Map had Error");
		return param;
	}
	
	/**
	 * xml结果解析成bean
	 * @param xml
	 * @param bean
	 */
	private static void xml2bean(String xml,BaseResponse bean){
		if(xml==null||xml.length()==0){
			bean.setCode("-2");
			bean.setInfo("未获取到xml");
			return;
		}
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> eles =  root.elements();
			for(Element e : eles){
				try{
					BeanUtils.setProperty(bean, e.getName(), e.getTextTrim());
				}catch(Exception ie){
					log.error(bean.getClass()+" has not "+e.getName(), ie);
				}
			}
		} catch (DocumentException e) {   
		    log.error("读取人员配置内容错误！"+xml, e);  
		    bean.setCode("-7");
		    bean.setInfo("xml解析出错");
		}catch(Exception e){
			log.error("解析错误"+xml, e);
			bean.setCode("-7");
			bean.setInfo("xml解析出错");
		}
	}
	
}