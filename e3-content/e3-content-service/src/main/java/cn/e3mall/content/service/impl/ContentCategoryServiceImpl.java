package cn.e3mall.content.service.impl;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    /**
     * 异步树的方式获取内容（大广告，小广告...）分类信息
     * @return
     */
    @Override
    public List<EasyUITreeNode> getCategoryList(Long parentId) {

        // 1.设置查询条件
        TbContentCategoryExample example = new TbContentCategoryExample();
        Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);

        // 2.执行查询
        List<TbContentCategory> categoryList = contentCategoryMapper.selectByExample(example);

        // 3.封装成EasyUITreeNode对象，返回
        List<EasyUITreeNode> easyUITreeNodes = new ArrayList<>();

        for (TbContentCategory category: categoryList) {
            // 创建一个EasyUITreeNode对象，添加属性
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(category.getId());
            node.setText(category.getName());
            node.setState(category.getIsParent()?"closed":"open");
            // 添加到arraylist中
            easyUITreeNodes.add(node);
        }

        return easyUITreeNodes;
    }

    @Override
    public E3Result addCategory(TbContentCategory category) {

        category.setStatus(1);   //可选值:1(正常),2(删除)
        category.setSortOrder(1);
        category.setIsParent(false);    // 是否为父节点
        category.setCreated(new Date());
        category.setUpdated(new Date());

        contentCategoryMapper.insert(category);

        // 在映射文件中设置返回主键后，他会直接封装到我们的对象中
        //<selectKey keyProperty="id" resultType="long" order="AFTER">
        //      select last_insert_id()
        //    </selectKey>
        // 判断父节点isParent字段是否为1
        Long parentId = category.getParentId();
        TbContentCategory parentCategory = contentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parentCategory.getIsParent()){
            parentCategory.setIsParent(true);
            contentCategoryMapper.insert(parentCategory);
        }


        return E3Result.ok(category);
    }
}
