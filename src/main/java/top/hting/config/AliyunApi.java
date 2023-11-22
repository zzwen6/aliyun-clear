package top.hting.config;

/**
 * 阿里云api 路径
 */
public class AliyunApi {

    /**
     * 由刷新令牌获取 访问令牌
     */
    public static final String ACCOUNT_TOKEN = "https://api.aliyundrive.com/v2/account/token";

    /**
     * 获取用户信息
     */
    public static final String GET_USER = "https://user.aliyundrive.com/v2/user/get";

    /**
     * 获取文件列表
     */
    public static final String GET_FILE_LIST = "https://api.aliyundrive.com/adrive/v3/file/list";

    /**
     * 获取路径
     */
    public static final String GET_FILE_PATH = "https://api.aliyundrive.com/adrive/v1/file/get_path";

    /**
     * 批处理
     */
    public static final String BATCH = "https://api.aliyundrive.com/v3/batch";

    /**
     * 容量详情
     */
    public static final String DRIVE_CAPACITY_DETAILS = "https://api.aliyundrive.com/adrive/v1/user/driveCapacityDetails";

}
