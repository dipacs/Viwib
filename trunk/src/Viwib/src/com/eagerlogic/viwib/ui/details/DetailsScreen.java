/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.details;

import com.eagerlogic.viwib.Viwib;
import com.eagerlogic.viwib.connectors.TorrentDescriptor;
import com.eagerlogic.viwib.torrent.manager.DestinationFile;
import com.eagerlogic.viwib.torrent.manager.TorrentFile;
import com.eagerlogic.viwib.ui.MainFrame;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author dipacs
 */
public class DetailsScreen extends Parent {
    
    private static final Font NAME_FONT = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 18);
    private static final Font SUB_TITLE_FONT = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14);
    private static final Color NAME_COLOR = new Color(1.0, 0.5, 0.0, 1.0);
    private static final Color SUB_TITLE_COLOR = new Color(1.0, 0.5, 0.0, 1.0);
    
    private static final String[] movieExtensions = new String[] {".mpg", ".mpeg", ".mkv", ".avi", ".wmv", ".mov", ".mp4", ".flv", ".iso"};
    
    private final TorrentDescriptor descriptor;
    private final TorrentFile torrent;
    
    
    public final SimpleDoubleProperty widthProperty = new SimpleDoubleProperty(400);

    public DetailsScreen(TorrentDescriptor descriptor, TorrentFile torrent) {
        this.descriptor = descriptor;
        this.torrent = torrent;
        
        VBox root = new VBox(20);
        this.getChildren().add(root);
        
        // name
        Text txtName = new Text(descriptor.getName());
        txtName.setFont(NAME_FONT);
        txtName.setFill(NAME_COLOR);
        root.getChildren().add(txtName);
        
        //TODO add images
        
        // TODO add seed leach statistics
        
        // file list
        root.getChildren().add(createFileList());
        
        // TODO add description 
    }
    
    private Node createFileList() {
        VBox root = new VBox(5);
        
        Text txtFiles = new Text("Files");
        txtFiles.setFont(SUB_TITLE_FONT);
        txtFiles.setFill(SUB_TITLE_COLOR);
        root.getChildren().add(txtFiles);
        
        int idx = 0;
        for (DestinationFile df : torrent.getFiles()) {
            root.getChildren().add(createFileListItem(df, idx));
            idx++;
        }
        
        return root;
    }
    
    private Node createFileListItem(DestinationFile destFile, final int fileIndex) {
        String name = destFile.getPath();
        if (isSupportedMovieFile(name)) {
            Text txtName = new Text(name);
            txtName.setFill(new Color(0.0, 0xc0 / 255.0, 1.0, 1.0));
            txtName.setOnMouseClicked(new EventHandler<Event>() {

                @Override
                public void handle(Event t) {
                    // TODO implement playing
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            MainFrame.getInstance().play(torrent, fileIndex);
                        }
                    });
                    
//                    PlayerScreen ps = new PlayerScreen(torrent, torrent.getFiles()[fileIndex]);
//                    ps.widthProperty.bind(Viwib.getInstance().getSlidePanel().widthProperty);
//                    ps.heightProperty.bind(Viwib.getInstance().getSlidePanel().heightProperty);
//                    Viwib.getInstance().slideLeft(ps);
                }
            });
            txtName.setCursor(Cursor.HAND);
            return txtName;
        } else {
            Text txtName = new Text(name);
            txtName.setFill(Color.GRAY);
            return txtName;
        }
    }
    
    private boolean isSupportedMovieFile(String name) {
        for (String s : movieExtensions) {
            if (name.endsWith(s)) {
                return true;
            }
        }
        return false;
    }
    
}
