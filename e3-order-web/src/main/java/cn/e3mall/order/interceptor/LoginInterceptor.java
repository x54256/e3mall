package cn.e3mall.order.interceptor;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class LoginInterceptor implements HandlerInterceptor {

    @Value("${LOGIN_PATH}")
    private String LOGIN_PATH;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CartService cartService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1.获取cookie中的token
        String token = CookieUtils.getCookieValue(request, "token");
        // 2.判断token是否为空
        if (StringUtils.isBlank(token)) {
            // 为空，重定向到登录页面
            response.sendRedirect(LOGIN_PATH+"/page/login?redirect=" + request.getRequestURL());
            // 拦截
            return false;
        }
        // 3.调用service层方法，获取user对象
        E3Result e3Result = tokenService.getUserByToken(token);
        if (e3Result.getStatus()!=200){
            // 为空，重定向到登录页面
            response.sendRedirect(LOGIN_PATH+"/page/login?redirect=" + request.getRequestURL());
            // 拦截
            return false;
        }
        // 获取user对象
        TbUser user = (TbUser) e3Result.getData();
        // 4.获取cookie中的购物车列表
        String json = CookieUtils.getCookieValue(request, "cart", true);
        List<TbItem> tbItemList = JsonUtils.jsonToList(json, TbItem.class);
        // 5.合并购物车
        cartService.mergeCart(user.getId(),tbItemList);
        // 6.将user对象放进request域中，放行
        request.setAttribute("user",user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
