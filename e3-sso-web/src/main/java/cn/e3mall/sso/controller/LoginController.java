package cn.e3mall.sso.controller;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.sso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @RequestMapping("/page/login")
    public String showLogin(){
        return "login";
    }

    @Autowired
    private UserService userService;

    @RequestMapping("/user/login")
    @ResponseBody
    public E3Result login(String username, String password, HttpServletRequest request, HttpServletResponse response){

        // 1.进行数据库查询用户是否存在
        E3Result result = userService.login(username,password);
        // 2.判断是否通过验证，通过验证戴上cookie
        if (result.getStatus() == 200){
            // redis（session）的key
            String token = result.getData().toString();
            // 写入cookie，**Cookie要跨域**
            // 因为我们写的是localhost，所以用这种方式就行；如果是www.jd.com，item.jd.com这种形式的话就要使用doSetCookie方法设置跨域cookie
            CookieUtils.setCookie(request,response,"token",token);
        }

        return result;
    }


}
