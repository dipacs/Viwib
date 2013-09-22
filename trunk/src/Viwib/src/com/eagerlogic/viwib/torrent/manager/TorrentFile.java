/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.torrent.manager;

import com.eagerlogic.viwib.config.Config;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bitlet.wetorrent.Metafile;
import org.bitlet.wetorrent.Torrent;
import org.bitlet.wetorrent.disk.PlainFileSystemTorrentDisk;
import org.bitlet.wetorrent.peer.IncomingPeerListener;
import org.bitlet.wetorrent.pieceChooser.SequentialPieceChooser;

/**
 *
 * @author dipacs
 */
public class TorrentFile implements Serializable {

    private static final long serialVersionUID = 1l;
    private transient Metafile metaFile;
    private transient DestinationFile preferredFile;
    private transient DestinationFile[] files;
    private byte[] torrentData;
    private transient PlainFileSystemTorrentDisk torrentDisk;
    private transient IncomingPeerListener peerListener;
    private transient Torrent torrent;
    private double seedProgress = 0.0;
    private String id;
    private boolean keepOnDisk = false;
    private boolean isStopped = false;
    private long added = 0;

    public TorrentFile(byte[] torrentData) {
        this.torrentData = torrentData;
        this.init();
    }

    public TorrentFile() {
    }

    public void init() {
        this.files = new DestinationFile[getMetafile().getLengths().size()];
        for (int i = 0; i < files.length; i++) {
            this.files[i] = new DestinationFile(getMetafile().getPaths().get(i), getMetafile().getOffsets().get(i), getMetafile().getLengths().get(i));
        }
    }

    public void start() throws IOException {
        if (isStarted()) {
            return;
        }

        System.out.println("Starting torrent: " + this.getMetafile().getName());
        
        torrentDisk = new PlainFileSystemTorrentDisk(this.getMetafile(), new File(Config.getSaveLocation()));
        if (torrentDisk.init()) {
            torrentDisk.resume();
        }

        peerListener = new IncomingPeerListener(12345);
        peerListener.start();


        final SequentialPieceChooser spc = new SequentialPieceChooser();


        torrent = new Torrent(this.getMetafile(), torrentDisk, peerListener, null, spc);
        
        if (preferredFile != null) {
                this.torrent.setPrefStart(preferredFile.getOffset());
                this.torrent.setPrefEnd(preferredFile.getOffset() + preferredFile.getLength());
            } else {
                this.torrent.setPrefStart(-1);
                this.torrent.setPrefEnd(-1);
            }
        
        torrent.startDownload();
        
        System.out.println("Started: " + this.getMetafile().getName());
    }

    public void stop() {
        if (!isStarted()) {
            return;
        }
        

        peerListener.interrupt();
        torrent.stopDownload();

        peerListener = null;
        torrent = null;
        
        System.out.println("Stopped: " + this.getMetafile().getName());
    }

    public boolean isStarted() {
        return torrent != null;
    }

    public boolean isDownloading() {
        return this.getFinishedBytes() >= this.metaFile.getLength();
    }

    public long getFinishedBytes() {
        return ((SequentialPieceChooser) this.torrent.getPieceChooser()).getFinishedBytes();
    }

    public DestinationFile[] getFiles() {
        return files;
    }

    public void setPreferredFile(DestinationFile file) {
        this.preferredFile = file;
        if (torrent != null) {
            if (file != null) {
                this.torrent.setPrefStart(file.getOffset());
                this.torrent.setPrefEnd(file.getOffset() + file.getLength());
            } else {
                this.torrent.setPrefStart(-1);
                this.torrent.setPrefEnd(-1);
            }
        }
    }

    public DestinationFile getPreferredFile() {
        return preferredFile;
    }

    public Metafile getMetafile() {
        // lazy init
        if (metaFile == null) {
            synchronized (this) {
                if (metaFile == null) {
                    try {
                        metaFile = new Metafile(new ByteArrayInputStream(this.torrentData));
                    } catch (IOException ex) {
                        Logger.getLogger(TorrentFile.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(TorrentFile.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return metaFile;
    }

    public PlainFileSystemTorrentDisk getTorrentDisk() {
        return torrentDisk;
    }

    public void setTorrentDisk(PlainFileSystemTorrentDisk torrentDisk) {
        this.torrentDisk = torrentDisk;
    }

    public IncomingPeerListener getPeerListener() {
        return peerListener;
    }

    public void setPeerListener(IncomingPeerListener peerListener) {
        this.peerListener = peerListener;
    }

    public Torrent getTorrent() {
        return torrent;
    }

    public void setTorrent(Torrent torrent) {
        this.torrent = torrent;
    }

    public double getSeedProgress() {
        return seedProgress;
    }

    public void setSeedProgress(double seedProgress) {
        this.seedProgress = seedProgress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isKeepOnDisk() {
        return keepOnDisk;
    }

    public void setKeepOnDisk(boolean keepOnDisk) {
        this.keepOnDisk = keepOnDisk;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }
}
