package cn.e3mall.portal.controller;

import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private ContentService contentService;

    @Value("${ad1}")
    private Long ad1;

    /**
     * 展示主页,并显示大广告
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model){

        // 获取并设置到域中
        List<TbContent> ad1List = contentService.findByCategoryId(ad1);
        model.addAttribute("ad1List",ad1List);

        return "index";
    }

}
