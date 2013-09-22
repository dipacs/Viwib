/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.BitSet;
import javax.swing.JPanel;

/**
 *
 * @author dipacs
 */
public class PlayerSeekerPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color(0x000000);
    private static final Color COMPLETED_PIECE_COLOR = new Color(0xa00000);
    private static final Color SEQ_COLOR = new Color(0xa0a0a0);
    private static final Color POSITION_COLOR = new Color(0x00c0ff);
    
    private BitSet completedPieces;
    private int totalPieceCount;
    private double seqDownloaded = 0.0;
    private double position = 0.0;
    
    public ISeekListener seekListener;

    public PlayerSeekerPanel() {
        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (seekListener != null) {
                    double newPos = ((double)e.getX()) / ((double)getWidth());
                    seekListener.onSeek(newPos);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
    
    public void setCompletedPieces(BitSet completedPieces, int totalPieceCount) {
        this.completedPieces = completedPieces;
        this.totalPieceCount = totalPieceCount;
        this.repaint();
    }

    public double getSeqDownloaded() {
        return seqDownloaded;
    }

    public void setSeqDownloaded(double seqDownloaded) {
        this.seqDownloaded = seqDownloaded;
        this.repaint();
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int width = this.getWidth();
        int height = this.getHeight();
        
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, width, height);
        
        if (this.completedPieces == null) {
            return;
        }
        
        // drawing completed pieces
        g.setColor(COMPLETED_PIECE_COLOR);
        for (int i = 0; i < totalPieceCount; i++) {
            if (completedPieces.get(i)) {
                int x = (int) ((i / (double)totalPieceCount) * width) + 1;
                int w = width / totalPieceCount + 1;
                g.fillRect(x, 0, w, height);
            }
        }
        
        // drawing seq pieces
        g.setColor(SEQ_COLOR);
        g.fillRect(0, 0, (int)(width * seqDownloaded), height);
        
        // drawing position
        g.setColor(POSITION_COLOR);
        g.fillRect(0, 0, (int)(width * position), height);
    }
    
}
