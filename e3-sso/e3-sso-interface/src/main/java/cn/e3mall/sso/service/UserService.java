package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;

public interface UserService {
    E3Result login(String username, String password);
}
