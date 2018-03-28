package cn.e3mall.search.service;

import cn.e3mall.common.utils.E3Result;

public interface SearchItemService {

    // 查询所有商品，并建立索引
    E3Result importItems();

    void addDocument(Long itemId);
}
