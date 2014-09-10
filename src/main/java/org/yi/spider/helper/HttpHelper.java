package org.yi.spider.helper;

import org.apache.http.impl.client.CloseableHttpClient;
import org.yi.spider.constants.GlobalConfig;
import org.yi.spider.utils.HttpUtils;

public class HttpHelper {

	public static String getContent(CloseableHttpClient client, String url, String charset) throws Exception {
		return HttpUtils.getContent(client, url, charset, GlobalConfig.USER_AGENT.getValue());
	}
	
}
