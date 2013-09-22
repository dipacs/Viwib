/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.browse;

import com.eagerlogic.viwib.Viwib;
import com.eagerlogic.viwib.connectors.TorrentDescriptor;
import com.eagerlogic.viwib.torrent.manager.DestinationFile;
import com.eagerlogic.viwib.torrent.manager.TorrentFile;
import com.eagerlogic.viwib.ui.ImageButton;
import com.eagerlogic.viwib.ui.MainFrame;
import com.eagerlogic.viwib.ui.SlidePanel;
import com.eagerlogic.viwib.ui.ZoomImageFrame;
import com.eagerlogic.viwib.utils.AsyncImageDownloadService;
import com.eagerlogic.viwib.utils.IAsyncImageDownloadCallback;
import java.awt.EventQueue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.bitlet.wetorrent.Metafile;

/**
 *
 * @author dipacs
 */
public class SearchResultItem extends Parent {

//    public static final class ItemClickedEvent extends Event {
//        private final SearchResultItem item;
//
//        public ItemClickedEvent(SearchResultItem item) {
//            super(null);
//            this.item = item;
//        }
//
//        public SearchResultItem getItem() {
//            return item;
//        }
//    }
    private static final String[] movieExtensions = new String[]{".mpg", ".mpeg", ".mkv", ".avi", ".wmv", ".mov", ".mp4", ".flv"};
    private final TorrentDescriptor descriptor;
    public final SimpleDoubleProperty widthProperty = new SimpleDoubleProperty(500);
    public final SimpleDoubleProperty heightProperty = new SimpleDoubleProperty(150);
    private final ZoomImageFrame imageFrame;
    private Group slidePanel;
    private Group detailsPanel;
    private Group fileListPanel;

//    public EventHandler<ItemClickedEvent> onClicked;
    public SearchResultItem(TorrentDescriptor descriptor) {
        this.descriptor = descriptor;

        Rectangle bg = new Rectangle();
        bg.widthProperty().bind(this.widthProperty);
        bg.heightProperty().bind(this.heightProperty);
        bg.setFill(Color.WHITE);
        bg.setStroke(Color.rgb(128, 128, 128));
        bg.setStrokeWidth(1);
        bg.setStrokeType(StrokeType.INSIDE);
        this.getChildren().add(bg);

        final HBox root = new HBox(20);
        root.setTranslateX(10);
        root.setTranslateY(10);
        this.getChildren().add(root);

        this.imageFrame = new ZoomImageFrame();
        imageFrame.widthProperty.set(130);
        imageFrame.heightProperty.set(130);
        root.getChildren().add(imageFrame);

        final VBox vb = new VBox(15);
        root.getChildren().add(vb);

        // TODO add ellipsis at the end
        Text txtTitle = new Text(descriptor.getName());
        txtTitle.wrappingWidthProperty().bind(this.widthProperty.subtract(imageFrame.widthProperty).subtract(40));
        txtTitle.setTextAlignment(TextAlignment.LEFT);
        txtTitle.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 20));
        txtTitle.setFill(Color.BLACK);
        vb.getChildren().add(txtTitle);

        if (descriptor.getCoverUrl() != null) {
            AsyncImageDownloadService.download(descriptor, new IAsyncImageDownloadCallback() {
                @Override
                public void onImageDownloadSuccess(Image image) {
                    setImage(image);
                }
            });
        }

        this.slidePanel = new Group();
//        this.slidePanel.widthProperty.bind(this.widthProperty.subtract(imageFrame.widthProperty).subtract(40));
//        this.slidePanel.heightProperty.set(90);
        vb.getChildren().add(this.slidePanel);

        this.detailsPanel = createDetailsPanel();
        this.slidePanel.getChildren().add(detailsPanel);

    }

    private Group createDetailsPanel() {
        Group res = new Group();

        HBox hb = new HBox(10);
        res.getChildren().add(hb);

        VBox vb2 = new VBox();
        hb.getChildren().add(vb2);

        Text txtAdded = new Text("Added: ");
        if (descriptor.getDateAdded() != null) {
            txtAdded.setText(txtAdded.getText() + descriptor.getDateAdded());
        }
        txtAdded.wrappingWidthProperty().bind(this.widthProperty.subtract(imageFrame.widthProperty).subtract(126));
        txtAdded.setTextAlignment(TextAlignment.LEFT);
        txtAdded.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 14));
        txtAdded.setFill(Color.BLACK);
        vb2.getChildren().add(txtAdded);

        Text txtSize = new Text("Size: ");
        if (descriptor.getSize() != null) {
            txtSize.setText(txtSize.getText() + descriptor.getSize());
        }
        txtSize.wrappingWidthProperty().bind(this.widthProperty.subtract(imageFrame.widthProperty).subtract(126));
        txtSize.setTextAlignment(TextAlignment.LEFT);
        txtSize.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 14));
        txtSize.setFill(Color.BLACK);
        vb2.getChildren().add(txtSize);

        ImageButton ib = new ImageButton(new Image(this.getClass().getResource("/com/eagerlogic/viwib/res/browser/btnNext.png").toExternalForm()));
        ib.onClicked = new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (fileListPanel == null) {
                    fileListPanel = createFileListPanel();
                }
                slidePanel.getChildren().clear();
                slidePanel.getChildren().add(fileListPanel);
            }
        };
        hb.getChildren().add(ib);

        return res;
    }

    private Group createFileListPanel() {
        Group res = new Group();

        HBox hb = new HBox(20);
        res.getChildren().add(hb);

        ImageButton ib = new ImageButton(new Image(this.getClass().getResource("/com/eagerlogic/viwib/res/browser/btnPrev.png").toExternalForm()));
        ib.onClicked = new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                slidePanel.getChildren().clear();
                slidePanel.getChildren().add(detailsPanel);
            }
        };
        hb.getChildren().add(ib);

        ScrollPane sp = new ScrollPane();
        sp.prefWidthProperty().bind(this.widthProperty.subtract(this.imageFrame.widthProperty).subtract(126));
        sp.prefHeightProperty().set(90);
        hb.getChildren().add(sp);

        byte[] torrentFileBytes = Viwib.getConnector().getTorrentFileFromUrl(descriptor.getTorrentUrl());
        TorrentFile tf = new TorrentFile(torrentFileBytes);
        tf.setId(this.descriptor.getId());

        sp.setContent(createFileList(tf));



        return res;
    }

    private Node createFileList(TorrentFile torrent) {
        VBox root = new VBox(3);

        int idx = 0;
        for (DestinationFile df : torrent.getFiles()) {
            root.getChildren().add(createFileListItem(df, idx, torrent));
            idx++;
        }

        return root;
    }

    private Node createFileListItem(DestinationFile destFile, final int fileIndex, final TorrentFile torrent) {
        String name = destFile.getPath();
        if (isSupportedMovieFile(name)) {
            Text txtName = new Text(name);
            txtName.setFill(new Color(1.0, 0.0, 0.5, 1.0));
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
            txtName.setFill(Color.rgb(128, 128, 128));
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

    public void setImage(Image image) {
        this.imageFrame.setImage(image);
    }
}
