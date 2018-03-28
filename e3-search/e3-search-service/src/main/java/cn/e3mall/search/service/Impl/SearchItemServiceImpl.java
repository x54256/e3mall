package cn.e3mall.search.service.Impl;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.service.SearchItemService;
import cn.e3mall.search.service.mapper.ItemMapper;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SearchItemServiceImpl implements SearchItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrServer solrServer;

    @Override
    public E3Result importItems() {

        try {
            // 1.查询到所有商品列表
            List<SearchResult> list = itemMapper.selectAllItem();
            // 2.导入索引库中
            for (SearchResult searchResult: list) {
                // 创建文档对象
                SolrInputDocument document = new SolrInputDocument();
                // 向文档对象中添加域
                document.addField("id",searchResult.getId());
                document.addField("item_title",searchResult.getTitle());
                document.addField("item_sell_point",searchResult.getSell_point());
                document.addField("item_price",searchResult.getPrice());
                document.addField("item_image",searchResult.getImage());
                document.addField("item_category_name",searchResult.getCategory_name());
                // 将文档添加到索引库中
                solrServer.add(document);
            }
            // 3.提交
            solrServer.commit();
            return E3Result.ok();

        } catch (Exception ex){

            ex.printStackTrace();
            return E3Result.build(500,"建立商品索引时失败");
        }

    }
}
