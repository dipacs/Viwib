/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.connectors;

/**
 *
 * @author dipacs
 */
public class TorrentDescriptor {
    
    private String id;
    private String name;
    private String description;
    private String torrentUrl;
    private String coverUrl;
    private String dateAdded;
    private long seeds;
    private long leeches;
    private long downloaded;
    private String size;
    private String categoryId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTorrentUrl() {
        return torrentUrl;
    }

    public void setTorrentUrl(String torrentUrl) {
        this.torrentUrl = torrentUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getSeeds() {
        return seeds;
    }

    public void setSeeds(long seeds) {
        this.seeds = seeds;
    }

    public long getLeeches() {
        return leeches;
    }

    public void setLeeches(long leeches) {
        this.leeches = leeches;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
}
