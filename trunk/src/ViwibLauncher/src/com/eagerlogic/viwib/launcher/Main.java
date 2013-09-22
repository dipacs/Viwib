/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.launcher;

import java.awt.EventQueue;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;

/**
 *
 * @author dipacs
 */
public class Main {

    public static final String VIWIB_URL;
    private static final String REMOTE_VERSION_FILE_URL = "http://viwib.com/downloads/version";
    private static final String REMOTE_SPLASH_IMAGE_URL = "http://viwib.com/downloads/splash.png";
    private static final String REMOTE_VIWIB_JAR_URL = "http://viwib.com/downloads/Viwib.jar";

    static {
        String url = System.getProperty("user.home");
        if (!url.endsWith(File.separator)) {
            url += File.separator;
        }
        url = url + "Viwib" + File.separator;
        VIWIB_URL = url;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(SplashScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(SplashScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(SplashScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(SplashScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>



        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                final SplashScreen splashScreen = new SplashScreen();
                splashScreen.setVisible(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSaveNewVersion = true;
                        // checking local splash image
                        File splashFile = new File(Main.VIWIB_URL + "splash.png");
                        if (splashFile.exists()) {
                            splashScreen.setBackgroundImage(Main.VIWIB_URL + "splash.png");
                        }

                        // getting versions
                        splashScreen.setStatus("Checking updates...");
                        splashScreen.setProgress(1);
                        String localSplashVersion = "";
                        String remoteSplashVersion = "";
                        String localAppVersion = "";
                        String remoteAppVersion = "";

                        String[] localVersions = getLocalVersionFile();
                        if (localVersions != null && localVersions.length > 1) {
                            localSplashVersion = localVersions[0];
                            localAppVersion = localVersions[1];
                        }
                        String[] remoteVersions = getRemoteVersionFile();
                        if (remoteVersions != null && remoteVersions.length > 1) {
                            remoteSplashVersion = remoteVersions[0];
                            remoteAppVersion = remoteVersions[1];
                        } else {
                            isSaveNewVersion = false;
                            EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JOptionPane.showMessageDialog(null, "Can't get remote versions.", "ERROR!", JOptionPane.WARNING_MESSAGE);
                                }
                            });
                        }

                        // checking new splash screen version
                        if (!localSplashVersion.equals(remoteSplashVersion)) {
                            // updating splash image
                            splashScreen.setStatus("Checking updates - updating splash screen...");
                            splashScreen.setProgress(2);
                            byte[] data = requestBinary(REMOTE_SPLASH_IMAGE_URL);
                            if (data != null) {
                                File f = new File(VIWIB_URL);
                                f.mkdirs();
                                f = new File(VIWIB_URL + "splash.png");
                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(f);
                                    fos.write(data);
                                    fos.flush();
                                    splashScreen.setBackgroundImage(VIWIB_URL + "splash.png");
                                } catch (Throwable ex) {
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                } finally {
                                    if (fos != null) {
                                        try {
                                            fos.close();
                                        } catch (IOException ex) {
                                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            } else {
                                // TODO handle error
                            }
                        }


                        // checking new application version
                        splashScreen.setStatus("Checking updates - checking application updates...");
                        splashScreen.setProgress(10);
                        if (!localAppVersion.equals(remoteAppVersion)) {
                            splashScreen.setStatus("Checking updates - downloading new version: " + remoteAppVersion + " ...");
                            splashScreen.setProgress(11);
                            final Thread progressThread = new Thread() {
                                @Override
                                public void run() {
                                    while (splashScreen.getProgress() < 90) {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException ex) {
                                            return;
                                        }
                                        splashScreen.setProgress(splashScreen.getProgress() + 1);
                                    }
                                }
                            };
                            progressThread.start();
                            byte[] appData = requestBinary(REMOTE_VIWIB_JAR_URL);
                            if (appData != null) {
                                File f = new File(VIWIB_URL);
                                f.mkdirs();
                                f = new File(VIWIB_URL + "Viwib.jar");
                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(f);
                                    fos.write(appData);
                                    fos.flush();
                                    splashScreen.setBackgroundImage(VIWIB_URL + "splash.png");
                                } catch (final Throwable ex) {
                                    // TODO handle error
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    isSaveNewVersion = false;
                                    EventQueue.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            JOptionPane.showMessageDialog(null, "Can't save new version.\n\nDetails:\n" + ex.getMessage(), "ERROR!", JOptionPane.WARNING_MESSAGE);

                                        }
                                    });
                                } finally {
                                    if (fos != null) {
                                        try {
                                            fos.close();
                                        } catch (IOException ex) {
                                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            } else {
                                isSaveNewVersion = false;
                                EventQueue.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        JOptionPane.showMessageDialog(null, "Can't download new version. Starting previous version.", "WARNING!", JOptionPane.WARNING_MESSAGE);

                                    }
                                });
                            }
                            progressThread.interrupt();
                        }

                        // saving version file
                        if (isSaveNewVersion) {
                            splashScreen.setStatus("Saving version files...");
                            splashScreen.setProgress(90);
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(new File(VIWIB_URL + "version"));
                                fos.write((remoteSplashVersion + "," + remoteAppVersion).getBytes());
                            } catch (final Throwable ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                EventQueue.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        JOptionPane.showMessageDialog(null, "Can't save version file..\n\nDetails:\n" + ex.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE);
                                        System.exit(1);
                                    }
                                });
                            } finally {
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }

                        // starting application
                        splashScreen.setStatus("Starting application...");
                        splashScreen.setProgress(95);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ClassLoader cl = new URLClassLoader(new URL[]{new File(VIWIB_URL + "Viwib.jar").toURI().toURL()});
                                    Class c = cl.loadClass("com.eagerlogic.viwib.Main");
                                    c.getMethod("main", new String[]{}.getClass()).invoke(null, new Object[]{args});
                                } catch (final Throwable ex) {
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    EventQueue.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            JOptionPane.showMessageDialog(null, "Can't start application.\n\nDetails:\n" + ex.getMessage(), "ERROR!", JOptionPane.ERROR_MESSAGE);
                                            splashScreen.setVisible(false);
                                            System.exit(1);
                                        }
                                    });
                                }
                                EventQueue.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        splashScreen.setVisible(false);
                                    }
                                });
                            }
                        }).start();

                    }
                }).start();
            }
        });
    }

    private static String[] getLocalVersionFile() {
        File f = new File(VIWIB_URL + "version");
        if (!f.exists()) {
            return null;
        }

        InputStream is = null;
        try {
            is = new FileInputStream(f);
            String res = readAnswer(is);
            if (res == null) {
                return null;
            }
            String[] resA = res.split(",");
            return resA;
        } catch (Throwable ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static String[] getRemoteVersionFile() {
        String res = request(REMOTE_VERSION_FILE_URL);
        if (res == null) {
            return null;
        }
        String[] resA = res.split(",");
        return resA;
    }

    private static String request(String u) {
        URL url;
        try {
            url = new URL(u);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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

    private static byte[] requestBinary(String u) {
        URL url;
        try {
            url = new URL(u);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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

            byte[] response = readBinaryAnswer(connection);
            return response;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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
            return readAnswer(is);
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

    private static String readAnswer(InputStream is) {
        try {
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

    private static byte[] readBinaryAnswer(HttpURLConnection connection) {
        InputStream is = null;
        try {
            is = connection.getInputStream();
            return readBinaryAnswer(is);
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

    private static byte[] readBinaryAnswer(InputStream is) {
        try {
            int readed;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[65536];
            while ((readed = is.read(buff)) > 0) {
                baos.write(buff, 0, readed);
            }

            buff = baos.toByteArray();
            baos.close();
            return buff;
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
