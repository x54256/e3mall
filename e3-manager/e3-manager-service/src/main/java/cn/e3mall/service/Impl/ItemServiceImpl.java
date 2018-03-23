package cn.e3mall.service.Impl;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    @Override
    public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
        //1.设置分页信息
        PageHelper.startPage(page, rows);
        //2.执行查询
        TbItemExample example = new TbItemExample();
        List<TbItem> list = itemMapper.selectByExample(example);
        //3.创建一个返回值对象
        EasyUIDataGridResult result = new EasyUIDataGridResult();
            // 将返回的list放入pageBean中
        result.setRows(list);
        //4.取分页结果
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        //5.取总记录数
        long total = pageInfo.getTotal();
            // 将总条数放入pageBean中
        result.setTotal(total);
        return result;
    }
}
