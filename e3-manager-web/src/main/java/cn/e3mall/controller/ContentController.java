package cn.e3mall.controller;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 添加内容分类（大广告，...）的内容
     * @param content
     * @return
     */
    @RequestMapping("/content/save")
    @ResponseBody
    public E3Result save(TbContent content){

        return contentService.save(content);

    }

}
