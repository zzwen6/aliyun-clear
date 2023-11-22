package top.hting.service;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import top.hting.config.AliyunApi;

public class AliyunService {

    private static String ACCESS_TOKEN = null;
    private static String DRIVER_ID = null;


    /**
     * 获取token
     */
    public JSONObject getToken(String refreshToken) {
        JSONObject request = new JSONObject();
        request.put("refresh_token", refreshToken);
        request.put("grant_type", "refresh_token");

        HttpRequest httpRequest = HttpUtil.createRequest(Method.POST, AliyunApi.ACCOUNT_TOKEN);
        httpRequest.header(Header.CONTENT_TYPE, "application/json");
        httpRequest.body(request.toJSONString());
        String body = httpRequest.execute().body();

        JSONObject result = JSON.parseObject(body);
        ACCESS_TOKEN = result.getString("access_token");
        return result;
    }

    /**
     * 获取用户信息
     * @param
     * @return
     */
    public JSONObject getUser(String refreshToken) {
        if (ACCESS_TOKEN == null) {
            getToken(refreshToken);
        }

        HttpRequest httpRequest = HttpUtil.createRequest(Method.POST, AliyunApi.GET_USER);
        httpRequest.header(Header.CONTENT_TYPE, "application/json");
        httpRequest.header(Header.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
        httpRequest.body("{}");
        String body = httpRequest.execute().body();
        JSONObject result = JSON.parseObject(body);

        if (result.containsKey("resource_drive_id") && StringUtils.isNotBlank(result.getString("resource_drive_id"))) {
            DRIVER_ID = result.getString("resource_drive_id");
        } else {
            DRIVER_ID = result.getString("backup_drive_id");
        }
        return result;
    }

    public JSONObject driveCapacityDetails(String refreshToken) {
        if (ACCESS_TOKEN == null) {
            getToken(refreshToken);
        }

        HttpRequest httpRequest = HttpUtil.createRequest(Method.POST, AliyunApi.DRIVE_CAPACITY_DETAILS);
        httpRequest.header(Header.CONTENT_TYPE, "application/json");
        httpRequest.header(Header.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
        httpRequest.body("{}");
        String body = httpRequest.execute().body();
        JSONObject result = JSON.parseObject(body);

        return result;
    }

    public double getCapacity(String refreshToken) {
        JSONObject jsonObject = this.driveCapacityDetails(refreshToken);
        Long driveTotalSize = jsonObject.getLong("drive_total_size");
        Long driveUsedSize = jsonObject.getLong("drive_used_size");
        return driveUsedSize * 1.0 / driveTotalSize;
    }


    public JSONObject getFileList(String refreshToken, String parentFileId, String marker) {
        if (DRIVER_ID == null) {
            getUser(refreshToken);
        }

        HttpRequest httpRequest = HttpUtil.createRequest(Method.POST, AliyunApi.GET_FILE_LIST);
        httpRequest.header(Header.CONTENT_TYPE, "application/json");
        httpRequest.header(Header.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);

        JSONObject request = new JSONObject();
        request.put("drive_id", DRIVER_ID);
        request.put("parent_file_id", parentFileId);
        request.put("marker", marker);
        request.put("order_by", "created_at");
        request.put("order_direction", "ASC");
        request.put("limit",100);

        httpRequest.body(request.toJSONString());
        String body = httpRequest.execute().body();
        JSONObject result = JSON.parseObject(body);
        return result;
    }


    public JSONObject deleteFile(String refreshToken, String fileId){
        if (DRIVER_ID == null) {
            getUser(refreshToken);
        }
        HttpRequest httpRequest = HttpUtil.createRequest(Method.POST, AliyunApi.BATCH);
        httpRequest.header(Header.CONTENT_TYPE, "application/json");
        httpRequest.header(Header.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);

        String tml = "{\n" +
                "  \"requests\": [\n" +
                "    {\n" +
                "      \"body\": {\n" +
                "        \"drive_id\": \"$DRIVER_ID\",\n" +
                "        \"file_id\": \"$FILE_ID\"\n" +
                "      },\n" +
                "      \"headers\": {\n" +
                "        \"Content-Type\": \"application/json\"\n" +
                "      },\n" +
                "      \"id\": \"$FILE_ID\",\n" +
                "      \"method\": \"POST\",\n" +
                "      \"url\": \"/file/delete\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"resource\": \"file\"\n" +
                "}";

        String tml_1 = StringUtils.replace(tml, "$DRIVER_ID", DRIVER_ID);
        String tml_2 = StringUtils.replace(tml_1, "$FILE_ID", fileId);

        httpRequest.body(tml_2);
        String body = httpRequest.execute().body();
        JSONObject result = JSON.parseObject(body);
        return result;
    }



}
