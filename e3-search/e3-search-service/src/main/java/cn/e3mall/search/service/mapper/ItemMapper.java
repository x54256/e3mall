package cn.e3mall.search.service.mapper;


import cn.e3mall.common.pojo.SearchResult;

import java.util.List;

public interface ItemMapper {

    List<SearchResult> selectAllItem();

}
