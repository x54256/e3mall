package cn.e3mall.cart.service.impl;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_CART_PRE}")
    private String REDIS_CART_PRE;

    // 不建议服务调服务，但又是需要立即加载的，不能用mq，所以我们直接用mapper
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 商品添加购物车
     * 分析：以hash方式存入redis中;key:userId,field:itemId,value:Item对象的json格式
     */
    @Override
    public void addCart(Long id, Long itemId, Integer num) {

        TbItem item;
        // 判断当前的itemId是否存在于redis中，使用hexists
        Boolean hexists = jedisClient.hexists(REDIS_CART_PRE + ":" + id, itemId + "");
        if (hexists){
            // 存在将其取出+num
            String json = jedisClient.hget(REDIS_CART_PRE + ":" + id, itemId + "");
            // 将其转成TbItem格式
            item = JsonUtils.jsonToPojo(json, TbItem.class);
            // 修改商品个数
            item.setNum(item.getNum()+num);
        } else {
            // 不存在通过mapper获取商品信息加入redis中
            item = itemMapper.selectByPrimaryKey(itemId);
            // 重新设置数量
            item.setNum(num);
            // 取一张照片
            item.setImage(item.getImage().split(",")[0]);
        }
        // 重新转成json格式，放入redis中
        jedisClient.hset(REDIS_CART_PRE + ":" + id, itemId + "",JsonUtils.objectToJson(item));

    }

    /**
     * 合并商品列表
     * @param id
     * @param cartList
     */
    @Override
    public void mergeCart(Long id, List<TbItem> cartList) {
        // 循环遍历，调用addCart方法
        for (TbItem item:cartList) {
            addCart(id,item.getId(),item.getNum());
        }

    }

    /**
     * 获取商品列表
     * @param id
     */
    @Override
    public List<TbItem> getCartList(Long id) {
        // 1.获取商品列表
        List<String> list = jedisClient.hvals(REDIS_CART_PRE + ":" + id);
        // 2.将json转成TbItem格式
        List<TbItem> tbItems = new ArrayList<>();
        for (String json:list) {
            TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
            tbItems.add(tbItem);
        }
        return tbItems;
    }

    /**
     * 更新购物车中商品的数量
     * @param id
     * @param itemId
     * @param num
     */
    @Override
    public void updateCartNum(Long id, Long itemId, Integer num) {
        // 1.获取商品信息
        String json = jedisClient.hget(REDIS_CART_PRE + ":" + id, itemId + "");
        // 2.转码.
        TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
        // 3.修改其中num数量
        tbItem.setNum(num);
        // 4.写入redis
        jedisClient.hset(REDIS_CART_PRE + ":" + id, itemId + "",JsonUtils.objectToJson(tbItem));
    }

    /**
     * 删除购物车中的商品
     * @param id
     * @param itemId
     */
    @Override
    public void deleteCartItem(Long id, Long itemId) {
        // 删除
        jedisClient.hdel(REDIS_CART_PRE + ":" + id, itemId + "");
    }

    /**
     * 清空购物车
     * @param id
     */
    @Override
    public void clearCartItem(Long id) {
        // 1.获取当前用户的购物车中所有的item
        List<String> hvals = jedisClient.hvals(REDIS_CART_PRE + ":" + id);
        // 2.循环调用，删除购物车中的商品
        for (String json:hvals) {
            deleteCartItem(id,JsonUtils.jsonToPojo(json,TbItem.class).getId());
        }
    }
}
