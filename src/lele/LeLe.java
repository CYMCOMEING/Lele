package lele;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeLe {
    private static String url = "http://retoys.net/";

    private HttpGetConnect connect = new HttpGetConnect();

    /**
     * 获取"http://retoys.net/主页面的pickup4个连接
     * 已经弃用
     * @return
     */
    public List<String> getPickUpUrl() {
        List<String> list = new ArrayList<String>();

        try {
            connect.connect(url, "utf-8", new HttpGetConnect.ICallBack() {
                @Override
                public void onSuccess(String html) {
                    Document doc = Jsoup.parse(html);
                    Elements eles = doc.getElementsByClass("columns--grid2 columns--grid2--right").get(0).children();
                    for (Element item : eles) {
                        Element a = item.getElementsByTag("a").first();
                        if (a != null) {
                            list.add(a.attr("href"));
                        }
                    }
                }

                @Override
                public void onFail(int retCode) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllPickupUrl(){
        String url = "http://retoys.net/pickup/";
        List<String> list = new ArrayList<>();

        try {
            connect.connect(url, "utf-8", new HttpGetConnect.ICallBack() {
                @Override
                public void onSuccess(String html) {
                    Document doc = Jsoup.parse(html);

                    // 获取第一个row下的所有子项
                    Elements rowClass = doc.getElementsByClass("row").first().children();
                    for (Element row : rowClass){
                        list.add(row.getElementsByTag("a").attr("href"));
                    }
                }

                @Override
                public void onFail(int retCode) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


        return list;
    }

    /**
     *  获取pickup作品下的图片链接
     * @param url
     * @return
     */
    public List<String> getPictureUrls(String url) {
        List<String> list = new ArrayList<String>();

        try {
            connect.connect(url, "utf-8", new HttpGetConnect.ICallBack() {
                @Override
                public void onSuccess(String html) {
                    Document doc = Jsoup.parse(html);
                    // 获取标题
                    // 标题会带有/的字符，用-代替
                    list.add(formatString(doc.getElementsByClass("entry__title").first().text()));
                    // 获取图片url
                    Elements classImgs = doc.getElementsByClass("img");
                    for (Element classImg : classImgs) {
                        Elements tabImgs = classImg.getElementsByTag("img");
                        for (Element tabImg : tabImgs) {
                            String string = tabImg.attr("data-src").trim();
                            if (!"".equals(string)){
                                list.add(string);
                            }

                        }
                    }
                }
                @Override
                public void onFail(int retCode) {}
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String formatString(String str){
        String ret = str.replace("/", "-");
        return ret.replace("\\", "-");
    }

    public static void main(String[] args){
    }

}
