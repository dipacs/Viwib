/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dipacs
 */
public class Config {
    
    public static final int ACT_VLC_VERSION = 1;
    
    public static final String VIWIB_URL;
    public static final String CONFIG_FILE_URL;
    public static final String VLC_URL;
    public static final String VISIBLE_VERSION = "1.0.0.111 Alpha";
    public static final String VERSION = "1.0.0.111 Alpha";
    
    static {
        String url = System.getProperty("user.home");
        if (!url.endsWith(File.separator)) {
            url += File.separator;
        }
        url = url + "Viwib" + File.separator;
        VIWIB_URL = url;
        url += "config.props";
        CONFIG_FILE_URL = url;
        VLC_URL = VIWIB_URL + "vlc" + File.separator;
    }
    
    private static String saveLocation;
    private static String vlcVersion;
    private static String ID;

    public static String getVlcVersion() {
        return vlcVersion;
    }

    public static void setVlcVersion(String vlcVersion) {
        Config.vlcVersion = vlcVersion;
    }
    
    public static void load() {
        unload();
        Properties props = new Properties();
        FileInputStream fis = null;
        InputStreamReader reader = null;
        try {
            File file = new File(CONFIG_FILE_URL);
            if (!file.exists()) {
                ID = UUID.randomUUID().toString();
                return;
            }
            fis = new FileInputStream(file);
            reader = new InputStreamReader(fis, "UTF-8");
            props.load(fis);
            saveLocation = props.getProperty("saveLocation");
            vlcVersion = props.getProperty("vlcVersion");
            ID = props.getProperty("uuid");
            if (ID == null) {
                ID = UUID.randomUUID().toString();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // who cares
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    // who cares
                }
            }
        }
    }
    
    private static void unload() {
        saveLocation = null;
    }
    
    public static void save() {
        Properties props = new Properties();
        FileOutputStream fos = null;
        OutputStreamWriter writer = null;
        try {
            File file = new File(VIWIB_URL);
            file.mkdirs();
            file = new File(CONFIG_FILE_URL);
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, "UTF-8");
            setProperty("saveLocation", saveLocation, props);
            setProperty("vlcVersion", vlcVersion, props);
            setProperty("uuid", ID, props);
            props.store(writer, "");
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    // who cares
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    // who cares
                }
            }
        }
    }
    
    private static void setProperty(String key, String value, Properties props) {
        if (value == null) {
            props.remove(key);
        } else {
            props.setProperty(key, value);
        }
    }

    public static String getSaveLocation() {
        return saveLocation;
    }

    public static void setSaveLocation(String saveLocation) {
        Config.saveLocation = saveLocation;
    }

    public static String getID() {
        return ID;
    }

    public static void setID(String ID) {
        Config.ID = ID;
    }
    
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.toLowerCase().indexOf("win") > -1;
    }
    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.indexOf("mac") > -1;
    }
    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.indexOf("nix") >= -1 || os.indexOf("nux") >= -1 || os.indexOf("aix") > -1;
    }
    public static boolean isSolaris() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.indexOf("sunos") > -1;
    }
    
    public static boolean is64bit() {
        return "64".equals(System.getProperty("sun.arch.data.model"));
    }
    
}
