/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.connectors;

/**
 *
 * @author dipacs
 */
public class SearchResult {
    
    private int totalCount;
    private int pageIndex;
    private int pageSize;
    private TorrentDescriptor[] result;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public TorrentDescriptor[] getResult() {
        return result;
    }

    public void setResult(TorrentDescriptor[] result) {
        this.result = result;
    }
    
}
