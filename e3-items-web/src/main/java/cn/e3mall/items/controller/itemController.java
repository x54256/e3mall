package cn.e3mall.items.controller;

import cn.e3mall.items.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class itemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    public String showItemInfo(@PathVariable Long itemId, Model model){

        // 1.调用service层方法，获取商品信息
        TbItem tbItem = itemService.getItemById(itemId);
        // 2.由于页面上需要一个image数组，所以我们需要给它包装一下
        Item item = new Item(tbItem);
        // 3.获取商品详情信息
        TbItemDesc tbItemDesc = itemService.getItemDescById(itemId);
        // 4.把数据传递给页面
        model.addAttribute("item", item);
        model.addAttribute("itemDesc", tbItemDesc);
        // 5.返回视图
        return "item";
    }
}
