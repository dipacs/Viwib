/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author dipacs
 */
public class ImageButton extends Parent {
    
    public EventHandler<Event> onClicked;
    public final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
    
    public ImageButton() {
        this(null);
    }

    public ImageButton(Image image) {
        imageProperty.set(image);
        
        final ImageView iv = new ImageView();
        iv.imageProperty().bind(this.imageProperty);
        this.getChildren().add(iv);
        
        Rectangle rect = new Rectangle();
        rect.widthProperty().bind(new DoubleBinding() {
            
            {
                super.bind(iv.boundsInLocalProperty());
            }

            @Override
            protected double computeValue() {
                return iv.boundsInLocalProperty().get().getWidth();
            }
        });
        rect.heightProperty().bind(new DoubleBinding() {
            
            {
                super.bind(iv.boundsInLocalProperty());
            }

            @Override
            protected double computeValue() {
                return iv.boundsInLocalProperty().get().getHeight();
            }
        });
        rect.setFill(Color.TRANSPARENT);
        rect.setCursor(Cursor.HAND);
        rect.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                iv.setOpacity(0.8);
            }
        });
        rect.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                iv.setOpacity(1.0);
            }
        });
        rect.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                iv.setOpacity(0.4);
            }
        });
        rect.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                iv.setOpacity(0.8);
            }
        });
        rect.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (onClicked != null) {
                    onClicked.handle(t);
                }
            }
        });
        this.getChildren().add(rect);
        
    }
    
}
