/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.player;

import java.util.BitSet;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author dipacs
 */
//public class PlayerSeeker extends Parent {
//    
//    public final SimpleDoubleProperty widthProperty = new SimpleDoubleProperty(200);
//    public final SimpleDoubleProperty heightProperty = new SimpleDoubleProperty(200);
//    public final SimpleDoubleProperty positionProperty = new SimpleDoubleProperty(200);
//    public final SimpleDoubleProperty downloadedProperty = new SimpleDoubleProperty(200);
//    
//    private Group pieceGroup;
//
//    public PlayerSeeker() {
//        Rectangle rectBg = new Rectangle();
//        rectBg.widthProperty().bind(widthProperty);
//        rectBg.heightProperty().bind(heightProperty);
//        this.getChildren().add(rectBg);
//        
//        LinearGradient bg = new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, 
//                new Stop(0.0, Color.BLACK),
//                new Stop(1.0, new Color(0.05, 0.05, 0.05, 1.0))
//                );
//        rectBg.setFill(bg);
//        
//        pieceGroup = new Group();
//        this.getChildren().add(pieceGroup);
//        
//        Rectangle rectDownloaded = new Rectangle();
//        rectDownloaded.widthProperty().bind(new DoubleBinding() {
//            
//            {
//                super.bind(widthProperty, downloadedProperty);
//            }
//
//            @Override
//            protected double computeValue() {
//                return widthProperty.get() * downloadedProperty.get();
//            }
//        });
//        rectDownloaded.heightProperty().bind(heightProperty);
//        rectDownloaded.setFill(new Color(0.5, 0.5, 0.5, 0.9));
//        this.getChildren().add(rectDownloaded);
//        
//        Rectangle rectPos = new Rectangle();
//        rectPos.widthProperty().bind(new DoubleBinding() {
//            
//            {
//                super.bind(widthProperty, positionProperty);
//            }
//
//            @Override
//            protected double computeValue() {
//                return widthProperty.get() * positionProperty.get();
//            }
//        });
//        rectPos.heightProperty().bind(heightProperty);
//        rectPos.setFill(new Color(0.0, 0.7, 1.0, 0.9));
//        this.getChildren().add(rectPos);
//        
//    }
//    
//    public void setCompletedPieces(BitSet completedPieces, int totalPieceCount) {
//        pieceGroup.getChildren().clear();
//        double height = this.heightProperty.get();
//        double width = widthProperty.get();
//        for (int i = 0; i < totalPieceCount; i++) {
//            if (completedPieces.get(i)) {
//                double x = (i / (double)totalPieceCount) * width;
//                Rectangle rect = new Rectangle();
//                rect.setTranslateX(x);
//                double sw = width / totalPieceCount;
//                if (sw < 1.0) {
//                    sw = 1.0;
//                }
//                rect.setWidth(sw);
//                rect.setHeight(height);
//                rect.setFill(Color.DARKRED);
////                Line ln = new Line(x, 0, x, height);
////                ln.setFill(Color.DARKRED);
//                //ln.setStroke(Color.DARKRED);
//                //ln.setStrokeWidth(sw);
//                pieceGroup.getChildren().add(rect);
//            }
//        }
//    }
//    
//    
//    
//    
//}
