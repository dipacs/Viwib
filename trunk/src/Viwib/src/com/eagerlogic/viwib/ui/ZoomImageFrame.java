/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui;

import javafx.beans.binding.Binding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author dipacs
 */
public class ZoomImageFrame extends Parent {

    public final SimpleDoubleProperty widthProperty = new SimpleDoubleProperty(170);
    public final SimpleDoubleProperty heightProperty = new SimpleDoubleProperty(170);
    private Image image;
    private ImageView iv;

    public ZoomImageFrame() {
        Group root = new Group();
        this.getChildren().add(root);
        
        iv = new ImageView();

        Rectangle bg = new Rectangle();
        bg.widthProperty().bind(this.widthProperty);
        bg.heightProperty().bind(this.heightProperty);
        bg.setFill(Color.rgb(240, 240, 240));
        bg.setStroke(Color.rgb(192, 192, 192));
        bg.setStrokeWidth(1);
        bg.setStrokeType(StrokeType.OUTSIDE);
        root.getChildren().add(bg);

        iv.fitWidthProperty().bind(widthProperty.subtract(10));
        iv.fitHeightProperty().bind(heightProperty.subtract(10));
        iv.setPreserveRatio(true);
        iv.setTranslateX(5);
        iv.setTranslateY(5);
        this.getChildren().add(iv);
        
        recalculateViewPort();

    }
    
    private void recalculateViewPort() {
        if (image == null) {
            return;
        }
        if (image.getWidth() < 1 || image.getHeight() < 1) {
            return;
        }
        
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        
        if (w == h) {
            // square
            iv.setViewport(new Rectangle2D(0, 0, w, h));
        } else if (w > h) {
            // landscape
            iv.setViewport(new Rectangle2D(0, (h-w) / 2, w, w));
        } else {
            // portrait
            iv.setViewport(new Rectangle2D((w - h) / 2, 0, h, h));
        }
    }
    
    public void setImage(Image image) {
        this.image = image;
        this.iv.setImage(image);
        recalculateViewPort();
    }
}
