package cn.e3mall.search.service;

import cn.e3mall.common.pojo.SearchList;

public interface SearchService {
    SearchList search(String keyword, Integer page, Integer rows) throws Exception;
}
