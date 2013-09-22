/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.torrent.manager;

import com.eagerlogic.viwib.Viwib;
import com.eagerlogic.viwib.config.Config;
import com.eagerlogic.viwib.ui.MainFrame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dipacs
 */
public class TorrentManager implements Serializable {

    private static final long serialVersionUID = 1l;
    private static TorrentManager instance;

    public static synchronized TorrentManager getInstance() {
        if (instance == null) {
            synchronized (TorrentManager.class) {
                if (instance == null) {
                    TorrentManager.load();
                }
            }
        }
        return instance;
    }

    private static void load() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(Config.VIWIB_URL + "activeTorrents.dsc"));
            ois = new ObjectInputStream(fis);
            instance = (TorrentManager) ois.readObject();

        } catch (Throwable ex) {
            Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, "Can't load torrent manager, creating new.");
            instance = new TorrentManager();
            save();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (final TorrentFile tf : instance.torrents) {
            tf.init();
            try {
                tf.start();
            } catch (IOException ex) {
                Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public static void save() {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(new File(Config.VIWIB_URL + "activeTorrents.dsc"));
            oos = new ObjectOutputStream(fos);
            oos.writeObject(getInstance());
        } catch (Throwable ex) {
            Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    private final ArrayList<TorrentFile> torrents = new ArrayList<TorrentFile>();
    private transient int playingTorrent = -1;
    private transient Thread thread;

    private TorrentManager() {
    }

    public void addTorrent(TorrentFile torrentFile) {
        synchronized (this) {
            torrentFile.init();
            int index = indexOfTorrentFile(torrentFile);
            if (index < 0) {
                this.torrents.add(torrentFile);
                TorrentManager.save();
            }
        }
    }

    public void removeTorrent(TorrentFile torrentFile) {
        synchronized (this) {
            this.torrents.remove(torrentFile);
            TorrentManager.save();
        }
    }

    public void removeTorrent(int index) {
        synchronized (this) {
            this.torrents.remove(index);
            TorrentManager.save();
        }
    }

    public ArrayList<TorrentFile> getTorrents() {
        synchronized (this) {
            return this.torrents;
        }
    }

    public void playTorrent(TorrentFile torrentFile, int playingFileIndex) {
        synchronized (this) {
            this.addTorrent(torrentFile);
            int index = this.indexOfTorrentFile(torrentFile);
            this.playingTorrent = index;
            for (int i = 0; i < this.torrents.size(); i++) {
                TorrentFile tf = this.torrents.get(i);
                if (i == index) {
                    tf.setPreferredFile(tf.getFiles()[playingFileIndex]);
                    if (!tf.isStarted()) {
                        if (tf.getSeedProgress() < 1.0) {
                            try {
                                tf.start();
                            } catch (IOException ex) {
                                Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                } else {
                    tf.setPreferredFile(null);
                    if (tf.isStarted()) {
                        if (tf.isDownloading()) {
                            tf.stop();
                        }
                    }
                }
            }
        }
    }

    public void stopPlayingTorrent() {
        synchronized (this) {
            int pt = playingTorrent;
            this.playingTorrent = -1;
            for (int i = 0; i < this.torrents.size(); i++) {
                TorrentFile tf = this.torrents.get(i);
                tf.setPreferredFile(null);
                if (!tf.isStarted()) {
                    if (tf.getSeedProgress() < 1.0) {
                        try {
                            tf.start();
                        } catch (IOException ex) {
                            Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    public void startAllTorrent() {
        synchronized (this) {
            for (TorrentFile tf : this.torrents) {
                if (!tf.isStarted()) {
                    if (tf.getSeedProgress() < 1.0) {
                        try {
                            tf.start();
                        } catch (IOException ex) {
                            Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    public void stopAllTorrent() {
        synchronized (this) {
            for (TorrentFile tf : this.torrents) {
                if (tf.isStarted()) {
                    tf.stop();
                }
            }
        }
    }

    public int getPlayingTorrentIndex() {
        synchronized (this) {
            return this.playingTorrent;
        }
    }

    public void start() {
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long lastSeedProgressCheck = System.currentTimeMillis();
                    while (!thread.isInterrupted()) {
                        try {
                            Thread.sleep(5 * 1000);
                        } catch (InterruptedException ex) {
                            break;
                        }
                        synchronized (TorrentManager.this) {
                            for (TorrentFile tf : torrents) {
                                if (tf.getTorrent() != null) {
                                    //System.out.println("Ticking torrent");
                                    tf.getTorrent().tick();
                                }
                            }
                        }
                        if (lastSeedProgressCheck <= System.currentTimeMillis() - (1000 * 60 * 60)) {
                            checkSeedProgress();
                        }
                    }
                    synchronized (TorrentManager.this) {
                        for (TorrentFile tf : torrents) {
                            tf.getTorrent().tick();
                        }
                    }
                } finally {

                    Logger.getLogger(TorrentManager.class.getName()).log(Level.SEVERE, "Announce thread stopped.");
                }
            }
        });
        this.thread.start();
    }

    private void checkSeedProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TorrentFile[] torrentFiles = null;
                synchronized (this) {
                    torrentFiles = torrents.toArray(new TorrentFile[0]);
                }
                for (TorrentFile tf : torrentFiles) {
                    try {
                        tf.setSeedProgress(Viwib.getConnector().getSeedProgress(tf.getId()));
                        // TODO check seed progress and remove torrent if needed
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }, "TSeedProgressCheck").start();
    }

    public void stop() {
        this.stopAllTorrent();

        if (thread != null) {
            thread.interrupt();
            int cnt = 0;
            while (thread.isAlive()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
                if (cnt > 100) {
                    thread.stop();
                    return;
                }
            }
        }


    }

    public int indexOfTorrentFile(TorrentFile tf) {
        synchronized (this) {
            byte[] tfHash = tf.getMetafile().getInfoSha1();
            for (int i = 0; i < this.torrents.size(); i++) {
                if (isHashEquals(tfHash, this.torrents.get(i).getMetafile().getInfoSha1())) {
                    return i;
                }
            }
            return -1;
        }
    }

    private boolean isHashEquals(byte[] hash1, byte[] hash2) {
        if (hash1.length != hash2.length) {
            return false;
        }

        for (int i = 0; i < hash1.length; i++) {
            if (hash1[i] != hash2[i]) {
                return false;
            }
        }

        return true;
    }
}
