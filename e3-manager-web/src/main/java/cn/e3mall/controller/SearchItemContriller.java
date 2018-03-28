package cn.e3mall.controller;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.service.SearchItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchItemContriller {

    @Autowired
    private SearchItemService searchItemService;


    @RequestMapping("/index/item/import")
    @ResponseBody
    public E3Result impotItemIndex() {

        return searchItemService.importItems();
    }
}
