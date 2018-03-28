package cn.e3mall.common.pojo;

import java.io.Serializable;
import java.util.List;

public class SearchList implements Serializable{

    private Long recordCount;
    private List<SearchResult> itemList;
    private Integer totalPages;


    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public List<SearchResult> getItemList() {
        return itemList;
    }

    public void setItemList(List<SearchResult> itemList) {
        this.itemList = itemList;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
