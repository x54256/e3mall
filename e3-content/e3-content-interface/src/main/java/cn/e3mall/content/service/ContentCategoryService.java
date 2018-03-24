package cn.e3mall.content.service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbContentCategory;

import java.util.List;

public interface ContentCategoryService {

    //异步树的方式获取内容（大广告，小广告...）分类信息
    List<EasyUITreeNode> getCategoryList(Long parentId);

    //实现添加分类功能
    //@return 需要返回添加后的分类id，所以要对sql映射文件进行修改
    E3Result addCategory(TbContentCategory category);
}
