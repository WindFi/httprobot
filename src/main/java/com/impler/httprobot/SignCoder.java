package com.impler.httprobot;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 签名制造
 * @author  Invalid
 * @date 2012-3-14 下午5:18:47
 */
public class SignCoder {
	
	/**
	 * 验证签名
	 * @param md5
	 * @param params
	 * @param key
	 * @return
	 * @author  Invalid
	 * @date 2012-3-15 上午11:52:18
	 */
	public static boolean validateSign(String md5, Map<String,String[]> params, String key){
		return makeSign(params, key).equals(md5);
	}
	
	/**
	 * 生成签名
	 * @param params
	 * @param key
	 * @return
	 * @author  Invalid
	 * @date 2012-3-15 上午11:49:23
	 */
	public static String makeSign(Map<String,String[]> params, String key){
		if(params instanceof TreeMap)
			return makeSign((TreeMap<String,String[]>)params,key);
		else
			return makeSign(new TreeMap<String,String[]>(params),key);
	}
	
	/**
	 * 生成签名
	 * @param params
	 * @param key
	 * @return
	 * @author  Invalid
	 * @date 2012-3-15 上午11:49:36
	 */
	public static String makeSign(TreeMap<String,String[]> params, String key){
		StringBuilder sb = new StringBuilder();
		for(Entry<String,String[]> entry : params.entrySet()){
			if(entry.getValue()==null)continue;
			if(entry.getValue().length==1)
				sb.append(entry.getKey()).append(entry.getValue()[0]);
			else
				addValues(sb, entry.getKey(), entry.getValue());
		}
		sb.append(key);
		return KeyedMD5.getMd5Utf8(sb.toString(), "");
	}
	
	/**
	 * 数组排序
	 * @param sb
	 * @param name
	 * @param values
	 * @author  Invalid
	 * @date 2012-3-15 上午11:49:50
	 */
	private static void addValues(StringBuilder sb, String name, String[] values){
		Arrays.sort(values);
		for(String val : values){
			sb.append(name).append(val);
		}
	}

}

