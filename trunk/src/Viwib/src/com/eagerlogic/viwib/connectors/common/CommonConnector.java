/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.connectors.common;

import com.eagerlogic.viwib.MyCookieManager;
import com.eagerlogic.viwib.connectors.Category;
import com.eagerlogic.viwib.connectors.ASiteConnector;
import com.eagerlogic.viwib.connectors.NewestTorrents;
import com.eagerlogic.viwib.connectors.SearchResult;
import com.eagerlogic.viwib.connectors.SiteConfig;
import com.eagerlogic.viwib.connectors.SortCategory;
import com.eagerlogic.viwib.connectors.TorrentDescriptor;
import com.eagerlogic.viwib.connectors.ncore.NCoreSiteConnector;
import com.eagerlogic.viwib.utils.URLCoder;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author dipacs
 */
public class CommonConnector extends ASiteConnector {

    private SiteConfig siteConfig = null;

    public CommonConnector(String siteUrl) {
        super(siteUrl);
    }

    @Override
    public SiteConfig getConfig() {
        if (this.siteConfig != null) {
            return this.siteConfig;
        }

        SiteConfig res = new SiteConfig();
        String jsonStr = this.request(this.getSiteUrl() + "/viwib.json");
        if (jsonStr == null) {
            Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "Can't find '" + this.getSiteUrl() + "/viwib.json'.");
            return null;
        }
        try {
            JSONObject root = (JSONObject) JSONValue.parseWithException(jsonStr);

            // parsing version
            String version = (String) root.get("version");
            if (version == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'version' attribute is missing from viwib.json");
                return null;
            }
            res.setVersion(version);

            // parsing name
            String name = (String) root.get("name");
            if (name == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'name' attribute is missing from viwib.json");
                return null;
            }
            res.setName(name);

            // parsing description
            String description = (String) root.get("description");
            res.setDescription(description);

            // parsing url
            String url = (String) root.get("url");
            if (url == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'url' attribute is missing from viwib.json");
                return null;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            res.setUrl(url);

            // parsing apiUrl
            String apiUrl = (String) root.get("apiUrl");
            if (apiUrl == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'apiUrl' attribute is missing from viwib.json");
                return null;
            }
            res.setApiUrl(apiUrl);

            // parsing needLogin
            Boolean needLogin = (Boolean) root.get("needLogin");
            if (needLogin == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'needLogin' attribute is missing from viwib.json");
                return null;
            }
            res.setNeedLogin(needLogin == Boolean.TRUE);

            // parsing keepAliveInterval
            Number keepAliveInterval = (Long) root.get("keepAliveInterval");
            if (keepAliveInterval == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'keepAliveInterval' attribute is missing from viwib.json");
                return null;
            }
            res.setKeepAliveInterval(keepAliveInterval.intValue());

            // parsing categories
            String err = parseCategories(root, res);
            if (err != null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, err);
                return null;
            }
            
            // parsing sort categories
            JSONArray sortCatsArr = (JSONArray) root.get("sortCategories");
            if (sortCatsArr == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'sortCategories' attribute is missing from viwib.json");
                return null;
            }
            err = parseSortCategories(sortCatsArr, res);
            if (err != null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, err);
                return null;
            }

            this.siteConfig = res;
            return this.siteConfig;
        } catch (Throwable ex) {
            Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private String parseCategories(JSONObject root, SiteConfig res) {
        JSONArray categories = (JSONArray) root.get("categories");
        if (categories == null) {
            return "The 'categories' attribute is missing from viwib.json";
        }

        ArrayList<Category> catList = new ArrayList<Category>();
        for (Object catObj : categories) {
            JSONObject cat = (JSONObject) catObj;
            Category category = new Category();
            String err = parseCategory(cat, category);
            if (err != null) {
                return err;
            }
            catList.add(category);
        }
        res.setCategories(catList.toArray(new Category[catList.size()]));

        return null;
    }

    private String parseSortCategories(JSONArray scArr, SiteConfig res) {
        ArrayList<SortCategory> sortCats = new ArrayList<SortCategory>();
        for (Object scObjObj : scArr) {
            JSONObject scObj = (JSONObject) scObjObj;


            String id = (String) scObj.get("id");
            if (id == null) {
                return "Missing 'id' attribute of SortCategory in viwib.json.";
            }

            String name = (String) scObj.get("name");
            if (name == null) {
                return "Missing 'name' attribute of SortCategory in viwib.json.";
            }

            Boolean descendent = (Boolean) scObj.get("isDescending");
            if (descendent == null) {
                return "Missing 'isDescending' attribute of SortCategory in viwib.json.";
            }
            SortCategory sc = new SortCategory(id, name);
            sc.setDesc(descendent == Boolean.TRUE);
            sortCats.add(sc);
        }

        res.setSortCategories(sortCats.toArray(new SortCategory[sortCats.size()]));

        return null;
    }

    private String parseCategory(JSONObject cat, Category res) {
        // parsing id
        String id = (String) cat.get("id");
        if (id == null) {
            return "The 'id' attribute of a category is missing in the viwib.json";
        }
        res.setId(id);

        // parsing name
        String name = (String) cat.get("name");
        if (name == null) {
            return "The 'name' attribute of a category is missing in the viwib.json";
        }
        res.setName(name);

        // parsing description
        String description = (String) cat.get("description");
        res.setDescription(description);

        // parsing adult
        Boolean adult = (Boolean) cat.get("adult");
        if (adult == null) {
            return "The 'adult' attribute of a category is missing in the viwib.json";
        }
        res.setAdult(adult == Boolean.TRUE);

        // parsing children
        ArrayList<Category> childList = new ArrayList<Category>();
        JSONArray children = (JSONArray) cat.get("children");
        if (children != null) {
            for (Object childObj : children) {
                JSONObject child = (JSONObject) childObj;
                Category c = new Category();
                String err = parseCategory(child, c);
                if (err != null) {
                    return err;
                }
                childList.add(c);
            }
        }
        if (childList.size() > 0) {
            res.setChildren(childList.toArray(new Category[childList.size()]));
        }

        return null;
    }

    @Override
    public SearchResult search(String term, Category[] categories, SortCategory sortCategory, int pageIndex) {
        String url = this.getConfig().getUrl() + this.getConfig().getApiUrl();
        url += "?";
        
        // ssid
        if (this.getConfig().isNeedLogin()) {
            url += "ssid=" +  URLCoder.encode(this.getSsid()) + "&";
        }
        
        // action
        url += "action=search&";
        
        // search term
        url += "s=" + URLCoder.encode(term);
        
        // categories
        url += "&cat=";
        String cats = "";
        boolean isFirst = true;
        for (Category cat : categories) {
            if (isFirst) {
                isFirst = false;
            } else {
                cats += ",";
            }
            cats += cat.getId();
        }
        url += URLCoder.encode(cats);
        
        // sort
        url += "&sort=" + URLCoder.encode(sortCategory.getId());
        
        // sort order
        String desc = "false";
        if (sortCategory.isDesc()) {
            desc = "true";
        }
        url+= "&desc=" + desc;
        
        // page index
        url += "&page=" + pageIndex;
        
        String jsonStr = this.request(url);
        if (jsonStr == null) {
            throw new RuntimeException("Can't access the server.");
        }
        try {
            SearchResult res = new SearchResult();
            JSONObject results = (JSONObject) JSONValue.parseWithException(jsonStr);
            
            // parsing total count
            Number totalCount = (Number) results.get("totalCount");
            if (totalCount == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'totalCount' attribute is missing from the result.");
                throw new RuntimeException("The 'totalCount' attribute is missing from the result.");
            }
            res.setTotalCount(totalCount.intValue());
            
            // parsing page index
            Number pi = (Number) results.get("pageIndex");
            if (pi == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'pageIndex' attribute is missing from the result.");
                throw new RuntimeException("The 'pageIndex' attribute is missing from the result.");
            }
            res.setPageIndex(pi.intValue());
            
            // parsing page size
            Number pageSize = (Number) results.get("pageIndex");
            if (pageSize == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'pageSize' attribute is missing from the result.");
                throw new RuntimeException("The 'pageSize' attribute is missing from the result.");
            }
            res.setPageSize(pageSize.intValue());
            
            // parsing results
            JSONArray resultsJArray = (JSONArray) results.get("results");
            if (resultsJArray == null) {
                Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, "The 'results' attribute is missing from the result.");
                throw new RuntimeException("The 'results' attribute is missing from the result.");
            }
            ArrayList<TorrentDescriptor> descriptors = new ArrayList<TorrentDescriptor>();
            for (Object o : resultsJArray) {
                JSONObject jo = (JSONObject) o;
                try {
                    TorrentDescriptor td = parseResultItem(jo);
                    descriptors.add(td);
                }catch (Throwable t) {
                    Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, t.getMessage());
                    throw new RuntimeException(t.getMessage());
                }
            }
            res.setResult(descriptors.toArray(new TorrentDescriptor[descriptors.size()]));
            
            return res;
        } catch (ParseException ex) {
            Logger.getLogger(CommonConnector.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Invalid response from the server.");
        }
        
    }
    
    private TorrentDescriptor parseResultItem(JSONObject resItem) {
        TorrentDescriptor res = new TorrentDescriptor();
        
        // parsing id
        String id = (String) resItem.get("id");
        if (id == null) {
            throw new RuntimeException("The 'id' attribute is missing from the result.");
        }
        res.setId(id);
        
        // parsing name
        String name = (String) resItem.get("name");
        if (name == null) {
            throw new RuntimeException("The 'name' attribute is missing from the result.");
        }
        res.setName(name);
        
        // parsing description
        String description = (String) resItem.get("description");
        if (description == null) {
            throw new RuntimeException("The 'description' attribute is missing from the result.");
        }
        res.setDescription(description);
        
        // parsing torrentUrl
        String torrentUrl = (String) resItem.get("torrentUrl");
        if (torrentUrl == null) {
            throw new RuntimeException("The 'torrentUrl' attribute is missing from the result.");
        }
        res.setTorrentUrl(torrentUrl);
        
        // parsing coverUrl
        String coverUrl = (String) resItem.get("coverUrl");
        res.setTorrentUrl(coverUrl);
        
        // parsing dateAdded
        String dateAdded = (String) resItem.get("dateAdded");
        if (dateAdded == null) {
            throw new RuntimeException("The 'dateAdded' attribute is missing from the result.");
        }
        res.setDateAdded(dateAdded);
        
        // parsing seeders
        Number seeds = (Number) resItem.get("seeders");
        if (seeds == null) {
            throw new RuntimeException("The 'seeders' attribute is missing from the result.");
        }
        res.setSeeds(seeds.intValue());
        
        // parsing leeches
        Number leechers = (Number) resItem.get("leechers");
        if (leechers == null) {
            throw new RuntimeException("The 'leechers' attribute is missing from the result.");
        }
        res.setLeeches(leechers.intValue());
        
        // parsing downloaded
        Number downloaded = (Number) resItem.get("downloaded");
        if (downloaded == null) {
            throw new RuntimeException("The 'downloaded' attribute is missing from the result.");
        }
        res.setDownloaded(downloaded.intValue());
        
        // parsing size
        String size = (String) resItem.get("size");
        if (size == null) {
            throw new RuntimeException("The 'size' attribute is missing from the result.");
        }
        res.setSize(size);
        
        // parsing categoryId
        String categoryId = (String) resItem.get("categoryId");
        if (categoryId == null) {
            throw new RuntimeException("The 'categoryId' attribute is missing from the result.");
        }
        res.setCategoryId(categoryId);
        
        return res;
    }

    @Override
    public void login(String username, String password) {
        if (!getConfig().isNeedLogin()) {
            return;
        }

        String url = this.getSiteUrl() + this.getConfig().getApiUrl();
        url += "?action=login";
        url += "&un=" + URLCoder.encode(username);
        url += "&pw=" + URLCoder.encode(password);

        String response = request(url);
        if (response == null) {
            throw new RuntimeException("The server does not answer.");
        }
        try {
            JSONObject jo = (JSONObject) new JSONParser().parse(response);
            String ssid = (String) jo.get("ssid");
            if (ssid == null) {
                String err = (String) jo.get("error");
                if (err == null) {
                    throw new RuntimeException("Invalid answer from the server");
                }
                throw new RuntimeException(err);
            }
            this.setSsid(ssid);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void keepAlive(String ssid) {
        if (!getConfig().isNeedLogin()) {
            return;
        }

        if (this.getSsid() == null) {
            return;
        }

        String url = this.getSiteUrl() + this.getConfig().getApiUrl();
        url += "?action=keepalive";
        url += "&ssid=" + URLCoder.encode(this.getSsid());

        request(url);
    }

    @Override
    public double getSeedProgress(String torrentId) {
        String url = this.getSiteUrl() + this.getConfig().getApiUrl();
        url += "?action=getSeedProgress";
        
        if (this.getSsid() != null) {
            url += "&ssid=" + URLCoder.encode(this.getSsid());
        }
        
        url += "&tid=" + URLCoder.encode(torrentId);
        
        String response = request(url);
        
        if (response == null) {
            System.out.println("The server does not answered for the getSeedProgress.");
            throw new RuntimeException("The server does not answered for the getSeedProgress request.");
        }
        
        try {
            JSONObject jo = (JSONObject) new JSONParser().parse(response);
            Double progress = (Double) jo.get("progress");
            if (progress == null) {
                throw new RuntimeException("The returned seed progress is null.");
            }
            if (progress < 0.0) {
                progress = 0.0;
            }
            return progress;
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void logout() {
        if (!getConfig().isNeedLogin()) {
            return;
        }

        if (this.getSsid() == null) {
            return;
        }

        String url = this.getSiteUrl() + this.getConfig().getApiUrl();
        url += "?action=logout";
        url += "&ssid=" + URLCoder.encode(this.getSsid());

        request(url);
    }
}
