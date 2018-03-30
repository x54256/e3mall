package cn.e3mall.cart.controller;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;

    @Value("${COOKIE_CART_EXPIRE}")
    private Integer COOKIE_CART_EXPIRE;

    /**
     * 从cookie中取购物车列表的处理
     */
    private List<TbItem> getCartListFromCookie(HttpServletRequest request) {
        String json = CookieUtils.getCookieValue(request, "cart", true);
        //判断json是否为空
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        //把json转换成商品列表
        List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
        return list;
    }

    /**
     * 商品添加购物车
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/cart/add/{itemId}")
    public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue="1")Integer num,
                          HttpServletRequest request, HttpServletResponse response) {

        // 1.获取用户对象
        TbUser user = (TbUser) request.getAttribute("user");
        if (user == null) {
            // 为空：将该商品数据写入cookie中
            // 1）获取购物车cookie
            List<TbItem> cartList = getCartListFromCookie(request);
            // 2）判断该商品是否存在
            boolean flag = true;
            for (TbItem item:cartList) {
                if (item.getId().equals(itemId)){
                    // 存在将商品取出+num
                    item.setNum(item.getNum()+num);
                    // 修改标志位
                    flag = false;
                    // 跳出循环
                    break;
                }
            }
            if (flag){
                // 不存在调用service层将商品取出，添加到列表中
                TbItem item = itemService.getItemById(itemId);
                // 重新设置数量
                item.setNum(num);
                // 将图片取一张
                item.setImage(item.getImage().split(",")[0]);
                cartList.add(item);
            }
            // 3）重新写入cookie，由于商品中可能有中文，所以要转码
            CookieUtils.setCookie(request,response,"cart",JsonUtils.objectToJson(cartList),COOKIE_CART_EXPIRE,true);
        } else {
            // 不为空：将商品数据通过service层写入redis中
            // 调用service层方法，将该商品写入redis
            cartService.addCart(user.getId(),itemId,num);
        }

        return "cartSuccess";
    }

    /**
     * 购物车页面
     * @return
     */
    @RequestMapping("/cart/cart")
    public String cart(Model model,HttpServletRequest request,HttpServletResponse response){
        // 1.获取cookie中的购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);
        // 2.获取user对象
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null) {
            // 如果存在，调用service层方法，将cookie中的商品列表与redis中的合并，并从redis中取出商品列表
            // 1）合并购物车
            cartService.mergeCart(user.getId(),cartList);
            // 2）清除cookie中的购物车
            CookieUtils.deleteCookie(request,response,"cart");
            // 3）获取购物车中的商品列表
            cartList = cartService.getCartList(user.getId());
        }
        // 不存在，直接将cookie中的商品列表设置到域中
        request.setAttribute("cartList",cartList);
//        model.addAttribute("cartList",cartList)
        // 返回逻辑视图
        return "cart";
    }

    /**
     * 更新购物车商品数量
     */
    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public E3Result updateCartNum(@PathVariable Long itemId, @PathVariable Integer num
            , HttpServletRequest request , HttpServletResponse response) {
        //判断用户是否为登录状态
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null) {
            cartService.updateCartNum(user.getId(), itemId, num);
            return E3Result.ok();
        }
        //从cookie中取购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);
        //遍历商品列表找到对应的商品
        for (TbItem tbItem : cartList) {
            if (tbItem.getId().longValue() == itemId) {
                //更新数量
                tbItem.setNum(num);
                break;
            }
        }
        //把购物车列表写回cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
        //返回成功
        return E3Result.ok();
    }

    /**
     * 删除购物车商品
     */
    @RequestMapping("/cart/delete/{itemId}")
    public String deleteCartItem(@PathVariable Long itemId, HttpServletRequest request,
                                 HttpServletResponse response) {
        //判断用户是否为登录状态
        TbUser user = (TbUser) request.getAttribute("user");
        if (user != null) {
            cartService.deleteCartItem(user.getId(), itemId);
            return "redirect:/cart/cart.html";
        }
        //从cookie中取购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);
        //遍历列表，找到要删除的商品
        for (TbItem tbItem : cartList) {
            if (tbItem.getId().longValue() == itemId) {
                //删除商品
                cartList.remove(tbItem);
                //跳出循环
                break;
            }
        }
        //把购物车列表写入cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
        //返回逻辑视图
        return "redirect:/cart/cart.html";
    }
}
