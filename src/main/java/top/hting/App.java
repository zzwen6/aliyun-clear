package top.hting;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import top.hting.service.AliyunService;

public class App
{
    public static void main( String[] args ){
        AliyunService aliyunService = new AliyunService();

        String refreshToken = System.getProperty("aliyun_refresh_token");
        String parentFileId = System.getProperty("parent_file_id");

        double v = aliyunService.getCapacity(refreshToken);
        if (v > 0.5) {
            Long totalSize = 0L;
            String marker = "";
            do {
                JSONObject res = aliyunService.getFileList(refreshToken, parentFileId, marker);
                // 文件列表
                JSONArray items = res.getJSONArray("items");
                for (int i = 0; i < items.size(); i++) {
                    JSONObject object = items.getJSONObject(i);
                    aliyunService.deleteFile(refreshToken, object.getString("file_id"));
                    System.out.println("删除: " + object.getString("file_id") + "->" + object.getString("name"));
                    totalSize += object.getLong("size");
                }

                marker = res.getString("marker");
                v = aliyunService.getCapacity(refreshToken);
            } while (StringUtils.isNotBlank(marker) && v > 0.5);
            double tg = totalSize*0.1 /1024/1024;
            System.out.println("本次总计删除(字节): " + totalSize );
            System.out.println("本次总计删除(GB): " + tg );
        }

    }
}
