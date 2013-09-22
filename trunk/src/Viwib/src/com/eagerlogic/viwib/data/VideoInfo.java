/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.data;

import java.util.ArrayList;

/**
 *
 * @author dipacs
 */
public class VideoInfo {
    
    private String id;
    private String title;
    private String description;
    private long size;
    private int seeds;
    private int leaches;
    private long downloaded;
    private long dateAdded;
    private String coverUrl;
    private String img1Url;
    private String img2Url;
    private String img3Url;
    private String coverUrlSmall;
    private String img1UrlSmall;
    private String img2UrlSmall;
    private String img3UrlSmall;
    private String torrentUrl;
    private EVideoFormat videoFormat;
    private final ArrayList<EVideoGenre> genres = new ArrayList<EVideoGenre>();
    private ELanguage language;

    public VideoInfo() {
    }

    public ArrayList<EVideoGenre> getGenres() {
        return genres;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getSeeds() {
        return seeds;
    }

    public void setSeeds(int seeds) {
        this.seeds = seeds;
    }

    public int getLeaches() {
        return leaches;
    }

    public void setLeaches(int leaches) {
        this.leaches = leaches;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getImg1Url() {
        return img1Url;
    }

    public void setImg1Url(String img1Url) {
        this.img1Url = img1Url;
    }

    public String getImg2Url() {
        return img2Url;
    }

    public void setImg2Url(String img2Url) {
        this.img2Url = img2Url;
    }

    public String getImg3Url() {
        return img3Url;
    }

    public void setImg3Url(String img3Url) {
        this.img3Url = img3Url;
    }

    public String getCoverUrlSmall() {
        return coverUrlSmall;
    }

    public void setCoverUrlSmall(String coverUrlSmall) {
        this.coverUrlSmall = coverUrlSmall;
    }

    public String getImg1UrlSmall() {
        return img1UrlSmall;
    }

    public void setImg1UrlSmall(String img1UrlSmall) {
        this.img1UrlSmall = img1UrlSmall;
    }

    public String getImg2UrlSmall() {
        return img2UrlSmall;
    }

    public void setImg2UrlSmall(String img2UrlSmall) {
        this.img2UrlSmall = img2UrlSmall;
    }

    public String getImg3UrlSmall() {
        return img3UrlSmall;
    }

    public void setImg3UrlSmall(String img3UrlSmall) {
        this.img3UrlSmall = img3UrlSmall;
    }

    public String getTorrentUrl() {
        return torrentUrl;
    }

    public void setTorrentUrl(String torrentUrl) {
        this.torrentUrl = torrentUrl;
    }

    public EVideoFormat getVideoFormat() {
        return videoFormat;
    }

    public void setVideoFormat(EVideoFormat videoFormat) {
        this.videoFormat = videoFormat;
    }

    public ELanguage getLanguage() {
        return language;
    }

    public void setLanguage(ELanguage language) {
        this.language = language;
    }
    
}
