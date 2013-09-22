/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.connectors;

/**
 *
 * @author dipacs
 */
public class SiteConfig {
    
    private String version;
    private String name;
    private String description;
    private String url;
    private String apiUrl;
    private Category[] categories;
    private SortCategory[] sortCategories;
    private boolean needLogin;
    private long keepAliveInterval;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Category[] getCategories() {
        return categories;
    }

    public void setCategories(Category[] categories) {
        this.categories = categories;
    }

    public SortCategory[] getSortCategories() {
        return sortCategories;
    }

    public void setSortCategories(SortCategory[] sortCategories) {
        this.sortCategories = sortCategories;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public long getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(long keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }
    
}
