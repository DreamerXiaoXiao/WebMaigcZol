package us.codecraft.webmagic.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Store results in files.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class FilePipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private PrintWriter printWriter = null;

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public FilePipeline() {
        setPath("data/output.txt");
    }

    public FilePipeline(String path) {
        setPath(path);
        try {
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFile(path)),"UTF-8"));
            printWriter.println("cate\tbrand\tmodel\tprice\tsize\tresolution_ratio\turl");
        }catch (IOException e){
            logger.warn("write file error", e);
        }

    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Object cate = resultItems.getAll().get("cate");
        Object brand = resultItems.getAll().get("brand");
        Object model = resultItems.getAll().get("model");
        Object price = resultItems.getAll().get("price");
        Object size = resultItems.getAll().get("size");
        Object resolution_ratio = resultItems.getAll().get("resolution_ratio");
        Object url = resultItems.getAll().get("url").toString();
        printWriter.println(cate + "\t" + brand + "\t" + model + "\t" + price + "\t" + size + "\t" + resolution_ratio + "\t" + url);
        System.out.println(url + "写入成功！");
    }

    @Override
    public void closeSpider() {
        if (printWriter != null){
            printWriter.close();
        }
    }
}
