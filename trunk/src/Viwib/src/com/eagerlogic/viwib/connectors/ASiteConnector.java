/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.connectors;

import com.eagerlogic.viwib.MyCookieManager;
import com.eagerlogic.viwib.connectors.ncore.NCoreSiteConnector;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author dipacs
 */
public abstract class ASiteConnector {
    
    private MyCookieManager cm;
     TrustManager[] trustAllCerts;
    // Install the all-trusting trust manager
    SSLContext sslContext;
    // Create an ssl socket factory with our all-trusting manager
    SSLSocketFactory sslSocketFactory;
    private final String siteUrl;
    private String ssid;

    public ASiteConnector(String siteUrl) {
        this.siteUrl = this.correctSiteUrl(siteUrl);
        
        trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};
        
        try {
            // Install the all-trusting trust manager
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Throwable ex) {
            Logger.getLogger(NCoreSiteConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.cm = new MyCookieManager();
    }
    
    private String correctSiteUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
    
    public abstract SiteConfig getConfig();
    public abstract SearchResult search(String term, Category[] categories, SortCategory sortCategory, int pageIndex);
    public abstract void login(String username, String password);
    public abstract void logout();
    public abstract void keepAlive(String ssid);
    public abstract double getSeedProgress(String torrentId);
    
    public byte[] getTorrentFileFromUrl(String torrentUrl) {
        URL url = null;
        try {
            String u = torrentUrl;
            url = new URL(u);
        } catch (MalformedURLException ex) {
            Logger.getLogger(NCoreSiteConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            
            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
            }
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            cm.setCookies(connection);

            InputStream is = connection.getInputStream();
            cm.storeCookies(connection);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[65536];
            int readed = 0;
            while ((readed = is.read(buff)) > 0) {
                baos.write(buff, 0, readed);
            }
            buff = baos.toByteArray();
            baos.close();
            return buff;
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
    
    public Image getImageFromUrl(String imageUrl) {
        URL url = null;
        try {
            String u = imageUrl;
            url = new URL(u);
        } catch (MalformedURLException ex) {
            Logger.getLogger(NCoreSiteConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            
            ((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            cm.setCookies(connection);

            InputStream is = connection.getInputStream();
            cm.storeCookies(connection);
            Image res = new Image(is);
            is.close();
            return res;
        } catch (IOException ex) {
            //Logger.getLogger(NCoreSiteConnector.class.getName()).log(Level.SEVERE, null, ex);
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
    
    protected String request(String u) {
        URL url;
        try {
            url = new URL(u);
        } catch (MalformedURLException ex) {
            Logger.getLogger(NCoreSiteConnector.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
            }
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);
            cm.setCookies(connection);
            connection.connect();

            String response = readAnswer(connection);
            return response;
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
    
    protected String readAnswer(HttpURLConnection connection) {
        InputStream is = null;
        try {
            is = connection.getInputStream();
            int readed;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[65536];
            while ((readed = is.read(buff)) > 0) {
                baos.write(buff, 0, readed);
            }

            buff = baos.toByteArray();
            String res = new String(buff, "UTF-8");
            baos.close();
            return res;
        } catch (Throwable t) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    // nothing to do here
                }
            }
        }
    }

    protected MyCookieManager getCookieManager() {
        return cm;
    }

    protected String getSiteUrl() {
        return siteUrl;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public TrustManager[] getTrustAllCerts() {
        return trustAllCerts;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }
    
}
