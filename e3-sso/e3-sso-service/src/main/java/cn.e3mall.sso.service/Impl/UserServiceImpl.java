package cn.e3mall.sso.service.Impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.sso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private JedisClient jedisClient;

    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;

    @Override
    public E3Result login(String username, String password) {

        // 1.创建查询条件
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        // 2.执行查询
        List<TbUser> tbUsers = userMapper.selectByExample(example);
        // 3.如果返回列表不为空，判断密码是否正确
        if (tbUsers!=null&&tbUsers.size()>0){
            if (DigestUtils.md5DigestAsHex(password.getBytes()).equals(tbUsers.get(0).getPassword())){
                // 4.生成随机uuid作为key，返回的user对象作value存入redis中
                String token = UUID.randomUUID().toString();
                // 将密码置空
                tbUsers.get(0).setPassword(null);
                jedisClient.set("SESSION:"+token, JsonUtils.objectToJson(tbUsers.get(0)));
                jedisClient.expire("SESSION:"+token,SESSION_EXPIRE);
                // 5.将随机uuid返回
                return E3Result.ok("SESSION:"+token);

            }else {
                // 如果两个密码不相等，返回登录失败
                return E3Result.build(500,"密码输入错误");
            }

        }else {
            // 如果返回列表为空，返回登录失败
            return E3Result.build(500,"用户名输入错误");
        }
    }
}
