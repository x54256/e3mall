package cn.e3mall.controller;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@Controller
public class PictureController {

    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    @RequestMapping(value = "/pic/upload",method = RequestMethod.POST,produces = MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
    @ResponseBody
    public String fileUpload(MultipartFile uploadFile){

        // 1.获取文件扩展名
        String filename = uploadFile.getOriginalFilename();
        String extName = filename.substring(filename.lastIndexOf(".") + 1);
        // 2.使用工具类创建一个FastDFS的客户端
        HashMap<Object, Object> result = new HashMap<>();
        try {
            // 3.读取配置文件
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");
            // 4.上传处理，返回文件路径 group/...
            String path = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
            // 5.拼接返回的url和ip地址，拼装成完整的url
            String url = IMAGE_SERVER_URL + path;
            // 6.返回一个字典
            System.out.println(url);
            result.put("error",0);
            result.put("url",url);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", 1);
            result.put("message", "图片上传失败");
        }
        String json = JsonUtils.objectToJson(result);
        return json;
    }
}
