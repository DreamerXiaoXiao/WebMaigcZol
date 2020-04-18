package us.codecraft.webmagic.processor.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.MysqlPipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.6.0
 * <p>
 * 电视类目==》品牌列表==》商品列表==》详情页
 */
public class ZolPageProcessor implements PageProcessor {
    // 主页URL
    private static final String INDEX_URL = "http://detail.zol.com.cn";
    //类目
    private static final String CATE = "电视";
    //类目URL
    private static final String CATE_URL = "http://detail.zol.com.cn/digital_tv/$";
    // 品牌URL
    private static final String BRAND_URL = "http://detail.zol.com.cn/digital_tv/.+/$";
    //列表页URL
    private static final String LIST_URL = "http://detail.zol.com.cn/digital_tv/.+/.+html$";
    //详情页URL
    private static final String DETAIL_URL = "http://detail.zol.com.cn/.+/.+/param.shtml$";

    //品牌Map，传递数据
    private Map<String, Map<String, String>> brandItemMap = new HashMap<String, Map<String, String>>();
    //类表Map,传递数据
    private Map<String, Map<String, String>> listItemMap = new HashMap<String, Map<String, String>>();

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    /**
     * 解析类目
     */
    private void parseCate(Page page) {
        List<String> aStringList = page.getHtml().xpath("//div[@id='J_ParamBrand']/a").all();
        for (String aString : aStringList) {
            Html aSelector = new Html(aString);
            Map<String, String> itemMap = new HashMap<String, String>();
            itemMap.put("cate", CATE);
            itemMap.put("brand", aSelector.xpath("//a/text()").toString());
            String brandUrl = INDEX_URL + aSelector.xpath("//a/@href").toString();
            brandItemMap.put(brandUrl, itemMap);
            page.addTargetRequest(brandUrl);
        }
        System.out.println(brandItemMap);
    }

    /**
     * 解析列表
     */
    private void parseList(Page page) {
        String currentUrl = String.valueOf(page.getUrl());

        List<String> liStringList = page.getHtml().xpath("//*[@id='J_PicMode']/li").all();
        for (String liString : liStringList) {

            Map<String, String> itemMap = new HashMap<String, String>();
            //添加传递的属性
            Map<String, String> brandItem = brandItemMap.get(currentUrl);
            for(String key: brandItem.keySet()){
                itemMap.put(key, brandItem.get(key));
            }

            // 解析
            Html aSelector = new Html(liString);
            String title = aSelector.xpath("//h3/a/@title").toString();
            itemMap.put("title", title);
            itemMap.put("price", aSelector.xpath("//*[@class='price-type']/text()").toString());
            String detailUrl = aSelector.xpath("//a[@class='comment-num']/@href").toString();

            if (detailUrl != null){
                String detailUrlFinal = INDEX_URL + detailUrl.replace("review", "param");
                //传递参数
                listItemMap.put(detailUrlFinal, itemMap);
                //添加详情页
                page.addTargetRequest(detailUrlFinal);
            }

        }
        // 列表页
        String listUrl = page.getHtml().xpath("//*[@class='next']/@href").toString();
        if (listUrl != null){
            String nextListUrl = INDEX_URL + listUrl;
            brandItemMap.put(nextListUrl, brandItemMap.get(currentUrl));
            page.addTargetRequest(nextListUrl);
        }
    }

    /**
     * 解析详情
     */
    private void parseDetail(Page page){
        String currentUrl = String.valueOf(page.getUrl());
        //添加传递的属性
        Map<String, String> listItem = listItemMap.get(currentUrl);
        for(String key: listItem.keySet()){
            page.putField(key, listItem.get(key));
        }

        page.putField("size", page.getHtml().xpath("//span[@id='newPmVal_1']/a/text()").toString());
        page.putField("resolution_ratio", page.getHtml().xpath("//span[@id='newPmVal_2']/a/text()").toString());
        page.putField("url", currentUrl);
        String brand = page.getResultItems().getAll().get("brand").toString();
        String model = page.getHtml().xpath("//*[@class='product-model__name']/text()").toString();
        page.putField("model", model.replace(brand, "").replace("参数", ""));
    }

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(CATE_URL).match()) {
            parseCate(page);
            page.setSkip(true);

        } else if (page.getUrl().regex(BRAND_URL).match() || page.getUrl().regex(LIST_URL).match()) {
            parseList(page);
            page.setSkip(true);
        }else if (page.getUrl().regex(DETAIL_URL).match()){
            parseDetail(page);
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider spider = Spider.create(new ZolPageProcessor());
//        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(
//                new Proxy("27.43.191.64",9999)
//                ,new Proxy("102.102.102.102",8888)));
//        spider.setDownloader(httpClientDownloader);
        spider.thread(6).addPipeline(new MysqlPipeline()).addUrl("http://detail.zol.com.cn/digital_tv/").run();
    }
}
