/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.player;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 * @author dipacs
 */
//public class PlayerControl extends Parent {
//    
//    public final SimpleDoubleProperty widthProperty = new SimpleDoubleProperty(200);
//    public final SimpleDoubleProperty heightProperty = new SimpleDoubleProperty(200);
//    public final SimpleStringProperty timerTextProperty = new SimpleStringProperty("--:--:-- / --:--:--");
//    public EventHandler<Event> onStopClicked;
//    public EventHandler<Event> onPlayPauseClicked;
//    public EventHandler<Event> onFullScreenClicked;
//    
//    public final PlayerSeeker seeker;
//    private final ImageView btnStop;
//    private final ImageView btnPlayPause;
//    private final ImageView btnFullScreen;
//    private final Text txtTimer;
//
//    public PlayerControl() {
//        this.seeker = new PlayerSeeker();
//        this.seeker.widthProperty.bind(this.widthProperty.subtract(20));
//        this.seeker.heightProperty.set(10);
//        this.seeker.setTranslateX(10);
//        this.seeker.setTranslateY(5);
//        this.getChildren().add(this.seeker);
//        
//        final HBox hbButtons = new HBox(40);
//        hbButtons.setTranslateY(20);
//        hbButtons.setAlignment(Pos.CENTER);
//        hbButtons.translateXProperty().bind(new DoubleBinding() {
//            
//            {
//                super.bind(widthProperty, hbButtons.boundsInLocalProperty());
//            }
//
//            @Override
//            protected double computeValue() {
//                return (widthProperty.get() - hbButtons.getBoundsInLocal().getWidth()) / 2.0;
//            }
//        });
//        this.getChildren().add(hbButtons);
//        
//        btnStop = new ImageView(new Image("/com/eagerlogic/viwib/res/player/btnStop.png"));
//        btnStop.setCursor(Cursor.HAND);
//        btnStop.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent t) {
//                if (onStopClicked != null) {
//                    onStopClicked.handle(t);
//                }
//            }
//        });
//        hbButtons.getChildren().add(btnStop);
//        
//        btnPlayPause = new ImageView(new Image("/com/eagerlogic/viwib/res/player/btnPlay.png"));
//        btnPlayPause.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent t) {
//                if (onPlayPauseClicked != null) {
//                    onPlayPauseClicked.handle(t);
//                }
//            }
//        });
//        hbButtons.getChildren().add(btnPlayPause);
//        
//        btnFullScreen = new ImageView(new Image("/com/eagerlogic/viwib/res/player/btnFullScreen.png"));
//        btnFullScreen.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent t) {
//                if (onFullScreenClicked != null) {
//                    onFullScreenClicked.handle(t);
//                }
//            }
//        });
//        hbButtons.getChildren().add(btnFullScreen);
//        
//        txtTimer = new Text("--:--:-- / --:--:--");
//        txtTimer.setFill(Color.WHITE);
//        txtTimer.textProperty().bind(this.timerTextProperty);
//        hbButtons.getChildren().add(txtTimer);
//        
//    }
//    
//}
