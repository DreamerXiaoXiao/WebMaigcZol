package com.baidu.spider;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;


public class BaiduSpider {

    private String getHtml(String url) {
        String responseBody = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity responseEntity = httpResponse.getEntity();
                responseBody = EntityUtils.toString(responseEntity, "utf-8");
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
        return responseBody;
    }

    private void parseIndexHttpClient() {
        String indexURL= "http://news.baidu.com/";
        String html = getHtml(indexURL);
        Document parse = Jsoup.parse(html);
        Elements liElements = parse.select("#pane-news > div > ul > li");
        for(Element liElement:liElements){
            Elements aElements = liElement.select("strong > a");
            for(Element aElement: aElements){
                System.out.println(aElement.text() + "\t" + aElement.attr("href"));
            }

        }
    }

    private void parseIndexJsoup() {
        try{
            Document parse = Jsoup.connect("http://news.baidu.com/").get();
            Elements liElements = parse.select("#pane-news > div > ul > li");
            for(Element liElement:liElements){
                Elements aElements = liElement.select("strong > a");
                for(Element aElement: aElements){
                    System.out.println(aElement.text() + "\t" + aElement.attr("href"));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void run(){
        parseIndexHttpClient();
        System.out.println("================================================");
        parseIndexJsoup();
    }
    public static void main(String[] args){
        new BaiduSpider().run();
    }

}
