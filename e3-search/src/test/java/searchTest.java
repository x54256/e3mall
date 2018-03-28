//import org.apache.solr.client.solrj.SolrQuery;
//import org.apache.solr.client.solrj.SolrServer;
//import org.apache.solr.client.solrj.impl.HttpSolrServer;
//import org.apache.solr.client.solrj.response.QueryResponse;
//import org.apache.solr.common.SolrDocument;
//import org.apache.solr.common.SolrDocumentList;
//import org.junit.Test;
//
//import java.util.List;
//import java.util.Map;
//
//public class searchTest {
//
//    @Test
//    public void queryDocument() throws Exception {
//        // 1.创建一个SolrServer对象
//        SolrServer solrServer = new HttpSolrServer("http://127.0.0.1:8080/solr");
//        // 2.创建一个查询对象，可以参考solr的后台的查询功能设置条件
//        SolrQuery query = new SolrQuery();
//        // 3.设置查询条件
//            // 1）设置查询字段的2中方法
//        //query.setQuery("手机");
//        query.set("q","手机");
//            // 2）设置分页条件，从第几条开始，取几条数据
//        query.setStart(1);
//        query.setRows(2);
//            // 3）开启高亮
//        query.setHighlight(true);
//            // 4）设置那个字段高亮显示
//        query.addHighlightField("item_title");
//            // 5）设置高亮字段的头尾
//        query.setHighlightSimplePre("<em>");
//        query.setHighlightSimplePost("</em>");
//            // 4）设置搜索域
//        query.set("df", "item_title");
//        // 4.执行查询，得到一个QueryResponse对象。
//        QueryResponse queryResponse = solrServer.query(query);
//        // 5.获取查询结果
//            // 1）获取查询结果总记录数
//        SolrDocumentList solrDocumentList = queryResponse.getResults();
//        System.out.println("查询结果总记录数：" + solrDocumentList.getNumFound());
//            // 2）取查询结果集
//        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
//        for (SolrDocument solrDocument : solrDocumentList) {
//            System.out.println(solrDocument.get("id"));
//            // 3）取高亮后的结果集
//            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
//                // 判断集合是否为空，为空就是标题中没有搜索的字段
//            String title;
//            if (list != null && list.size() > 0) {
//                title = list.get(0);
//            } else {
//                title = (String) solrDocument.get("item_title");
//            }
//            System.out.println(title);
//            System.out.println(solrDocument.get("item_sell_point"));
//            System.out.println(solrDocument.get("item_price"));
//            System.out.println(solrDocument.get("item_image"));
//            System.out.println(solrDocument.get("item_category_name"));
//        }
//
//    }
//}
