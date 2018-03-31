package cn.e3mall.order.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbOrder;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbOrderShippingMapper orderShippingMapper;

    @Autowired
    private JedisClient jedisClient;

    @Value("${ORDER_ID_GEN_KEY}")
    private String ORDER_ID_GEN_KEY;
    @Value("${ORDER_ID_START}")
    private String ORDER_ID_START;
    @Value("${ORDER_DETAIL_ID_GEN_KEY}")
    private String ORDER_DETAIL_ID_GEN_KEY;

    /**
     * 生成订单信息
     * @param orderInfo
     * @return
     */
    @Override
    public E3Result createOrder(OrderInfo orderInfo) {

        // 1.使用redis的incr生成订单号
            // 设置起始值
        if (!jedisClient.exists(ORDER_ID_GEN_KEY)){
            jedisClient.set(ORDER_ID_GEN_KEY,ORDER_ID_START);
        }
            // 生成订单号
        String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
        // 2.补全参数
        orderInfo.setOrderId(orderId);
        orderInfo.setStatus(1); //1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        // 3.插入订单表
        orderMapper.insert(orderInfo);

        // 4.向订单明细表插入数据
        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        for (TbOrderItem tbOrderItem : orderItems) {
            //生成明细id
            String odId = jedisClient.incr(ORDER_DETAIL_ID_GEN_KEY).toString();
            //补全pojo的属性
            tbOrderItem.setId(odId);
            tbOrderItem.setOrderId(orderId);
            //向明细表插入数据
            orderItemMapper.insert(tbOrderItem);
        }
        // 5.向订单物流表插入数据
        TbOrderShipping orderShipping = orderInfo.getOrderShipping();
        orderShipping.setOrderId(orderId);
        orderShipping.setCreated(new Date());
        orderShipping.setUpdated(new Date());
        orderShippingMapper.insert(orderShipping);

        // 6.返回E3Result，包含订单号
        return E3Result.ok(orderId);
    }
}
