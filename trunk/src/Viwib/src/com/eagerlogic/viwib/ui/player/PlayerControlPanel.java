/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.player;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author dipacs
 */
public class PlayerControlPanel extends javax.swing.JPanel {

    private Runnable onFullScreenClickedCallback;
    private Runnable onStopClickedCallback;
    private Runnable onPlayClickedCallback;
    private Runnable onVolumeChangedCallback;
    private Runnable onHideListener;
    private EPlayerState state;
    private boolean underRoot = true;
    private boolean underTracker = false;
    private boolean underStop = false;
    private boolean underPause = false;
    private boolean underSeeker = false;

    /**
     * Creates new form PlayerControlPanel
     */
    public PlayerControlPanel() {
        initComponents();
        this.jSlider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onSliderValueChanged();
            }
        });
        onSliderValueChanged();
    }

    private void hideIfNeeded() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!underRoot && !underTracker && !underStop && !underPause && !underSeeker) {
                    if (onHideListener != null) {
                        onHideListener.run();
                    }
                }
            }
        });
    }

    private void onSliderValueChanged() {
        this.lblVolume.setText(this.jSlider1.getValue() + "%");
        if (onVolumeChangedCallback != null) {
            onVolumeChangedCallback.run();
        }
    }

    public Runnable getOnFullScreenClickedCallback() {
        return onFullScreenClickedCallback;
    }

    public void setOnFullScreenClickedCallback(Runnable onFullScreenClickedCallback) {
        this.onFullScreenClickedCallback = onFullScreenClickedCallback;
    }

    public Runnable getOnVolumeChangedCallback() {
        return onVolumeChangedCallback;
    }

    public Runnable getOnHideListener() {
        return onHideListener;
    }

    public void setOnHideListener(Runnable onHideListener) {
        this.onHideListener = onHideListener;
    }

    public void setOnVolumeChangedCallback(Runnable onVolumeChangedCallback) {
        this.onVolumeChangedCallback = onVolumeChangedCallback;
    }

    public Runnable getOnStopClickedCallback() {
        return onStopClickedCallback;
    }

    public void setOnStopClickedCallback(Runnable onStopClickedCallback) {
        this.onStopClickedCallback = onStopClickedCallback;
    }

    public Runnable getOnPlayClickedCallback() {
        return onPlayClickedCallback;
    }

    public void setOnPlayClickedCallback(Runnable onPlayClickedCallback) {
        this.onPlayClickedCallback = onPlayClickedCallback;
    }

    public EPlayerState getState() {
        return state;
    }

    public void setState(EPlayerState state) {
        this.state = state;
        switch (state) {
            case BUFFERING:
                this.btnPlay.setEnabled(false);
                this.btnPlay.setForeground(Color.GRAY);
                try {
                    this.btnPlay.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/com/eagerlogic/viwib/res/player/btnPlay.png"))));
                } catch (IOException ex) {
                    Logger.getLogger(PlayerControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case PLAYING:
                this.btnPlay.setEnabled(true);
                this.btnPlay.setForeground(Color.BLACK);
                try {
                    this.btnPlay.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/com/eagerlogic/viwib/res/player/btnPause.png"))));
                } catch (IOException ex) {
                    Logger.getLogger(PlayerControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case PAUSED:
                this.btnPlay.setEnabled(true);
                this.btnPlay.setForeground(Color.BLACK);
                try {
                    this.btnPlay.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/com/eagerlogic/viwib/res/player/btnPlay.png"))));
                } catch (IOException ex) {
                    Logger.getLogger(PlayerControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }
    }

    public void setStateMessage(String message) {
        this.lblState.setText(message);
    }

    public void setTime(String time) {
        this.lblTime.setText(time);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSlider1 = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        lblVolume = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnPlay = new javax.swing.JLabel();
        btnStop = new javax.swing.JLabel();
        lblState = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        playerSeeker = new com.eagerlogic.viwib.ui.player.PlayerSeekerPanel();

        setFocusable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
        });

        jSlider1.setValue(100);
        jSlider1.setFocusable(false);
        jSlider1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jSlider1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jSlider1MouseExited(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Volume:");
        jLabel1.setFocusable(false);

        lblVolume.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblVolume.setText("100%");
        lblVolume.setFocusable(false);

        jPanel1.setFocusable(false);

        btnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eagerlogic/viwib/res/player/btnPlay.png"))); // NOI18N
        btnPlay.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPlay.setFocusable(false);
        btnPlay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPlayMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPlayMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPlayMouseExited(evt);
            }
        });

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eagerlogic/viwib/res/player/btnStop.png"))); // NOI18N
        btnStop.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnStop.setFocusable(false);
        btnStop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnStopMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnStopMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnStopMouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnStop)
                .addGap(18, 18, 18)
                .addComponent(btnPlay))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnPlay))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnStop)
                .addContainerGap())
        );

        lblState.setForeground(new java.awt.Color(102, 102, 102));
        lblState.setText("Initializing file...");
        lblState.setFocusable(false);

        lblTime.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblTime.setText("-:--:-- / -:--:--");
        lblTime.setFocusable(false);

        playerSeeker.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        playerSeeker.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playerSeekerMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                playerSeekerMouseExited(evt);
            }
        });

        javax.swing.GroupLayout playerSeekerLayout = new javax.swing.GroupLayout(playerSeeker);
        playerSeeker.setLayout(playerSeekerLayout);
        playerSeekerLayout.setHorizontalGroup(
            playerSeekerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        playerSeekerLayout.setVerticalGroup(
            playerSeekerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playerSeeker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblState, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                                .addGap(138, 138, 138))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTime)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblVolume)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playerSeeker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblTime)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblState)
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                    .addComponent(lblVolume, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnStopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnStopMouseClicked
        if (this.onStopClickedCallback != null) {
            this.onStopClickedCallback.run();
        }
    }//GEN-LAST:event_btnStopMouseClicked

    private void btnPlayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPlayMouseClicked
        if (this.onPlayClickedCallback != null) {
            this.onPlayClickedCallback.run();
        }
    }//GEN-LAST:event_btnPlayMouseClicked

    private void btnStopMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnStopMouseEntered
        this.underStop = true;
        
    }//GEN-LAST:event_btnStopMouseEntered

    private void btnStopMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnStopMouseExited
        this.underStop = false;
        hideIfNeeded();
    }//GEN-LAST:event_btnStopMouseExited

    private void btnPlayMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPlayMouseEntered
        this.underPause = true;
        
    }//GEN-LAST:event_btnPlayMouseEntered

    private void btnPlayMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPlayMouseExited
        this.underPause = false;
        hideIfNeeded();
    }//GEN-LAST:event_btnPlayMouseExited

    private void jSlider1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1MouseEntered
        this.underTracker = true;
    }//GEN-LAST:event_jSlider1MouseEntered

    private void jSlider1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1MouseExited
        this.underTracker = false;
        hideIfNeeded();
    }//GEN-LAST:event_jSlider1MouseExited

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        this.underRoot = true;
    }//GEN-LAST:event_formMouseEntered

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        this.underRoot = false;
        this.hideIfNeeded();
    }//GEN-LAST:event_formMouseExited

    private void playerSeekerMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerSeekerMouseEntered
        this.underSeeker = true;
    }//GEN-LAST:event_playerSeekerMouseEntered

    private void playerSeekerMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerSeekerMouseExited
        this.underSeeker = false;
        hideIfNeeded();
    }//GEN-LAST:event_playerSeekerMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnPlay;
    private javax.swing.JLabel btnStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JSlider jSlider1;
    private javax.swing.JLabel lblState;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblVolume;
    public com.eagerlogic.viwib.ui.player.PlayerSeekerPanel playerSeeker;
    // End of variables declaration//GEN-END:variables
}
