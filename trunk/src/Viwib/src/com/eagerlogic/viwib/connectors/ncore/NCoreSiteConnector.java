/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.connectors.ncore;

import com.eagerlogic.viwib.connectors.Category;
import com.eagerlogic.viwib.connectors.ASiteConnector;
import com.eagerlogic.viwib.connectors.SearchResult;
import com.eagerlogic.viwib.connectors.SiteConfig;
import com.eagerlogic.viwib.connectors.SortCategory;
import com.eagerlogic.viwib.connectors.TorrentDescriptor;
import com.eagerlogic.viwib.utils.URLCoder;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author dipacs
 */
public class NCoreSiteConnector extends ASiteConnector {

    private String cookieNick;
    private String cookiePass;
    private Category catMovie;
    private Category catMovieXvidHu = new Category("xvid_hun", "XviD/HU", null, false);
    private Category catMovieXvidEn = new Category("xvid", "XviD/EN", null, false);
    private Category catMovieDvdrHu = new Category("dvd_hun", "DVDR/HU", null, false);
    private Category catMovieDvdrEn = new Category("dvd", "DVDR/EN", null, false);
    private Category catMovieDvd9Hu = new Category("dvd9_hun", "DVD9/HU", null, false);
    private Category catMovieDvd9En = new Category("dvd9", "DVD9/EN", null, false);
    private Category catMovieHdHu = new Category("hd_hun", "HD/HU", null, false);
    private Category catMovieHdEn = new Category("hd", "HD/EN", null, false);
    private Category catSeries;
    private Category catSeriesXvidHu = new Category("xvidser_hun", "XviD/HU", null, false);
    private Category catSeriesXvidEn = new Category("xvidser", "XviD/EN", null, false);
    private Category catSeriesDvdrHu = new Category("dvdser_hun", "DVDR/HU", null, false);
    private Category catSeriesDvdrEn = new Category("dvdser", "DVDR/EN", null, false);
    private Category catSeriesHdHu = new Category("hdser_hun", "HD/HU", null, false);
    private Category catSeriesHdEn = new Category("hdser", "HD/EN", null, false);
    private Category catXxx;
    private Category catXxxXvid = new Category("xxx_xvid", "XviD", null, true);
    private Category catXxxDvdr = new Category("xxx_dvd", "DVDR", null, true);
    private Category catXxxHd = new Category("xxx_hd", "HD", null, true);
    private final Category[] categories;

    public NCoreSiteConnector() {
        super("http://ncore.cc");
        // Create an ssl socket factory with our all-trusting manager



        catMovie = new Category("film", "Film", null, false);
        catMovie.setChildren(new Category[]{
            catMovieXvidHu,
            catMovieXvidEn,
            catMovieDvdrHu,
            catMovieDvdrEn,
            catMovieDvd9Hu,
            catMovieDvd9En,
            catMovieHdHu,
            catMovieHdEn,});

        catSeries = new Category("sorozat", "Sorozat", null, false);
        catSeries.setChildren(new Category[]{
            catSeriesXvidHu,
            catSeriesXvidEn,
            catSeriesDvdrHu,
            catSeriesDvdrEn,
            catSeriesHdHu,
            catSeriesHdEn,});

        catXxx = new Category("xxx", "XXX", null, true);
        catXxx.setChildren(new Category[]{
            catXxxXvid,
            catXxxDvdr,
            catXxxHd,});

        this.categories = new Category[]{catMovie, catSeries, catXxx};
    }

    @Override
    public SiteConfig getConfig() {
        SiteConfig res = new SiteConfig();
        res.setName("nCore");
        res.setDescription("Never ending revolution.");
        res.setKeepAliveInterval(0);
        res.setNeedLogin(true);
        res.setUrl("http://ncore.cc");
        res.setCategories(categories);

        SortCategory[] sortCats = new SortCategory[6];
        sortCats[0] = new SortCategory("fid", "Feltöltve");
        sortCats[0].setDesc(true);
        sortCats[1] = new SortCategory("name", "Név");
        sortCats[2] = new SortCategory("size", "Méret");
        sortCats[3] = new SortCategory("times_completed", "Letöltve");
        sortCats[4] = new SortCategory("seeders", "Seederek");
        sortCats[5] = new SortCategory("leeches", "Leecherek");
        res.setSortCategories(sortCats);
        return res;
    }

    @Override
    public SearchResult search(String term, Category[] categories, SortCategory sortCategory, int pageIndex) {
        //https://ncore.cc/torrents.php?miszerint=name&hogyan=DESC&tipus=kivalasztottak_kozott&kivalasztott_tipus=xvid_hun,xvid,dvd_hun,dvd,dvd9_hun,dvd9,hd_hun,hd,xvidser_hun,xvidser,dvdser_hun,dvdser,hdser_hun,hdser,xxx_xvid,xxx_dvd,xxx_hd&mire=total%20total&miben=name


        URL url = null;
        try {
            String u = "https://ncore.cc/torrents.php?";

            // rendezés
            if (sortCategory == null) {
                u += "miszerint=fid&hogyan=DESC";
            } else {
                u += "miszerint=" + sortCategory.getId() + "&hogyan=" + (sortCategory.isDesc() ? "DESC" : "ASC") + "";
            }

            // szűrés
            if (categories != null && categories.length > 0) {
                u += "&tipus=kivalasztottak_kozott&kivalasztott_tipus=";
                boolean isFirst = true;
                for (Category cat : categories) {
                    if (cat.getChildren() == null) {
                        if (isFirst) {
                            isFirst = false;
                        } else {
                            u += ",";
                        }
                        u += cat.getId();
                    }
                }
            }

            // keresés
            if (term != null) {
                term = term.trim();
            }
            if (!"".equals(term)) {
                // TODO UrlEncode
                u += "&mire=" + term + "&miben=name";
            }

            // paging
            if (pageIndex > 0) {
                u += "&oldal=" + (pageIndex + 1);
            }

            url = new URL(u);
        } catch (MalformedURLException ex) {
            Logger.getLogger(NCoreSiteConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);
            this.getCookieManager().setCookies(connection);
            connection.connect();

            String response = readAnswer(connection);
            this.getCookieManager().storeCookies(connection);

            if (response == null) {
                return null;
            }
            response = response.trim();
            if (response.equals("")) {
                return null;
            }

            ArrayList<TorrentDescriptor> res = new ArrayList<TorrentDescriptor>();

            int idx = 0;
            while (true) {
                idx = response.indexOf("<div class=\"box_torrent\">", idx + 1);
                if (idx < 0) {
                    break;
                }

                TorrentDescriptor header = processTorrentHeader(response, idx);
                if (header != null) {
                    res.add(header);
                }
            }

            SearchResult sr = new SearchResult();
            sr.setPageIndex(pageIndex);
            // TODO get it from html
            sr.setPageSize(100);
            // TODO read it from html
            sr.setTotalCount(1000);
            sr.setResult(res.toArray(new TorrentDescriptor[res.size()]));

            return sr;
        } catch (IOException ex) {
            Logger.getLogger(NCoreSiteConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException ex) {
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private TorrentDescriptor processTorrentHeader(String html, int index) {
        int boxEndIndex = getBoxEndIndex(html, index);
        TorrentDescriptor res = new TorrentDescriptor();

        int idxStart;
        int idxEnd;

        // id
        idxStart = html.indexOf("<a href=\"torrents.php?action=details&id=", index);
        idxStart = html.indexOf("&id=", idxStart) + 4;
        idxEnd = html.indexOf("\"", idxStart);
        res.setId(html.substring(idxStart, idxEnd));

        // name
        idxStart = index;
        idxStart = html.indexOf("<nobr>", index);
        idxEnd = html.indexOf("</nobr>", index);
        if (idxStart < 0 || idxEnd < 0) {
            return null;
        }
        idxStart += 6; // cutting <nobr>
        res.setName(html.substring(idxStart, idxEnd));
        res.setName(res.getName().trim());

        // cover
        idxStart = html.indexOf("<div class=\"infobar\">", index);
        if (idxStart < boxEndIndex) {
            idxStart = html.indexOf("onmouseover=\"mutat('https://img", idxStart);
            if (idxStart > -1 && idxStart < boxEndIndex) {
                idxStart = idxStart + 20;
                idxEnd = html.indexOf("',", idxStart);
                if (idxEnd > -1) {
                    res.setCoverUrl(html.substring(idxStart, idxEnd));
                }
            }
        }

        //size
        idxStart = html.indexOf("<div class=\"box_meret2\">", index);
        if (idxStart < boxEndIndex) {
            idxStart = idxStart + 24;
            idxEnd = html.indexOf("</div>", idxStart);
            if (idxEnd > -1 && idxEnd < boxEndIndex) {
                res.setSize(html.substring(idxStart, idxEnd));
            }
        }

        //downloaded
        idxStart = html.indexOf("<div class=\"box_d2\">", index);
        if (idxStart < boxEndIndex) {
            idxStart = idxStart + 20;
            idxEnd = html.indexOf("</div>", idxStart);
            if (idxEnd > -1 && idxEnd < boxEndIndex) {
                String dStr = html.substring(idxStart, idxEnd);
                try {
                    res.setDownloaded(Long.parseLong(dStr));
                } catch (Throwable t) {
                }
            }
        }

        //seeds
        idxStart = html.indexOf("<div class=\"box_s2\">", index);
        if (idxStart < boxEndIndex) {
            idxStart = html.indexOf("&amp;peers=1#peers\">", idxStart);
            if (idxStart > -1 && idxStart < boxEndIndex) {
                idxStart = idxStart + 20;
                idxEnd = html.indexOf("</a>", idxStart);
                if (idxEnd > -1) {
                    String dStr = html.substring(idxStart, idxEnd);
                    try {
                        res.setSeeds(Long.parseLong(dStr));
                    } catch (Throwable t) {
                    }
                }
            }
        }

        //leeches
        idxStart = html.indexOf("<div class=\"box_l2\">", index);
        if (idxStart < boxEndIndex) {
            idxStart = html.indexOf("&amp;peers=1#peers\">", idxStart);
            if (idxStart > -1 && idxStart < boxEndIndex) {
                idxStart = idxStart + 20;
                idxEnd = html.indexOf("</a>", idxStart);
                if (idxEnd > -1) {
                    String dStr = html.substring(idxStart, idxEnd);
                    try {
                        res.setLeeches(Long.parseLong(dStr));
                    } catch (Throwable t) {
                    }
                }
            }
        }

        res.setTorrentUrl("https://ncore.cc/torrents.php?action=download&id=" + res.getId());

        return res;
    }

    private int getBoxEndIndex(String html, int startIndex) {
        int res = html.indexOf("<div class=\"box_torrent\">", startIndex + 1);
        if (res < 0) {
            res = html.length();
        }
        return res;
    }

    private int getDivEndIndex(String html, int startIndex) {
        int actIndex = startIndex;
        int level = 1;
        while (level > 0) {
            int closeIndex = html.indexOf("</div", actIndex + 1);
            int openIndex = html.indexOf("<div", actIndex + 1);
            if (openIndex > -1) {
                if (closeIndex > -1) {
                    if (openIndex < closeIndex) {
                        // open tag
                        level++;
                        actIndex = openIndex;
                    } else {
                        // close tag
                        level--;
                        actIndex = closeIndex;
                    }
                } else {
                    // open tag
                    level++;
                    actIndex = openIndex;
                }
            } else {
                // no open div
                if (closeIndex > -1) {
                    // close tag
                    level--;
                    actIndex = closeIndex;
                } else {
                    return actIndex;
                }
            }
        }
        return actIndex;
    }

    @Override
    public void login(String username, String password) {
        this.login(username, password, 0);
    }
    public void login(String username, String password, int retryCount) {
        if (retryCount > 2) {
            return;
        }
        URL url = null;
        try {
            url = new URL("https://ncore.cc/login.php");
        } catch (MalformedURLException ex) {
            Logger.getLogger(NCoreSiteConnector.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        try {
            String params = "set_lang=hu&submitted=1&nev=" + URLCoder.encode(username) + "&submit=" + URLCoder.encode("Belépés") + "!&pass=" + URLCoder.encode(password) + "&ne_leptessen_ki=1";
            byte[] paramBytes = params.getBytes("UTF-8");

            connection = (HttpURLConnection) url.openConnection();
            
            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection)connection).setSSLSocketFactory(this.getSslSocketFactory());
            }

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(paramBytes.length));
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            connection.setUseCaches(false);
            
            connection.connect();

            

            wr = new DataOutputStream(connection.getOutputStream());
            wr.write(paramBytes);
            wr.flush();
            
            Thread.sleep(200);

            this.getCookieManager().storeCookies(connection);

            String nick = "";
            String pass = "";

            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
            if (cookies == null) {
                throw new RuntimeException("Missing cookies.");
            }

            for (String s : cookies) {
                s = s.trim();
                if (s.startsWith("nick=")) {
                    nick = s.substring(5);
                } else if (s.startsWith("pass=")) {
                    pass = s.substring(5);
                }
            }

            if (nick == null || pass == null) {
                throw new RuntimeException("Nick and pass cookie can not be found.");
            }

            nick = nick.substring(0, nick.indexOf(";"));
            pass = pass.substring(0, pass.indexOf(";"));

            nick = nick.trim();
            pass = pass.trim();

            if (nick.equals("deleted") || pass.equals("deleted")) {
                throw new RuntimeException("Nick and pass cookie can not be found.");
            }

            this.cookieNick = nick;
            this.cookiePass = pass;

            this.setSsid(nick);
        } catch (SocketException ex) {
            if (retryCount >= 2) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
            this.login(username, password, retryCount + 1);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException ex) {
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public void keepAlive(String ssid) {
    }

    @Override
    public double getSeedProgress(String torrentId) {
        return 0.0;
    }

    @Override
    public void logout() {
    }
}
