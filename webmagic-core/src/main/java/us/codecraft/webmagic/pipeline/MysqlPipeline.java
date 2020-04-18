package us.codecraft.webmagic.pipeline;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.sql.*;
import java.util.*;

/**
 * Store results in files.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class MysqlPipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Connection con = null;

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public MysqlPipeline(){
        //1.加载驱动
        String url = "jdbc:mysql://127.0.0.1:3306/javaspider?useSSL=true&useUnicode=true&characterEncoding=UTF-8";
        String user = "root";
        String pwd = "jinpeng";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user,pwd);
            logger.info("Mysql连接初始化成功！！！");
        } catch (Exception e) {
            logger.warn("load class error", e);
        }

    }

    /**
     * MySQL数据库插入和更新数据
     * @param tableName 表名
     * @param dataMap Map数据
     * @return 更新数据个数
     */
    private int insertUpdateData(String tableName, Map dataMap){

        int size = dataMap.keySet().size();
        String keysString = StringUtils.join(dataMap.keySet(), ",");
        String[] valueList = new String[size];
        Arrays.fill(valueList, "?");
        String valuesString = StringUtils.join(valueList, ",");

        List<Object> keyValueList = new ArrayList<Object>();
        for (Object key: dataMap.keySet()){
            keyValueList.add(key + "=" + "?");
        }
        String keyValueString = StringUtils.join(keyValueList, ",");
        String sqlTemplate = "INSERT INTO %s (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s;";

        String sql = String.format(sqlTemplate, tableName, keysString, valuesString, keyValueString);
        int i = 0;
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            int j = 1;
            for(int k=0; k<2;k++){
                for(Object value: dataMap.values()){
                    ps.setObject(j, value);
                    j++;
                }
            }
            i = ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                logger.warn("rollback", e);
            }
        }
        return i;

    }

    public static void main(String[] args){
        Map<String, String> map = new HashMap<String, String>();
        map.put("cate", "电视");
        map.put("price", "7999");
        map.put("title", "海信HZ65U7E");
        map.put("size", "65英寸");
        map.put("resolution_ratio", "4K（3840*2160）");
        map.put("url", "http://detail.zol.com.cn/1268/1267839/param.shtml");
        map.put("model", "HZ65U7E");
        MysqlPipeline pipeline = new MysqlPipeline();
        int tv = pipeline.insertUpdateData("tb_tv", map);
        System.out.println(tv);
    }

    /**
     * 处理数据
     * @param resultItems resultItems item数据
     * @param task task 任务
     */
    @Override
    public void process(ResultItems resultItems, Task task) {
        String tableName = "tb_tv";
        Map<String, Object> all = resultItems.getAll();
        int insertUpdateCount = insertUpdateData(tableName, all);
        logger.info(all.get("title") + "\t" + String.format("数据插入成功%d", insertUpdateCount));
    }

    /**
     * 关闭Mysql连接
     */
    @Override
    public void closeSpider() {
        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                logger.warn("close error", e);
            }
        }
    }

}
