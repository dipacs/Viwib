/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.welcome;

import com.eagerlogic.viwib.Viwib;
import com.eagerlogic.viwib.config.Config;
import com.eagerlogic.viwib.ui.addtracker.AddTrackerScreen;
import com.eagerlogic.viwib.ui.login.LoginScreen;
import java.awt.EventQueue;
import java.awt.Panel;
import java.io.File;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author dipacs
 */
public class WelcomeScreen extends Parent {

    public final SimpleDoubleProperty width = new SimpleDoubleProperty();
    public final SimpleDoubleProperty height = new SimpleDoubleProperty();

    public WelcomeScreen() {
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

        Label lblTitle = new Label("Welcome");
        lblTitle.getStyleClass().add("lblScreenTitle");
        vbox.getChildren().add(lblTitle);

        Label lblInfo = new Label("It seems that this is the first time when you start Viwib. Please choose a folder "
                + "where Viwib will store your downloaded files. If you dont know what does this mean, just "
                + "leave this on the default value.");
        lblInfo.getStyleClass().add("lblScreenInfo");
        lblInfo.setWrapText(true);
        lblInfo.setTextAlignment(TextAlignment.CENTER);
        lblInfo.setPrefWidth(500);
        vbox.getChildren().add(lblInfo);

        VBox vbox2 = new VBox(5);
        vbox.getChildren().add(vbox2);

        Label lblSaveFolder = new Label("Select Save Location:");
        lblSaveFolder.getStyleClass().add("lblFieldTitle");
        vbox2.getChildren().add(lblSaveFolder);

        String defSaveUrl = Config.VIWIB_URL;
        if (!defSaveUrl.endsWith(File.separator)) {
            defSaveUrl += File.separator;
        }
        defSaveUrl += "Downloads" + File.separator;

        final Button btnSaveLocation = new Button(defSaveUrl);
        btnSaveLocation.setPrefWidth(500);
        btnSaveLocation.setCursor(Cursor.HAND);
        btnSaveLocation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JFileChooser jfc = new JFileChooser();
                        jfc.setMultiSelectionEnabled(false);
                        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            final String newUrl = jfc.getSelectedFile().getAbsolutePath();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    btnSaveLocation.setText(newUrl);
                                }
                            });
                        }
                    }
                });
            }
        });
        vbox2.getChildren().add(btnSaveLocation);


        Button btnSave = new Button("Save");
        btnSave.getStyleClass().add("btnApprove");
        btnSave.setTranslateX(400);
        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                String saveLocation = btnSaveLocation.getText();
                if (saveLocation == null) {
                    showErrorMessage("Invalid Save Location", "The selected save location is invalid. Please check if the selected folder is accessible and writeable.");
                    return;
                }
                File f = null;
                try {
                    f = new File(saveLocation);
                    if (!f.exists()) {
                        if (!f.mkdirs()) {
                            showErrorMessage("Invalid Save Location", "The selected save location is invalid. Please check if the selected folder is accessible and writeable.");
                            return;
                        }
                    }
                } catch (Throwable t) {
                    showErrorMessage("Invalid Save Location", "The selected save location is invalid. Please check if the selected folder is accessible and writeable.");
                    return;
                }

                Config.setSaveLocation(saveLocation);
                Config.save();

                LoginScreen loginScren = new LoginScreen();
                loginScren.width.bind(Viwib.getInstance().getSlidePanel().widthProperty);
                loginScren.height.bind(Viwib.getInstance().getSlidePanel().heightProperty);
                Viwib.getInstance().slideLeft(loginScren);
            }
        });
        vbox.getChildren().add(btnSave);
        btnSave.setAlignment(Pos.TOP_RIGHT);
        btnSave.setManaged(true);



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

    private void showErrorMessage(final String title, final String message) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
