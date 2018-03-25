package cn.e3mall.content.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;
import cn.e3mall.pojo.TbContentExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private JedisClient jedisClient;


    @Value("${CONTENT_LIST}")
    private String CONTENT_LIST;

    /**
     * 保存（大广告..）内容
     * @param content
     * @return
     */
    @Override
    public E3Result save(TbContent content) {

        // 1.封装参数
        content.setCreated(new Date());
        content.setCreated(new Date());

        // 2.插入
        contentMapper.insert(content);

        try {
            jedisClient.hdel(CONTENT_LIST,content.getCategoryId().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 3.返回
        return E3Result.ok();
    }

    /**
     * 根据分类id，取出各个分类的数据
     * @param i
     * @return
     */
    @Override
    public List<TbContent> findByCategoryId(Long i) {

        try{
            // 1）先从redis缓存中获取，需要的是字符串格式
            String json = jedisClient.hget(CONTENT_LIST, i + "");
            // 2）转成list
            if(StringUtils.isNotBlank(json)){
                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
                return list;
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

        // 1.设置查询条件
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(i);
        // 2.执行查询并返回
        List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);

        try {
            // 1）将list转成字符串
            String json = JsonUtils.objectToJson(list);
            // 2）设置到redis中
            jedisClient.hset(CONTENT_LIST,i+"",json);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return list;
    }
}
