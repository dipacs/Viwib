/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.addtracker;

import com.eagerlogic.viwib.config.Config;
import com.eagerlogic.viwib.connectors.ASiteConnector;
import com.eagerlogic.viwib.connectors.SiteConfig;
import com.eagerlogic.viwib.connectors.common.CommonConnector;
import java.awt.EventQueue;
import java.io.File;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javax.swing.JFileChooser;

/**
 *
 * @author dipacs
 */
public class AddTrackerScreen extends Parent {
    
    public final SimpleDoubleProperty width = new SimpleDoubleProperty();
    public final SimpleDoubleProperty height = new SimpleDoubleProperty();
    
    public AddTrackerScreen() {
        final Group root = new Group();
        root.translateXProperty().bind(new DoubleBinding() {
            
            {
                super.bind(root.boundsInLocalProperty(), width);
            }

            @Override
            protected double computeValue() {
                return (width.get() - root.getBoundsInLocal().getWidth()) / 2;
            }
        });
        root.translateYProperty().bind(new DoubleBinding() {
            
            {
                super.bind(root.boundsInLocalProperty(), width);
            }

            @Override
            protected double computeValue() {
                return (height.get() - root.getBoundsInLocal().getHeight()) / 2;
            }
        });
        this.getChildren().add(root);
        
        Rectangle rect = new Rectangle();
        
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.rgb(192, 192, 192));
        rect.setStrokeWidth(1.0);
        rect.setStrokeType(StrokeType.INSIDE);
        DropShadow ds = new DropShadow();
        ds.setRadius(20);
        ds.setOffsetX(1);
        ds.setOffsetY(1);
        rect.setEffect(ds);
        root.getChildren().add(rect);
        
        final VBox vbox = new VBox(40);
        vbox.translateXProperty().set(20);
        vbox.translateYProperty().set(20);
        root.getChildren().add(vbox);
        
        Label lblTitle = new Label("ADD TRACKER");
        lblTitle.getStyleClass().add("lblScreenTitle");
        vbox.getChildren().add(lblTitle);
        
        Label lblInfo = new Label("You need some tracker to use Viwib. A tracker is a torrent site which contains the torrent files. Enter the url of your"
                + " favorite torrent site, for example: 'www.thepiratebay.org'");
        lblInfo.getStyleClass().add("lblScreenInfo");
        lblInfo.setWrapText(true);
        lblInfo.setTextAlignment(TextAlignment.CENTER);
        lblInfo.setPrefWidth(500);
        vbox.getChildren().add(lblInfo);
        
        HBox hbox = new HBox(5);
        vbox.getChildren().add(hbox);
        
        Label lblSaveFolder = new Label("Torrent Site URL:");
        lblSaveFolder.getStyleClass().add("lblFieldTitle");
        hbox.getChildren().add(lblSaveFolder);
        
        
        final TextField tfUrl = new TextField();
        tfUrl.setPrefWidth(300);
        hbox.getChildren().add(tfUrl);
        
        final Text txtCheckState = new Text("A");
        txtCheckState.setVisible(false);
        txtCheckState.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16));
        txtCheckState.setWrappingWidth(500);
        txtCheckState.setTextAlignment(TextAlignment.CENTER);
        vbox.getChildren().add(txtCheckState);
        
        AnchorPane ap = new AnchorPane();
        //ap.setMaxWidth(100);
        vbox.getChildren().add(ap);
        
        final Button btnCheck = new Button("Check");
        btnCheck.getStyleClass().add("btnOrange");
        btnCheck.setDisable(true);
        ap.getChildren().add(btnCheck);
        AnchorPane.setLeftAnchor(btnCheck, 0.0);
        
        
        final Button btnNext = new Button("Add");
        btnNext.setDisable(true);
        btnNext.getStyleClass().add("btnApprove");
        ap.getChildren().add(btnNext);
        AnchorPane.setRightAnchor(btnNext, 0.0);
        
        tfUrl.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                btnCheck.setDisable("".equals(t1));
                btnNext.setDisable(true);
                txtCheckState.setVisible(false);
            }
        });
        btnCheck.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                String url = tfUrl.getText();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                ASiteConnector connector = new CommonConnector(url);
                SiteConfig config = connector.getConfig();
                if (config != null) {
                    txtCheckState.setText("The given tracker can be used with Viwib.");
                    txtCheckState.setFill(Color.GREEN);
                    txtCheckState.setVisible(true);
                    btnNext.setDisable(false);
                } else {
                    txtCheckState.setText("Sorry, the given tracker can not be used with Viwib. Ask the tracker administrator to integrate this tracker with Viwib!");
                    txtCheckState.setFill(Color.RED);
                    txtCheckState.setVisible(true);
                    btnNext.setDisable(true);
                }
            }
        });
        
        
        
        rect.widthProperty().bind(new DoubleBinding() {
            
            {
                super.bind(vbox.boundsInLocalProperty());
            }

            @Override
            protected double computeValue() {
                return vbox.boundsInLocalProperty().get().getWidth() + 40;
            }
        });
        rect.heightProperty().bind(new DoubleBinding() {
            
            {
                super.bind(vbox.boundsInLocalProperty());
            }

            @Override
            protected double computeValue() {
                return vbox.boundsInLocalProperty().get().getHeight() + 40;
            }
        });
        
    }
    
}
