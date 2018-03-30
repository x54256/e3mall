package cn.e3mall.cart.interceptor;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // 前处理，执行handler之前执行此方法。返回true，放行	false：拦截

        // 1.从cookie中取出用户的token
        String token = CookieUtils.getCookieValue(httpServletRequest, "token");
        // 2.判断token是否为空
            // 为空直接放行
        if (StringUtils.isBlank(token)){
            return true;
        }
        // 3.调用service层方法，取出user对象
            // 没有取到用户信息。登录过期，直接放行
        E3Result e3Result = tokenService.getUserByToken(token);
        if (e3Result.getStatus()!=200){
            return true;
        }
        // 4.将user对象放入request域中后，放行
        httpServletRequest.setAttribute("user",e3Result.getData());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
