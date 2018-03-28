package cn.e3mall.search.controller;

import cn.e3mall.common.pojo.SearchList;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.service.SearchItemService;
import cn.e3mall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    // 搜索页面展示商品条数
    @Value("${SEARCH_RESULT_ROWS}")
    private Integer SEARCH_RESULT_ROWS;


    @RequestMapping("/search")
//    @ResponseBody
    public String impotItemIndex(String keyword, @RequestParam(defaultValue="1") Integer page, Model model) throws Exception {

        // 由于是get请求，需要我们自己处理一下乱码问题
        keyword = new String(keyword.getBytes("iso-8859-1"), "utf-8");

        SearchList searchList = searchService.search(keyword, page, SEARCH_RESULT_ROWS);

        //把结果传递给页面
        model.addAttribute("query", keyword);
        model.addAttribute("totalPages", searchList.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("recourdCount", searchList.getRecordCount());
        model.addAttribute("itemList", searchList.getItemList());

        //返回逻辑视图
        return "search";
    }

}
