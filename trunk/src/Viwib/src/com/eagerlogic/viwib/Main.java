/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib;

import com.eagerlogic.viwib.config.Config;
import static com.eagerlogic.viwib.config.Config.VIWIB_URL;
import com.eagerlogic.viwib.connectors.ncore.NCoreSiteConnector;
import com.eagerlogic.viwib.torrent.manager.TorrentManager;
import com.eagerlogic.viwib.ui.MainFrame;
import com.eagerlogic.viwib.utils.Fake;
import com.eagerlogic.viwib.utils.JarUtils;
import com.sun.jna.NativeLibrary;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.UIManager;

/**
 *
 * @author dipacs
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Config.load();

        int vlcVersion = -1;
        try {
            vlcVersion = Integer.parseInt(Config.getVlcVersion());
        } catch (Throwable t) {
        }

        if (vlcVersion < Config.ACT_VLC_VERSION) {
            // copying vlc
            try {
                System.out.println("copying VLC ...");
                File f = new File(Config.VLC_URL);
                // TODO delete previous vlc version
                f.mkdirs();
                JarUtils.unjar(new Fake().getClass().getResourceAsStream("/com/eagerlogic/viwib/res/vlc/vlc.jar"), f);
                Config.setVlcVersion("" + Config.ACT_VLC_VERSION);
                Config.save();
                System.out.println("VLC copied successfully.");
            } catch (Throwable ex) {
                Logger.getLogger(Viwib.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        loadVlc();
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        new Thread(new Runnable() {
            @Override
            public void run() {
                //TorrentManager.getInstance().startAllTorrent();
                TorrentManager.getInstance().start();
            }
        }).start();
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                new MainFrame().setVisible(true);
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.exit(1);
                }
            }
        });
        
        sendUsageStatistic();
    }

    public static void loadVlc() {
//        String os = System.getProperty("os.name").toLowerCase();
//        boolean is64bit = "64".equals(System.getProperty("sun.arch.data.model"));
        if (Config.isWindows()) {
            // windows
            if (Config.is64bit()) {
                System.out.println("Loading 64 bit Windows Libraries.");
                NativeLibrary.addSearchPath("libvlc", Config.VLC_URL + "windows" + File.separator + "x64" + File.separator);
            } else {
                System.out.println("Loading 32 bit Windows Libraries.");
                NativeLibrary.addSearchPath("libvlc", Config.VLC_URL + "windows" + File.separator + "x86" + File.separator);
            }
        } else if (Config.isMac()) {
            // mac
        } else if (Config.isLinux()) {
            // linus
        } else if (Config.isSolaris()) {
            // solaris
        }
    }
    
    private static void sendUsageStatistic() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                request("http://viwib.com/ustat.php?uuid=" + Config.getID());
            }
        }).start();
    }
    
    private static String request(String u) {
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
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);
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
    
    private static String readAnswer(HttpURLConnection connection) {
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
    
}
