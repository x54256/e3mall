package cn.x5456.test;

import cn.e3mall.common.utils.FastDFSClient;
import org.csource.fastdfs.*;
import org.junit.Test;

public class FastDFSTest {

    @Test
    public void testFileUpload() throws Exception {
        // 1.配置文件路径
        FastDFSClient fastDFSClient = new FastDFSClient("/home/x5456/IdeaProjects/e3parent/e3-manager-web/src/main/resources/conf/client.conf");
        // 2.图片路径
        String file = fastDFSClient.uploadFile("/home/x5456/图片/选区_001.png");
        System.out.println(file);
    }
}
