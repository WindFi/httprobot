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
	public static boolean validateSign(String md5, Map<String,Object> params, String key){
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
	public static String makeSign(Map<String,Object> params, String key){
		if(params instanceof TreeMap)
			return makeSign((TreeMap<String,Object>)params,key);
		else
			return makeSign(new TreeMap<String,Object>(params),key);
	}
	
	/**
	 * 生成签名
	 * @param params
	 * @param key
	 * @return
	 * @author  Invalid
	 * @date 2012-3-15 上午11:49:36
	 */
	public static String makeSign(TreeMap<String,Object> params, String key){
		StringBuilder sb = new StringBuilder();
		Object val = null;
		for(Entry<String,Object> entry : params.entrySet()){
			val = entry.getValue();
			if(val==null)continue;
			if(val.getClass().isArray()){
				Object[] vals = (Object[]) val;
				if(vals.length==1){
					sb.append(entry.getKey()).append(vals[0]);
				}else{
					addValues(sb, entry.getKey(), vals);
				}
			}else{
				sb.append(entry.getKey()).append(val);
			}
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
	private static void addValues(StringBuilder sb, String name, Object[] values){
		Arrays.sort(values);
		for(Object val : values){
			sb.append(name).append(val);
		}
	}

}

