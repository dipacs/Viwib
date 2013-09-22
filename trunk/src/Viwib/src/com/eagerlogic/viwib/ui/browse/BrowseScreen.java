/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.browse;

import com.eagerlogic.viwib.Viwib;
import com.eagerlogic.viwib.connectors.Category;
import com.eagerlogic.viwib.connectors.SearchResult;
import com.eagerlogic.viwib.connectors.SiteConfig;
import com.eagerlogic.viwib.connectors.SortCategory;
import com.eagerlogic.viwib.connectors.TorrentDescriptor;
import com.eagerlogic.viwib.torrent.manager.TorrentFile;
import com.eagerlogic.viwib.ui.MainFrame;
import com.eagerlogic.viwib.ui.details.DetailsScreen;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.bitlet.wetorrent.Metafile;

/**
 *
 * @author dipacs
 */
public class BrowseScreen extends Parent {

    public final SimpleDoubleProperty width = new SimpleDoubleProperty();
    public final SimpleDoubleProperty height = new SimpleDoubleProperty();
    private final TextField tfSearch;
    private SiteConfig siteConfig;
    private CategoryList categoryList;
    private VBox vbSearchResults;
    private ChoiceBox<String> cbOrder;
    private boolean needRefresh = true;
    private Group popupGroup = new Group();
    private ScrollPane sp;
    
    private Pager pager;

    public BrowseScreen() {
        pager = new Pager();
        pager.setOnNextClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event t) {
                pager.pageIndexProperty.set(pager.pageIndexProperty.get() + 1);
                refreshResult();
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        sp.vvalueProperty().set(0);
                    }
                });
                
            }
        });
        pager.setOnPreviousClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event t) {
                pager.pageIndexProperty.set(pager.pageIndexProperty.get() - 1);
                refreshResult();
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        sp.vvalueProperty().set(0);
                    }
                });
            }
        });
        this.siteConfig = Viwib.getConnector().getConfig();

        HBox hb = new HBox(40);
        hb.setTranslateX(10);
        hb.setTranslateY(80);
        this.getChildren().add(hb);
        
        VBox vbc = new VBox(10);
        hb.getChildren().add(vbc);
        
        tfSearch = new TextField();
        tfSearch.setPromptText("Type here to search");
        tfSearch.setPrefWidth(180);
        tfSearch.setOnKeyTyped(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                if ("\n".equals(t.getCharacter())) {
                    pager.pageIndexProperty.set(0);
                    refreshResult();
                }
            }
        });
        vbc.getChildren().add(tfSearch);
        
        HBox hbOrder = new HBox(10);
        hbOrder.setAlignment(Pos.CENTER_RIGHT);
        vbc.getChildren().add(hbOrder);
        
        Text txtOrderBy = new Text("Order by:");
        txtOrderBy.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
        hbOrder.getChildren().add(txtOrderBy);
        
        cbOrder = new ChoiceBox();
        for (SortCategory cat : siteConfig.getSortCategories()) {
            cbOrder.getItems().add(cat.getName());
        }
        hbOrder.getChildren().add(cbOrder);
        cbOrder.getSelectionModel().selectFirst();
        final ChoiceBox cbAscDesc = new ChoiceBox();
        cbOrder.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                needRefresh = false;
                try {
                cbAscDesc.getSelectionModel().select(siteConfig.getSortCategories()[cbOrder.getSelectionModel().getSelectedIndex()].isDesc() ? 1 : 0);
                } finally {
                    needRefresh = true;
                }
                pager.pageIndexProperty.set(0);
                refreshResult();
            }
        });
        cbAscDesc.getItems().add("Ascending");
        cbAscDesc.getItems().add("Descending");
        if (siteConfig.getSortCategories()[0].isDesc()) {
            cbAscDesc.getSelectionModel().selectLast();
        } else {
            cbAscDesc.getSelectionModel().selectFirst();
        }
        
        cbAscDesc.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                int nv = ((Integer) t1).intValue();
                siteConfig.getSortCategories()[cbOrder.getSelectionModel().getSelectedIndex()].setDesc(nv == 1);
                pager.pageIndexProperty.set(0);
                refreshResult();
            }
        });
        
        HBox hbAscDesc = new HBox();
        hbAscDesc.getChildren().add(cbAscDesc);
        hbAscDesc.setAlignment(Pos.CENTER_RIGHT);
        vbc.getChildren().add(hbAscDesc);
        
        categoryList = new CategoryList(siteConfig.getCategories());
        vbc.getChildren().add(categoryList);

        VBox vb = new VBox(20);
        vb.setFillWidth(false);
        hb.getChildren().add(vb);

//        final HBox hb2 = new HBox(20);
//
//        hb2.translateXProperty().bind(new DoubleBinding() {
//            {
//                super.bind(width, hb2.boundsInLocalProperty(), categoryList.boundsInLocalProperty());
//            }
//
//            @Override
//            protected double computeValue() {
//                double res = (width.get() - 80 - categoryList.getBoundsInLocal().getWidth() - hb2.getBoundsInLocal().getWidth()) / 2.0;
////                System.out.println("------------------");
////                System.out.println(width.get());
////                System.out.println(categoryList.getBoundsInLocal().getWidth());
////                System.out.println(hb2.getBoundsInLocal().getWidth());
////                System.out.println(res);
//                return res;
//            }
//        });
//        vb.getChildren().add(hb2);


        sp = new ScrollPane();
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        sp.prefWidthProperty().set(770);
        sp.prefWidthProperty().bind(new DoubleBinding() {
            {
                super.bind(width, categoryList.boundsInLocalProperty());
            }

            @Override
            protected double computeValue() {
                return width.get() - 115 - categoryList.getBoundsInLocal().getWidth();
            }
        });
        sp.prefHeightProperty().bind(this.height.subtract(80));
        vb.getChildren().add(sp);
        
        VBox vbSearchList = new VBox(20);
        sp.setContent(vbSearchList);

        vbSearchResults = new VBox(20);
        vbSearchList.getChildren().add(vbSearchResults);
        
        
        vbSearchList.getChildren().add(pager);
        pager.setTranslateX(300);
        
        Rectangle rect = new Rectangle(10, 10);
        rect.setFill(Color.TRANSPARENT);
        vbSearchList.getChildren().add(rect);

        refreshResult();
        
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                sp.requestFocus();
            }
        });

//        tfSearch = new TextField();
//        tfSearch.prefWidthProperty().bind(width.subtract(100));
//        tfSearch.setTranslateX(50);
//        tfSearch.setTranslateY(10);
//        this.getChildren().add(tfSearch);
    }

    private void refreshResult() {
        if (!needRefresh) {
            needRefresh = true;
            return;
        }
        vbSearchResults.getChildren().clear();
        if (categoryList.selectedCategories.size() < 1) {
            // all categories
            ArrayList<Category> selectedCategories = new ArrayList<Category>();
            Category rootCat = new Category(null, null, null, false);
            rootCat.setChildren(siteConfig.getCategories());
            collectAllCategories(rootCat, selectedCategories);
            SearchResult res = Viwib.getConnector().search(tfSearch.getText(), selectedCategories.toArray(new Category[0]), siteConfig.getSortCategories()[cbOrder.getSelectionModel().getSelectedIndex()], pager.pageIndexProperty.get());
            for (final TorrentDescriptor descriptor : res.getResult()) {
                SearchResultItem item = new SearchResultItem(descriptor);
                item.widthProperty.set(700);
                item.heightProperty.set(150);
//                item.onClicked = new EventHandler<SearchResultItem.ItemClickedEvent>() {
//                    @Override
//                    public void handle(SearchResultItem.ItemClickedEvent t) {
//                        try {
//                            byte[] torrentFile = Viwib.getConnector().getTorrentFileFromUrl(descriptor.getTorrentUrl());
//                            Metafile mf = new Metafile(new ByteArrayInputStream(torrentFile));
//                            
//                            
//                            TorrentFile tf = new TorrentFile(mf);
//                            DetailsScreen ds = new DetailsScreen(descriptor, tf);
//                            MainFrame.getInstance().setDetailsScreen(ds);
//                            Viwib.getInstance().slideLeft(ds);
//                        } catch (IOException ex) {
//                            Logger.getLogger(BrowseScreen.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (NoSuchAlgorithmException ex) {
//                            Logger.getLogger(BrowseScreen.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                };
                this.vbSearchResults.getChildren().add(item);
            }
            pager.hasNextProperty.set(((res.getTotalCount() / res.getPageSize())) > pager.pageIndexProperty.get());
        } else {
            // selected categories
            Category[] selectedCategories = categoryList.selectedCategories.toArray(new Category[0]);
            siteConfig.getSortCategories()[4].setDesc(true);
            SearchResult res = Viwib.getConnector().search(tfSearch.getText(), selectedCategories, siteConfig.getSortCategories()[cbOrder.getSelectionModel().getSelectedIndex()], 0);
            for (final TorrentDescriptor descriptor : res.getResult()) {
                SearchResultItem item = new SearchResultItem(descriptor);
                item.widthProperty.set(700);
                item.heightProperty.set(150);
//                item.onClicked = new EventHandler<SearchResultItem.ItemClickedEvent>() {
//                    @Override
//                    public void handle(SearchResultItem.ItemClickedEvent t) {
//                        try {
//                            byte[] torrentFile = Viwib.getConnector().getTorrentFileFromUrl(descriptor.getTorrentUrl());
//                            Metafile mf = new Metafile(new ByteArrayInputStream(torrentFile));
//                            
//                            
//                            TorrentFile tf = new TorrentFile(mf);
//                            DetailsScreen ds = new DetailsScreen(descriptor, tf);
//                            MainFrame.getInstance().setDetailsScreen(ds);
//                            Viwib.getInstance().slideLeft(ds);
//                        } catch (IOException ex) {
//                            Logger.getLogger(BrowseScreen.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (NoSuchAlgorithmException ex) {
//                            Logger.getLogger(BrowseScreen.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                };
                this.vbSearchResults.getChildren().add(item);
            }
            pager.hasNextProperty.set(((res.getTotalCount() / res.getPageSize())) > pager.pageIndexProperty.get());
        }
    }

    private void collectAllCategories(Category category, List<Category> result) {
        if (category.getChildren() != null && category.getChildren().length > 0) {
            for (Category cat : category.getChildren()) {
                if (cat.isAdult()) {
                    continue;
                }
                collectAllCategories(cat, result);
            }
        } else {
            result.add(category);
        }
    }
}
