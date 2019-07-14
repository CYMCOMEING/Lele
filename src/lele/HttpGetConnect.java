package lele;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpGetConnect {

    interface ICallBack {
        void onSuccess(String html);

        void onFail(int retCode);
    }

    /**
     * 获取html内容
     *
     * @param url
     * @param charsetName UTF-8、GB2312
     * @throws IOException
     */
    public static void connect(String url, String charsetName, ICallBack iCallBack) throws IOException {
        BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .build();
        String content = "";
        int timeout = 10000;

        try {
            HttpGet httpget = new HttpGet(url);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .build();
            httpget.setConfig(requestConfig);
            httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpget.setHeader("Accept-Encoding", "gzip,deflate,sdch");
            httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpget.setHeader("Connection", "keep-alive");
            httpget.setHeader("Upgrade-Insecure-Requests", "1");
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
            //httpget.setHeader("Hosts", "www.oschina.net");
            httpget.setHeader("cache-control", "max-age=0");

            CloseableHttpResponse response = httpclient.execute(httpget);

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {

                HttpEntity entity = response.getEntity();
                InputStream instream = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(instream, charsetName));
                StringBuffer sbf = new StringBuffer();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sbf.append(line + "\n");
                }

                br.close();
                if (iCallBack != null) {
                    iCallBack.onSuccess(sbf.toString());
                }
            } else {
                if (iCallBack != null) {
                    iCallBack.onFail(status);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
    }
}