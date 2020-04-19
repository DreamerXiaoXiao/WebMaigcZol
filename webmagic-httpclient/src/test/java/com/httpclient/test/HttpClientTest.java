package com.httpclient.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpClientTest {

    /**
     * 测试发送get请求
     */
    @Test
    public void testHttpGet() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://www.baidu.com");
        httpGet.setHeader("cookie", "cookiekey=cookieval;cookiekey=cookieval");
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity responseEntity = httpResponse.getEntity();
                String responseBody = EntityUtils.toString(responseEntity, "utf-8");
                System.out.println(responseBody);
                System.out.println(Arrays.toString(httpResponse.getAllHeaders()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试abort方法
     */
    @Test
    public void testHttpGetAbort() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://www.baidu.com");
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity responseEntity = httpResponse.getEntity();
                String responseBody = EntityUtils.toString(responseEntity, "utf-8");
                System.out.println(responseBody);
            } else {
                httpGet.abort();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试认证URL
     */
    @Test
    public void testHttpGetAuthentication() {
        // 设置认证信息
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(new AuthScope("httpbin.org", 80),
                new UsernamePasswordCredentials("user", "passwd"));

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build();

        HttpGet httpGet = new HttpGet("http://httpbin.org/basic-auth/user/passwd");
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity responseEntity = httpResponse.getEntity();
                String responseBody = EntityUtils.toString(responseEntity, "utf-8");
                System.out.println(responseBody);
                JSONObject parse = JSONObject.parseObject(responseBody);
                System.out.println(parse);
                System.out.println(parse.getBooleanValue("authenticated"));
                System.out.println(parse.getString("user"));


            } else {
                httpGet.abort();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 测试发送get请求
     */
    @Test
    public void testHttpGetProxy() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://www.baidu.com");

        HttpHost proxy = new HttpHost("123.139.56.238", 9999, "http");
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        httpGet.setConfig(requestConfig);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity responseEntity = httpResponse.getEntity();
                String responseBody = EntityUtils.toString(responseEntity, "utf-8");
                System.out.println(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testPost() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://httpbin.org/post");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("username", "jinpeng"));
        parameters.add(new BasicNameValuePair("password", "nihao"));

        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            httpPost.setEntity(formEntity);

            //5.发送请求
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    String string = EntityUtils.toString(entity, "utf-8");
                    System.out.println(string);
                    System.out.println(Arrays.toString(response.getAllHeaders()));
                }
            } finally {
                response.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //6.关闭资源
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Test
    public void testGetCookies() {

        CookieStore httpCookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(httpCookieStore).build();
        HttpGet httpGet = new HttpGet("http://stackoverflow.com/");
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity responseEntity = httpResponse.getEntity();
                String responseBody = EntityUtils.toString(responseEntity, "utf-8");
                System.out.println("content:" + responseBody);
                System.out.println();
                System.out.println("headers:" + Arrays.toString(httpResponse.getAllHeaders()));
                System.out.println();
                List<Cookie> cookies = httpCookieStore.getCookies();
                for(Cookie cookie: cookies){
                    System.out.println(cookie);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
