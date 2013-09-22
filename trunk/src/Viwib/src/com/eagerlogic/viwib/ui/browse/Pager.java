/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.browse;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 *
 * @author dipacs
 */
public class Pager extends Parent {
    
    public final SimpleIntegerProperty pageIndexProperty = new SimpleIntegerProperty(0);
    public final SimpleBooleanProperty hasNextProperty = new SimpleBooleanProperty(true);
    
    private EventHandler<Event> onNextClicked;
    private EventHandler<Event> onPreviousClicked;

    public Pager() {
        HBox hb = new HBox(20);
        this.getChildren().add(hb);
        
        Button btnPrevious = new Button("< Prev");
        btnPrevious.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                if (onPreviousClicked != null) {
                    onPreviousClicked.handle(t);
                }
            }
        });
        btnPrevious.disableProperty().bind(new BooleanBinding() {
            
            {
                super.bind(pageIndexProperty);
            }

            @Override
            protected boolean computeValue() {
                return pageIndexProperty.get() < 1;
            }
        });
        hb.getChildren().add(btnPrevious);
        
        Text txtIndex = new Text();
        txtIndex.textProperty().bind(new StringBinding() {
            
            {
                super.bind(pageIndexProperty);
            }

            @Override
            protected String computeValue() {
                return "" + (pageIndexProperty.get() + 1);
            }
        });
        hb.getChildren().add(txtIndex);
        
        Button btnNext = new Button("Next >");
        btnNext.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                if (onNextClicked != null) {
                    onNextClicked.handle(t);
                }
            }
        });
        btnNext.disableProperty().bind(hasNextProperty.not());
        hb.getChildren().add(btnNext);
    }

    public EventHandler<Event> getOnNextClicked() {
        return onNextClicked;
    }

    public void setOnNextClicked(EventHandler<Event> onNextClicked) {
        this.onNextClicked = onNextClicked;
    }

    public EventHandler<Event> getOnPreviousClicked() {
        return onPreviousClicked;
    }

    public void setOnPreviousClicked(EventHandler<Event> onPreviousClicked) {
        this.onPreviousClicked = onPreviousClicked;
    }
    
}
