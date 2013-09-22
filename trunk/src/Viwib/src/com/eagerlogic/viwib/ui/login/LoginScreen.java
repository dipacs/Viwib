/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.login;

import com.eagerlogic.viwib.Viwib;
import com.eagerlogic.viwib.connectors.ASiteConnector;
import com.eagerlogic.viwib.connectors.SiteConfig;
import com.eagerlogic.viwib.connectors.common.CommonConnector;
import com.eagerlogic.viwib.connectors.monova.MonovaConnector;
import com.eagerlogic.viwib.connectors.ncore.NCoreSiteConnector;
import com.eagerlogic.viwib.ui.MainFrame;
import com.eagerlogic.viwib.ui.browse.BrowseScreen;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javax.swing.JOptionPane;

/**
 *
 * @author dipacs
 */
public final class LoginScreen extends Parent {

    public final SimpleDoubleProperty width = new SimpleDoubleProperty();
    public final SimpleDoubleProperty height = new SimpleDoubleProperty();
    private TextField tfSite;
    private TextField tfUsername;
    private PasswordField tfPassword;
    private final EventHandler<KeyEvent> onKeyReleasedHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent t) {
            if (t.getCode() == KeyCode.ENTER) {
                doLogin();
            }
        }
    };

    public LoginScreen() {
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


        final GridPane gp = new GridPane();
        gp.setPadding(new Insets(20, 20, 20, 20));
        gp.setVgap(2);
        root.getChildren().add(gp);



        Text txtTitle = new Text("Login");
        txtTitle.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 18));
        gp.add(txtTitle, 0, 0);

        Rectangle sep = new Rectangle(15, 15);
        sep.setFill(Color.TRANSPARENT);
        gp.add(sep, 0, 1);

        Text txtSite = new Text("Torrent Site URL:");
        gp.add(txtSite, 0, 2);

        tfSite = new TextField();
        tfSite.setPrefWidth(300);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tfSite.requestFocus();
            }
        });
        tfSite.setOnKeyReleased(onKeyReleasedHandler);
        gp.add(tfSite, 0, 3);



        sep = new Rectangle(15, 15);
        sep.setFill(Color.TRANSPARENT);
        gp.add(sep, 0, 4);

        Text txtUserName = new Text("Username:");
        gp.add(txtUserName, 0, 5);

        tfUsername = new TextField();
        tfUsername.setOnKeyReleased(onKeyReleasedHandler);
        gp.add(tfUsername, 0, 6);



        sep = new Rectangle(15, 15);
        sep.setFill(Color.TRANSPARENT);
        gp.add(sep, 0, 7);

        Text txtPassword = new Text("Password:");
        gp.add(txtPassword, 0, 8);

        tfPassword = new PasswordField();
        tfPassword.setOnKeyReleased(onKeyReleasedHandler);
        gp.add(tfPassword, 0, 9);



//        sep = new Rectangle(15, 15);
//        sep.setFill(Color.TRANSPARENT);
//        gp.add(sep, 0, 10);
//        
//        final CheckBox cbSaveSiteURL = new CheckBox();
//        cbSaveSiteURL.setText("Save torrent site URL");
//        gp.add(cbSaveSiteURL, 0, 11);
//        
//        sep = new Rectangle(15, 15);
//        sep.setFill(Color.TRANSPARENT);
//        gp.add(sep, 0, 12);
//        
//        final CheckBox cbSaveUsername = new CheckBox();
//        cbSaveUsername.setText("Save torrent site URL");
//        gp.add(cbSaveUsername, 0, 11);
//        
//        sep = new Rectangle(15, 15);
//        sep.setFill(Color.TRANSPARENT);
//        gp.add(sep, 0, 12);
//        
//        final CheckBox cbSavePassword = new CheckBox();
//        cbSavePassword.setText("Save password");
//        gp.add(cbSavePassword, 0, 13);
//        
//        sep = new Rectangle(15, 15);
//        sep.setFill(Color.TRANSPARENT);
//        gp.add(sep, 0, 14);
//        
//        CheckBox cbAutoLogin = new CheckBox();
//        cbAutoLogin.setText("Login automatically");
//        gp.add(cbAutoLogin, 0, 15);

        sep = new Rectangle(15, 25);
        sep.setFill(Color.TRANSPARENT);
        gp.add(sep, 0, 16);

        Button btnLogin = new Button();
        btnLogin.setText("Login");
        btnLogin.setPrefWidth(300);
        btnLogin.onActionProperty().set(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                doLogin();
            }
        });
        gp.add(btnLogin, 0, 17);



        rect.widthProperty().bind(new DoubleBinding() {
            {
                super.bind(gp.boundsInLocalProperty());
            }

            @Override
            protected double computeValue() {
                return gp.boundsInLocalProperty().get().getWidth();
            }
        });
        rect.heightProperty().bind(new DoubleBinding() {
            {
                super.bind(gp.boundsInLocalProperty());
            }

            @Override
            protected double computeValue() {
                return gp.boundsInLocalProperty().get().getHeight();
            }
        });

    }

    private void doLogin() {
        String siteUrl = tfSite.getText();
        if (siteUrl.startsWith("http://")) {
            siteUrl = siteUrl.substring(7);
        }
        if (siteUrl.startsWith("https://")) {
            siteUrl = siteUrl.substring(8);
        }

        // trying simple connector
        ASiteConnector connector = new CommonConnector(siteUrl);
        SiteConfig cfg = connector.getConfig();
        if (cfg != null) {
            if (cfg.isNeedLogin()) {
                try {
                    connector.login(tfUsername.getText(), tfPassword.getText());
                } catch (Throwable ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Login Failed!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            Viwib.setConnector(connector);
            BrowseScreen bs = new BrowseScreen();
            bs.width.bind(Viwib.getInstance().getSlidePanel().widthProperty);
            bs.height.bind(Viwib.getInstance().getSlidePanel().heightProperty);
            MainFrame.getInstance().setBrowseScreen(bs);
            Viwib.getInstance().getSlidePanel().slideLeft(bs);
        } else if (siteUrl.equals("ncore.cc") || siteUrl.equals("www.ncore.cc")) {
            // using built in ncore connector
            connector = new NCoreSiteConnector();
            try {
                connector.login(tfUsername.getText(), tfPassword.getText());
                Viwib.setConnector(connector);
                BrowseScreen bs = new BrowseScreen();
                bs.width.bind(Viwib.getInstance().getSlidePanel().widthProperty);
                bs.height.bind(Viwib.getInstance().getSlidePanel().heightProperty);
                MainFrame.getInstance().setBrowseScreen(bs);
                Viwib.getInstance().getSlidePanel().slideLeft(bs);
            } catch (Throwable ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Login Failed!", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (siteUrl.equals("monova.org") || siteUrl.equals("www.monova.org")) {
            // using built in ncore connector
            connector = new MonovaConnector();
            Viwib.setConnector(connector);
            BrowseScreen bs = new BrowseScreen();
            bs.width.bind(Viwib.getInstance().getSlidePanel().widthProperty);
            bs.height.bind(Viwib.getInstance().getSlidePanel().heightProperty);
            MainFrame.getInstance().setBrowseScreen(bs);
            Viwib.getInstance().getSlidePanel().slideLeft(bs);
        } else {
            JOptionPane.showMessageDialog(null, "The given torrent site doesn't integrated with Viwib.\nPlease ask the owner of the torrent site to integrate it with Viwib.", "Login Failed!", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
