/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib;

import com.eagerlogic.viwib.config.Config;
import com.eagerlogic.viwib.connectors.ASiteConnector;
import com.eagerlogic.viwib.connectors.ncore.NCoreSiteConnector;
import com.eagerlogic.viwib.ui.MainFrame;
import com.eagerlogic.viwib.ui.SlidePanel;
import com.eagerlogic.viwib.ui.browse.BrowseScreen;
import com.eagerlogic.viwib.ui.login.LoginScreen;
import com.eagerlogic.viwib.ui.welcome.WelcomeScreen;
import com.eagerlogic.viwib.utils.JarUtils;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author dipacs
 */
public class Viwib extends Scene {

    private static Viwib instance;
    private static ASiteConnector connector;

    public static Viwib getInstance() {
        return instance;
    }

    public static ASiteConnector getConnector() {
        return connector;
    }

    public static void setConnector(ASiteConnector connector) {
        Viwib.connector = connector;
    }
    private final Group root;
    private SlidePanel slidePanel;

    public Viwib() {
        super(new Group());
        root = (Group) this.getRoot();
        instance = this;
        Rectangle bg = new Rectangle();
        this.getStylesheets().add(this.getClass().getResource("/com/eagerlogic/viwib/ui/skin/default.css").toExternalForm());

        bg.widthProperty().bind(this.widthProperty());
        bg.heightProperty().bind(this.heightProperty());
        LinearGradient lg = new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, new Stop(0.0, new Color(0.9, 0.9, 0.9, 1.0)), new Stop(0.5, new Color(0.98, 0.98, 0.98, 1.0)), new Stop(1.0, new Color(0.8, 0.8, 0.8, 1.0)));
        bg.setFill(lg);
        root.getChildren().add(bg);

        slidePanel = new SlidePanel();
        slidePanel.widthProperty.bind(this.widthProperty());
        slidePanel.heightProperty.bind(this.heightProperty());
        root.getChildren().add(slidePanel);

        Config.load();
        if (Config.getSaveLocation() == null) {
            // first start
            WelcomeScreen ws = new WelcomeScreen();
            ws.width.bind(slidePanel.widthProperty);
            ws.height.bind(slidePanel.heightProperty);
            slidePanel.slideLeft(ws);

            // TODO copy VLC in to the appropriate location
        } else {
            LoginScreen ls = new LoginScreen();
            ls.width.bind(slidePanel.widthProperty);
            ls.height.bind(slidePanel.heightProperty);
            slidePanel.slideLeft(ls);
        }

        
    }
    
    

    public SlidePanel getSlidePanel() {
        return slidePanel;
    }

    public void slideLeft(Node node) {
        this.slidePanel.slideLeft(node);
    }

    public void slideRight(Node node) {
        this.slidePanel.slideRight(node);
    }
}
