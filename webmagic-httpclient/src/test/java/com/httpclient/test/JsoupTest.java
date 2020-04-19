package com.httpclient.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

public class JsoupTest {

    @Test
    public void testGet() throws IOException {
        Document document = Jsoup.connect("http://www.baidu.com").get();
        System.out.println(document);
    }
}
