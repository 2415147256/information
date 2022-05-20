package com.hd123.baas.sop.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author zhangjiahao
 */
public class HttpClientUtil {
    public static String doPostJSON(String url, String json) {
        return doPostJSON(url, json, null);
    }

    public static String doPostJSON(String url, String json, Map<String, String> headers) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(10000).build();
            // 为httpPost实例设置配置
            httpPost.setConfig(requestConfig);

            // 设置请求头
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            if (headers != null && headers.size() > 0) {
                for (String key:headers.keySet()) {
                    httpPost.addHeader(key, headers.get(key));
                }
            }

            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(json, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            httpPost.setEntity(stringEntity);

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response)
                        throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            return httpClient.execute(httpPost, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
