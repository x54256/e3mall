package cn.e3mall.service.Impl;

import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public TbItem getItemById(Long id) {
        // 逆向工程两种通过主键查询的方法
        // 方法一：
        // itemMapper.selectByPrimaryKey(id);
        // 方法二：
        TbItemExample example = new TbItemExample();
        // 1.设置查询条件
        Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);
        // 2.执行查询
        List<TbItem> itemList = itemMapper.selectByExample(example);
        // 3.返回
        if (itemList!=null&&itemList.size()>0){
            return itemList.get(0);
        }

        return null;
    }
}
