package cn.e3mall.order.controller;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 处理订单的Controller
 */
@Controller
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    /**
     * 返回购物车列表
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/order/order-cart")
    private String showOrderCart(Model model, HttpServletRequest request){
        // 1.获取用户id
        TbUser user = (TbUser) request.getAttribute("user");
        // 2.调用service方法，获取购物车列表
        List<TbItem> cartList = cartService.getCartList(user.getId());
        // 3.设置到域中
        model.addAttribute("cartList",cartList);
        // 4.返回逻辑视图
        return "order-cart";
    }

    /**
     * 生成订单
     * @param orderInfo
     * @param request
     * @return
     */
    @RequestMapping(value="/order/create", method = RequestMethod.POST)
    public String createOrder(OrderInfo orderInfo, HttpServletRequest request) {

        //取用户信息
        TbUser user = (TbUser) request.getAttribute("user");
        //把用户信息添加到orderInfo中。
        orderInfo.setUserId(user.getId());
        orderInfo.setBuyerNick(user.getUsername());
        //调用服务生成订单
        E3Result e3Result = orderService.createOrder(orderInfo);
        //如果订单生成成功，需要删除购物车
        if (e3Result.getStatus() == 200) {
            //清空购物车
            cartService.clearCartItem(user.getId());
        }
        //把订单号传递给页面
        request.setAttribute("orderId", e3Result.getData());
        request.setAttribute("payment", orderInfo.getPayment());
        //返回逻辑视图
        return "success";
    }
}
