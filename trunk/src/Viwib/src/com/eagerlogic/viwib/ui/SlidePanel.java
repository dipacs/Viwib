/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author dipacs
 */
public class SlidePanel extends Parent {
    
    public final SimpleDoubleProperty widthProperty = new SimpleDoubleProperty(200);
    public final SimpleDoubleProperty heightProperty = new SimpleDoubleProperty(200);
    
    private final Group root = new Group();
    
    private Node actNode = null;
    private Node prevNode = null;
    
    private Timeline runningTimeLine;

    public SlidePanel() {
        Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(widthProperty);
        clipRect.heightProperty().bind(heightProperty);
        root.setClip(clipRect);
        
        Rectangle bg = new Rectangle();
        bg.widthProperty().bind(widthProperty);
        bg.heightProperty().bind(heightProperty);
        bg.setFill(Color.TRANSPARENT);
        this.getChildren().add(bg);
        
    }
    
    public void slideLeft(Node node) {
        prevNode = actNode;
        actNode = node;
        if (actNode != null) {
            this.getChildren().add(actNode);
        }
        double width = widthProperty.get();
        
        if (runningTimeLine != null) {
            runningTimeLine.stop();
        }
        
        List<KeyFrame> keyFrames = new ArrayList<KeyFrame>();
        if (prevNode != null) {
            keyFrames.add(new KeyFrame(new Duration(0), new KeyValue(prevNode.opacityProperty(), 1.0)));
            keyFrames.add(new KeyFrame(new Duration(500), new KeyValue(prevNode.opacityProperty(), 0.0)));
            keyFrames.add(new KeyFrame(new Duration(0), new KeyValue(prevNode.translateXProperty(), 0.0)));
            keyFrames.add(new KeyFrame(new Duration(500), new KeyValue(prevNode.translateXProperty(), -100)));
        }
        if (actNode != null) {
            keyFrames.add(new KeyFrame(new Duration(0), new KeyValue(actNode.opacityProperty(), 0.0)));
            keyFrames.add(new KeyFrame(new Duration(500), new KeyValue(actNode.opacityProperty(), 1.0)));
            keyFrames.add(new KeyFrame(new Duration(0), new KeyValue(actNode.translateXProperty(), 100)));
            keyFrames.add(new KeyFrame(new Duration(500), new KeyValue(actNode.translateXProperty(), 0.0)));
        }
        
        
        runningTimeLine = new Timeline(keyFrames.toArray(new KeyFrame[keyFrames.size()]));
        runningTimeLine.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (prevNode != null) {
                    getChildren().remove(prevNode);
                    prevNode = null;
                }
            }
        });
        runningTimeLine.play();
    }
    
    public void slideRight(Node node) {
        prevNode = actNode;
        actNode = node;
        if (actNode != null) {
            this.getChildren().add(actNode);
        }
        double width = widthProperty.get();
        
        if (runningTimeLine != null) {
            runningTimeLine.stop();
        }
        
        List<KeyFrame> keyFrames = new ArrayList<KeyFrame>();
        if (prevNode != null) {
            keyFrames.add(new KeyFrame(new Duration(0), new KeyValue(prevNode.opacityProperty(), 1.0)));
            keyFrames.add(new KeyFrame(new Duration(500), new KeyValue(prevNode.opacityProperty(), 0.0)));
            keyFrames.add(new KeyFrame(new Duration(0), new KeyValue(prevNode.translateXProperty(), 0.0)));
            keyFrames.add(new KeyFrame(new Duration(500), new KeyValue(prevNode.translateXProperty(), 100)));
        }
        if (actNode != null) {
            keyFrames.add(new KeyFrame(new Duration(0), new KeyValue(actNode.opacityProperty(), 0.0)));
            keyFrames.add(new KeyFrame(new Duration(500), new KeyValue(actNode.opacityProperty(), 1.0)));
            keyFrames.add(new KeyFrame(new Duration(0), new KeyValue(actNode.translateXProperty(), -100)));
            keyFrames.add(new KeyFrame(new Duration(500), new KeyValue(actNode.translateXProperty(), 0.0)));
        }
        
        
        runningTimeLine = new Timeline(keyFrames.toArray(new KeyFrame[keyFrames.size()]));
        runningTimeLine.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (prevNode != null) {
                    getChildren().remove(prevNode);
                    prevNode = null;
                }
            }
        });
        runningTimeLine.play();
    }
    
}
