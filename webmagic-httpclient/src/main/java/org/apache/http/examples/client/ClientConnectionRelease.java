package org.apache.http.examples.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sun.xml.internal.stream.Entity;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * This example demonstrates the recommended way of using API to make sure
 * the underlying connection gets released back to the connection manager.
 */
public class ClientConnectionRelease {

    public final static void main(String[] args) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet("http://httpbin.org/get");

            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());

                // Get hold of the response entity
                HttpEntity entity = response.getEntity();
                // If the response does not enclose an entity, there is no need
                // to bother about connection release
                if (entity != null) {
                    InputStream inStream = entity.getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
                    String line = null;
                    while((line=bufferedReader.readLine()) !=null ){
                        System.out.println(line);
                    }
//                    try {
//                        System.out.println(inStream.read());
//                        // do something useful with the response
//                    } catch (IOException ex) {
//                        // In case of an IOException the connection will be released
//                        // back to the connection manager automatically
//                        throw ex;
//                    } finally {
//                        // Closing the input stream will trigger connection release
//                        inStream.close();
//                    }
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}
