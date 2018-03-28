package cn.e3mall.service.Impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemDescMapper itemDescMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Resource/*(name = "topicDestination")*/// 使用的是广播方式
    private Destination topicDestination;

    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_ITEM_PRE}")
    private String REDIS_ITEM_PRE;

    @Value("${ITEM_CACHE_EXPIRE}")
    private Integer ITEM_CACHE_EXPIRE;


    @Override
    public TbItem getItemById(Long id) {

        try {
            String json = jedisClient.get(REDIS_ITEM_PRE + id + ":BASE");
            if (StringUtils.isNoneBlank(json)){
                return JsonUtils.jsonToPojo(json,TbItem.class);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        // 逆向工程两种通过主键查询的方法
        // 方法一：
        // itemMapper.selectByPrimaryKey(id);
        // 方法二：
        TbItemExample example = new TbItemExample();
        // 1.设置查询条件
        Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        // 2.执行查询
        List<TbItem> itemList = itemMapper.selectByExample(example);
        // 3.返回
        if (itemList!=null&&itemList.size()>0){
            try {
                jedisClient.set(REDIS_ITEM_PRE + id + ":BASE",JsonUtils.objectToJson(itemList.get(0)));
                jedisClient.expire(REDIS_ITEM_PRE + id + ":BASE",ITEM_CACHE_EXPIRE);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return itemList.get(0);
        }

        return null;
    }

    @Override
    public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
        //1.设置分页信息
        PageHelper.startPage(page, rows);
        //2.执行查询
        TbItemExample example = new TbItemExample();
        List<TbItem> list = itemMapper.selectByExample(example);
        //3.创建一个返回值对象
        EasyUIDataGridResult result = new EasyUIDataGridResult();
            // 将返回的list放入pageBean中
        result.setRows(list);
        //4.取分页结果
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        //5.取总记录数
        long total = pageInfo.getTotal();
            // 将总条数放入pageBean中
        result.setTotal(total);
        return result;
    }

    @Override
    public E3Result save(TbItem item, String desc) {
        // 保存商品基本信息

        // 1.通过当前日期生成商品的id
        final Long itemId = IDUtils.genItemId();
        // 2.将id设置到item中
        item.setId(itemId);
        // 3.设置zhuangtaima，1-正常，2-下架，3-删除
        item.setStatus((byte) 1);
        // 4.设置create，update
        item.setCreated(new Date());
        item.setUpdated(new Date());
        // 5.保存
        itemMapper.insert(item);

        // 保存商品详情
        TbItemDesc itemDesc = new TbItemDesc();
        // 1.设置id，由于是一对一关系，所以与itemid一样
        itemDesc.setItemId(itemId);
        // 2.设置其它属性
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(new Date());
        itemDesc.setItemDesc(desc);
        // 3.保存
        itemDescMapper.insert(itemDesc);
        // ==============实现更新索引================

        jmsTemplate.send(topicDestination, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(itemId + "");
                return textMessage;
            }
        });

        // ========================================
        // 返回ok.
        return E3Result.ok();
    }

    @Override
    public TbItemDesc getItemDescById(Long itemId) {
        try {
            String json = jedisClient.get(REDIS_ITEM_PRE + itemId + ":DESC");
            if (StringUtils.isNoneBlank(json)){
                return JsonUtils.jsonToPojo(json,TbItemDesc.class);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        // 这个方法会连大文本一起返回
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);

        try {
            jedisClient.set(REDIS_ITEM_PRE + itemId + ":DESC",JsonUtils.objectToJson(itemDesc));
            jedisClient.expire(REDIS_ITEM_PRE + itemId + ":DESC",ITEM_CACHE_EXPIRE);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return itemDesc;

    }
}
