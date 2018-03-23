package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;

public interface ItemService {

    TbItem getItemById(Long id);

    EasyUIDataGridResult getItemList(Integer page, Integer rows);

    E3Result save(TbItem item, String desc);
}
