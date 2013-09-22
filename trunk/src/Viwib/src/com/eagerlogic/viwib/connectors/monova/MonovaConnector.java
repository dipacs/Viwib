/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.connectors.monova;

import com.eagerlogic.viwib.connectors.ASiteConnector;
import com.eagerlogic.viwib.connectors.Category;
import com.eagerlogic.viwib.connectors.SearchResult;
import com.eagerlogic.viwib.connectors.SiteConfig;
import com.eagerlogic.viwib.connectors.SortCategory;
import com.eagerlogic.viwib.connectors.TorrentDescriptor;
import com.eagerlogic.viwib.torrent.manager.TorrentFile;
import com.eagerlogic.viwib.torrent.manager.TorrentManager;
import com.eagerlogic.viwib.utils.URLCoder;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author dipacs
 */
public class MonovaConnector extends ASiteConnector {

    private SiteConfig siteConfig = null;

    public MonovaConnector() {
        super("http://www.monova.org/");
    }

    @Override
    public synchronized SiteConfig getConfig() {
        if (siteConfig == null) {
            SiteConfig res = new SiteConfig();
            res.setApiUrl(null);
            res.setName("MONOVA");
            res.setNeedLogin(false);
            res.setVersion("1.0");

            Category[] categories = new Category[3];
            categories[0] = new Category("movies", "Movies", "Movies", false);
            categories[1] = new Category("tv", "TV", "TV Shows", false);
            categories[2] = new Category("anime", "Anime", "Anime", false);
            res.setCategories(categories);

            SortCategory[] sCats = new SortCategory[7];
            sCats[0] = new SortCategory("added", "Added");
            sCats[1] = new SortCategory("name", "Name");
            sCats[2] = new SortCategory("size", "Size");
            sCats[3] = new SortCategory("seeds", "Seeds");
            sCats[4] = new SortCategory("peers", "Peers");
            sCats[5] = new SortCategory("health", "Health");
            sCats[6] = new SortCategory("categories", "Category");
            res.setSortCategories(sCats);

            siteConfig = res;
        }

        return siteConfig;
    }

    @Override
    public SearchResult search(String term, Category[] categories, SortCategory sortCategory, int pageIndex) {
        try {
            String url = "http://www.monova.org/rss.php?";

            // search term
            url += "term=";
            if (term != null) {
                url += URLCoder.encode(term);
            }

            // sort category
            url += "&order=";
            if (sortCategory != null) {
                url += URLCoder.encode(sortCategory.getId());
            }

            //        String result = this.request(url);

            // parse result
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("item");

            ArrayList<TorrentDescriptor> resA = new ArrayList<TorrentDescriptor>();
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    TorrentDescriptor descriptor = parseNode(node);
                    if (isGoodCategory(descriptor, categories)) {
                        resA.add(descriptor);
                    }
                }
            }

            SearchResult res = new SearchResult();
            res.setPageIndex(0);
            res.setPageSize(100);
            res.setTotalCount(resA.size());
            res.setResult(resA.toArray(new TorrentDescriptor[resA.size()]));
            return res;
        } catch (Throwable ex) {
            Logger.getLogger(MonovaConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private TorrentDescriptor parseNode(Node node) {
        TorrentDescriptor res = new TorrentDescriptor();

        String title = "";
        String torrentUrl = "";
        String dateAdded = "";
        String category = "";
        String seeds = "";
        String peers = "";
        String size = "";
        
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                if ("title".equalsIgnoreCase(e.getTagName())) {
                    title = e.getTextContent().trim();
                } else if ("pubdate".equalsIgnoreCase(e.getTagName())) {
                    dateAdded = e.getTextContent().trim();
                } else if ("category".equalsIgnoreCase(e.getTagName())) {
                    category = e.getTextContent().trim();
                } else if ("enclosure".equalsIgnoreCase(e.getTagName())) {
                    torrentUrl = e.getAttribute("url").trim();
                } else if ("description".equalsIgnoreCase(e.getTagName())) {
                    String desc = e.getTextContent().trim();
                    // TODO process desc
                }
            }
        }

        res.setCategoryId(category);
        res.setDateAdded(dateAdded);
        res.setId(title);
        res.setName(title);
        res.setSize(size);
        res.setTorrentUrl(torrentUrl);
        
        
        return res;
    }

    private boolean isGoodCategory(TorrentDescriptor desc, Category[] categories) {
        for (Category cat : categories) {
            if (cat.getId().equalsIgnoreCase(desc.getCategoryId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void login(String username, String password) {
    }

    @Override
    public void logout() {
    }

    @Override
    public void keepAlive(String ssid) {
    }

    @Override
    public double getSeedProgress(String torrentId) {
        TorrentFile[] torrents = TorrentManager.getInstance().getTorrents().toArray(new TorrentFile[0]);
        for (TorrentFile tf : torrents) {
            if (tf.getId() != null && tf.getId().equals(torrentId)) {
                return (System.currentTimeMillis() - tf.getAdded()) / ((double) (1000 * 60 * 60 * 24 * 3));
            }
        }
        return 1.1;
    }
}
