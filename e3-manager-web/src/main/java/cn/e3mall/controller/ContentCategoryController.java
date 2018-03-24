package cn.e3mall.controller;


import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.pojo.TbContentCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    /**
     * 异步树的方式获取内容（大广告，小广告...）分类信息
     * @return
     */
    @RequestMapping("/content/category/list")
    @ResponseBody
    public List<EasyUITreeNode> getCategoryList(@RequestParam(value = "id",defaultValue = "0") Long parentId){

        return contentCategoryService.getCategoryList(parentId);

    }

    /**
     * 实现添加分类功能
     * @return 需要返回添加后的分类id，所以要对sql映射文件进行修改
     */
    @RequestMapping("/content/category/create")
    @ResponseBody
    public E3Result addCategory(TbContentCategory category){

        return contentCategoryService.addCategory(category);

    }

}
