/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui;

import com.eagerlogic.viwib.Viwib;
import com.eagerlogic.viwib.config.Config;
import com.eagerlogic.viwib.torrent.manager.DestinationFile;
import com.eagerlogic.viwib.torrent.manager.TorrentFile;
import com.eagerlogic.viwib.torrent.manager.TorrentManager;
import com.eagerlogic.viwib.ui.browse.BrowseScreen;
import com.eagerlogic.viwib.ui.details.DetailsScreen;
import com.eagerlogic.viwib.ui.player.EPlayerState;
import com.eagerlogic.viwib.ui.player.ISeekListener;
import com.eagerlogic.viwib.ui.player.PlayerControlPanel;
//import com.eagerlogic.viwib.ui.player.PlayerControl;
import com.eagerlogic.viwib.utils.Fake;
import com.eagerlogic.viwib.utils.JarUtils;
import com.sun.jna.NativeLibrary;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.bitlet.wetorrent.Torrent;
import org.bitlet.wetorrent.disk.PlainFileSystemTorrentDisk;
import org.bitlet.wetorrent.disk.TorrentDisk;
import org.bitlet.wetorrent.peer.IncomingPeerListener;
import org.bitlet.wetorrent.pieceChooser.SequentialPieceChooser;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.x.XFullScreenStrategy;

/**
 *
 * @author dipacs
 */
public class MainFrame extends javax.swing.JFrame {

    private static MainFrame instance;

    public static MainFrame getInstance() {
        return instance;
    }
    private JFXPanel jfxPanel;
//    private JLayeredPane layeredPane;
    private EmbeddedMediaPlayerComponent mpC;
    private boolean isPlayer = false;
    private String path;
    private boolean playing = false;
    private boolean paused = false;
    private TorrentFile torrentFile;
//    private IncomingPeerListener peerListener;
    //private Torrent torrent;
    private BrowseScreen browseScreen;
    private PlayerControlPanel playerControl;
    private Object fsSyncObject = new Object();
    FullScreenFrame fsFrame;
    private volatile double playerPosition;

    private EmbeddedMediaPlayerComponent getMpc() {
        synchronized (fsSyncObject) {
            return mpC;
        }
    }

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        instance = this;
        initComponents();
        this.setTitle("Viwib " + Config.VISIBLE_VERSION);
//        this.setResizable(false);
        try {
            this.setIconImage(ImageIO.read(this.getClass().getResourceAsStream("/com/eagerlogic/viwib/res/logo/logo_64.png")));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        jfxPanel = new JFXPanel();
        layeredPane.add(jfxPanel);
        //layeredPane.setSize(1000, 700);

        this.pack();
        resizeComponents();
        jfxPanel.setSize(layeredPane.getWidth(), layeredPane.getHeight());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                jfxPanel.setScene(createScene());
                Platform.setImplicitExit(false);
            }
        });

        this.layeredPane.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
                resizeComponents();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });


    }

    private void resizeComponents() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //System.out.println("resize: " + layeredPane.getWidth());
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (getMpc() != null) {
                            getMpc().setSize(layeredPane.getWidth(), layeredPane.getHeight());
                        }
                        if (playerControl != null) {
                            playerControl.setBounds(0, layeredPane.getHeight() - 100, layeredPane.getWidth(), 100);
                        } else {
                            jfxPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                        }
                    }
                });
            }
        });


    }
    private boolean isFullScreen = false;
    private int lastState = 0;
    private Rectangle lastBounds;

//    private void toggleFullScreen() {
//        synchronized (fsSyncObject) {
//            isFullScreen = !isFullScreen;
//            boolean isPlaying = getMpc().getMediaPlayer().isPlaying();
//            float pos = 0.0f;
//            if (isPlaying) {
//                getMpc().getMediaPlayer().stop();
//                pos = getMpc().getMediaPlayer().getPosition();
//            }
//
//            if (isFullScreen) {
//                this.layeredPane.remove(getMpc());
//                this.layeredPane.remove(playerControl);
//                getMpc().getMediaPlayer().release();
//
//                fsFrame = new FullScreenFrame();
//                mpC = fsFrame.mpComponent;
//                playerControl = fsFrame.playerControl;
//                fsFrame.setVisible(true);
//
//
//
//                if (isPlaying) {
//                    getMpc().getMediaPlayer().startMedia(path);
//                    getMpc().getMediaPlayer().setPosition(pos);
//                }
//            } else {
//                fsFrame.setVisible(false);
//
//                showPlayer();
//
//                if (isPlaying) {
//                    getMpc().getMediaPlayer().startMedia(path);
//                    getMpc().getMediaPlayer().setPosition(pos);
//                }
//            }
//
//            playerControl.setOnFullScreenClickedCallback(new Runnable() {
//                @Override
//                public void run() {
//                    //System.out.println("Toggling full screen");
//                    toggleFullScreen();
//                }
//            });
//            playerControl.setOnStopClickedCallback(new Runnable() {
//                @Override
//                public void run() {
//                    stop();
//                }
//            });
//            playerControl.setOnPlayClickedCallback(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
//            playerControl.setOnVolumeChangedCallback(new Runnable() {
//                @Override
//                public void run() {
//                    EventQueue.invokeLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            getMpc().getMediaPlayer().setVolume(playerControl.jSlider1.getValue());
//                        }
//                    });
//                }
//            });
//        }
////
////                    float pos = 0.0f;
////                    if (isPlaying) {
////                        getMpc().getMediaPlayer().stop();
////                        pos = getMpc().getMediaPlayer().getPosition();
////                    }
////                    getMpc().getMediaPlayer().release();
////
////                    toggleFullScreen();
////
////                    getMpc() = new EmbeddedMediaPlayerComponent();
////                    getMpc().setSize(1000, 700);
////                    layeredPane.add(getMpc());
////
////                    resizeComponents();
////                    if (isPlaying) {
////                        getMpc().getMediaPlayer().playMedia(fPath);
//////                    getMpc().getMediaPlayer().setPosition(pos);
////                        getMpc().getMediaPlayer().setFullScreen(true);
////                    }
//
//
//
////        isFullScreen = !isFullScreen;
////        dispose();
////        if (isFullScreen) {
////            //save last bounds and its extended state
////            lastState = getExtendedState();
////            lastBounds = getBounds();
////            try {
////                setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
////            } catch (Exception ev) {
////                setBounds(getGraphicsConfiguration().getDevice().getDefaultConfiguration().getBounds());
////                ev.printStackTrace();
////            }
////        } else {
////            //restore last bounds and its extended state
////            setBounds(lastBounds);
////            setExtendedState(lastState);
////        }
////        setUndecorated(isFullScreen);
////        setVisible(true);
//    }
    private void showPlayer() {
        synchronized (fsSyncObject) {

            fsFrame = new FullScreenFrame();
            mpC = fsFrame.mpComponent;
            playerControl = fsFrame.playerControl;
            fsFrame.setVisible(true);
//            isPlayer = true;
//            mpC = new EmbeddedMediaPlayerComponent() {
//                @Override
//                protected FullScreenStrategy onGetFullScreenStrategy() {
//                    return new XFullScreenStrategy(MainFrame.this);
//                }
//            };
//            getMpc().setSize(1000, 700);
//
//            this.layeredPane.remove(jfxPanel);
//            playerControl = new PlayerControlPanel();
//            playerControl.setBounds(0, layeredPane.getHeight() - 100, layeredPane.getWidth(), 100);
//            layeredPane.add(playerControl);
//            this.layeredPane.add(getMpc());
//            layeredPane.setPosition(getMpc(), -1);
//
//            resizeComponents();
        }
    }

    private void hidePlayer() {
        synchronized (fsSyncObject) {
            isPlayer = false;
            if (getMpc() != null) {
                getMpc().getMediaPlayer().stop();
                getMpc().getMediaPlayer().release();
                mpC = null;
            }
            if (fsFrame != null) {
                fsFrame.setVisible(false);
            }
        }
    }

    public void stop() {
        if (this.torrentFile != null) {
            torrentFile = null;
            //torrent = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TorrentManager.getInstance().stopPlayingTorrent();
                }
            }).start();
            hidePlayer();
        }
    }

    public void play(final TorrentFile tf, final int playingFileIndex) {
        playing = false;
        paused = false;
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                TorrentManager.getInstance().playTorrent(tf, playingFileIndex);
            }
        });
        t.start();
        showPlayer();
        try {
            t.join();
        } catch (InterruptedException ex) {
            return;
        }
        this.torrentFile = TorrentManager.getInstance().getTorrents().get(TorrentManager.getInstance().getPlayingTorrentIndex());


        playerControl.setOnStopClickedCallback(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        });
        playerControl.setOnPlayClickedCallback(new Runnable() {
            @Override
            public void run() {
                if (playerControl.getState() == EPlayerState.PLAYING) {
                    paused = true;
                    getMpc().getMediaPlayer().pause();
                    playerControl.setState(EPlayerState.PAUSED);
                    playerControl.setStateMessage("Paused...");

                } else if (playerControl.getState() == EPlayerState.PAUSED) {
                    paused = false;
                    getMpc().getMediaPlayer().play();
                    playerControl.setState(EPlayerState.PLAYING);
                    playerControl.setStateMessage("Playing...");
                }
            }
        });
        playerControl.setOnVolumeChangedCallback(new Runnable() {
            @Override
            public void run() {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getMpc().getMediaPlayer().setVolume(playerControl.jSlider1.getValue());
                    }
                });
            }
        });
        playerControl.playerSeeker.seekListener = new ISeekListener() {
            @Override
            public void onSeek(double position) {
                final DestinationFile playingFile = TorrentManager.getInstance().getTorrents().get(
                        TorrentManager.getInstance().getPlayingTorrentIndex()).getFiles()[playingFileIndex];

                final long finished = ((SequentialPieceChooser) torrentFile.getTorrent().getPieceChooser()).getSequentiallyFinishedBytes();
                final long fileLength = playingFile.getLength();
                final double downloaded = finished / (double) playingFile.getLength();
                
                double bufferSizePercent = ((fileLength / 90.0) / 12.0) / fileLength;
                double finishedPercent = finished / (double) fileLength;
                if (position < 0.0) {
                    position = 0.0;
                } else if (position > 1.0) {
                    position = 1.0;
                }

                if ((position < finishedPercent - bufferSizePercent)) {
                    mpC.getMediaPlayer().setPosition((float) position);
                }
            }
        };


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    t.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                path = Config.getSaveLocation();
                if (!path.endsWith(File.separator)) {
                    path += File.separator;
                }
                if (torrentFile.getFiles().length > 1) {
                    path += torrentFile.getMetafile().getName();
                    if (!path.endsWith(File.separator)) {
                        path += File.separator;
                    }
                }
                //System.out.println(TorrentManager.getInstance());
                //System.out.println(TorrentManager.getInstance().getTorrents());
                //System.out.println(TorrentManager.getInstance().getPlayingTorrentIndex());
                //System.out.println(TorrentManager.getInstance().getTorrents().get(TorrentManager.getInstance().getPlayingTorrentIndex()));
                //System.out.println(TorrentManager.getInstance().getTorrents().get(TorrentManager.getInstance().getPlayingTorrentIndex()).getFiles());
                final DestinationFile playingFile = TorrentManager.getInstance().getTorrents().get(
                        TorrentManager.getInstance().getPlayingTorrentIndex()).getFiles()[playingFileIndex];
                path = path + playingFile.getPath();
                String fPath = path;

                playing = false;
                while (torrentFile != null) {
                    final long finished = ((SequentialPieceChooser) torrentFile.getTorrent().getPieceChooser()).getSequentiallyFinishedBytes();
                    final long fileLength = playingFile.getLength();
                    final double downloaded = finished / (double) playingFile.getLength();
                    //System.out.println("" + finished + " - " + downloaded + " - " + torrentFile.getTorrent().getTorrentDisk().getCompleted());

                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
//                                    pc.seeker.downloadedProperty.set(downloaded);
                            double playPos = getMpc().getMediaPlayer().getPosition();
                            if (playPos < 0.0) {
                                playPos = 0.0;
                            }
//                                    pc.seeker.positionProperty.set(playPos);
                            int startPiece = (int) (playingFile.getOffset() / torrentFile.getMetafile().getPieceLength());
                            int endPiece = (int) (torrentFile.getTorrent().getPrefEnd() / torrentFile.getMetafile().getPieceLength());
                            BitSet completedPieces = new BitSet(endPiece - startPiece + 1);
                            for (int i = startPiece; i <= endPiece; i++) {
                                completedPieces.set(i - startPiece, torrentFile.getTorrent().getTorrentDisk().isCompleted(i));
                            }
                            playerControl.playerSeeker.setCompletedPieces(completedPieces, endPiece - startPiece + 1);
                            playerControl.playerSeeker.setSeqDownloaded(downloaded);
                            playerControl.playerSeeker.setPosition(playPos);

//                                    pc.seeker.setCompletedPieces(completedPieces, endPiece - startPiece + 1);


                        }
                    });

                    if (!playing) {
                        double bufferSizePercent = ((fileLength / 90.0) / 3.0) / fileLength;
                        double finishedPercent = finished / (double) fileLength;
                        double playPercent = getMpc().getMediaPlayer().getPosition();
                        if (playPercent < 0.0) {
                            playPercent = 0.0;
                        }

                        if ((finishedPercent > playPercent + bufferSizePercent)) {  // 10 sec
                            if (getMpc().getMediaPlayer().getMediaPlayerState() == libvlc_state_t.libvlc_Paused) {
                                //System.out.println("---- buffering finished");
                                playing = true;
                                EventQueue.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        getMpc().getMediaPlayer().pause();
                                        playerControl.setState(EPlayerState.PLAYING);
                                        playerControl.setStateMessage("Playing...");
                                    }
                                });
                            } else {
                                //System.out.println("=========== Start playing");
                                playing = true;
                                EventQueue.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String path = Config.getSaveLocation();
                                            if (!path.endsWith(File.separator)) {
                                                path += File.separator;
                                            }
                                            if (torrentFile.getFiles().length > 1) {
                                                path += torrentFile.getMetafile().getName();
                                                if (!path.endsWith(File.separator)) {
                                                    path += File.separator;
                                                }
                                            }
                                            path = path + playingFile.getPath();
                                            //System.out.println("Playing: " + path);
                                            boolean res = getMpc().getMediaPlayer().playMedia(path);
                                            //System.out.println(res);
                                            playerControl.setState(EPlayerState.PLAYING);
                                            playerControl.setStateMessage("Playing...");
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } else {
                            EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    // (0.0014 - 0.0) / 0.00092
                                    double bufferSizePercent = ((fileLength / 90.0) / 3.0) / fileLength;
                                    double finishedPercent = finished / (double) fileLength;
                                    double playPercent = getMpc().getMediaPlayer().getPosition();
                                    if (playPercent < 0.0) {
                                        playPercent = 0.0;
                                    }
                                    int buffPercent = (int) (((finishedPercent - playPercent) / bufferSizePercent) * 100);
                                    if (buffPercent > 100) {
                                        buffPercent = 100;
                                    } else if (buffPercent < 0) {
                                        buffPercent = 0;
                                    }
                                    //getMpc().getMediaPlayer().pause();
                                    playerControl.setState(EPlayerState.BUFFERING);
                                    playerControl.setStateMessage("Buffering..." + buffPercent + "%");
                                }
                            });
                        }
                    } else {
                        double bufferSizePercent = ((fileLength / 90.0) / 12.0) / fileLength;
                        double finishedPercent = finished / (double) fileLength;
                        double playPercent = getMpc().getMediaPlayer().getPosition();
                        if (playPercent < 0.0) {
                            playPercent = 0.0;
                        }

                        if ((playPercent > finishedPercent - bufferSizePercent)) {
                            //System.out.println("----- buffering");
                            playing = false;
                            EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    getMpc().getMediaPlayer().pause();
                                    playerControl.setState(EPlayerState.BUFFERING);
                                    playerControl.setStateMessage("Buffering...");
                                }
                            });
                        } else {
                            EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    long length = getMpc().getMediaPlayer().getLength();
                                    long currPos = (long) (length * getMpc().getMediaPlayer().getPosition());
                                    playerControl.setTime(convertMillisToString(currPos) + " / " + convertMillisToString(length));
                                }
                            });
                        }
                    }


                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                }
            }
        }).start();


    }

    private Scene createScene() {
        return new Viwib();
    }

    public BrowseScreen getBrowseScreen() {
        return browseScreen;
    }

    public void setBrowseScreen(BrowseScreen browseScreen) {
        this.browseScreen = browseScreen;
    }

    private String convertMillisToString(long millis) {
        String res = "";
        int hours = (int) (millis / (1000 * 60 * 60));
        millis = millis - (hours * 1000 * 60 * 60);
        int minutes = (int) (millis / (1000 * 60));
        millis = millis - (minutes * 1000 * 60);
        int seconds = (int) (millis / 1000);
        res = res + hours;
        res += ":";
        if (minutes < 10) {
            res += "0";
        }
        res += minutes;
        res += ":";
        if (seconds < 10) {
            res += "0";
        }
        res += seconds;
        return res;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        layeredPane = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Viwib 0.1");
        setPreferredSize(new java.awt.Dimension(1000, 700));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(layeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(layeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.stop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                TorrentManager.getInstance().stop();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                }
                TorrentManager.getInstance().stopAllTorrent();
            }
        }).start();
        Platform.exit();
    }//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane layeredPane;
    // End of variables declaration//GEN-END:variables
}
