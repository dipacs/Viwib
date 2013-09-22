/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.ui.browse;

import com.eagerlogic.viwib.connectors.Category;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author dipacs
 */
public class CategoryList extends Parent {
    
    
    
    private static final Font MAIN_FONT = Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 18);
    private static final Font CATEGORY_FONT = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14);
    private static final Font ITEM_FONT = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12);
    
    private static final Color MAIN_FONT_COLOR = new Color(1.0, 0.5, 0.0, 1.0);
    private static final Color CATEGORY_FONT_COLOR = new Color(0.2, 0.2, 0.2, 1.0);
    private static final Color ITEM_FONT_COLOR = new Color(0.6, 0.6, 0.6, 1.0);
    
    private final Category[] categories;
    
    public final List<Category> selectedCategories = new ArrayList<Category>();

    public CategoryList(Category[] categories) {
        this.categories = categories;
        
        Category rootCat = new Category(null, "CATEGORIES", null, false);
        rootCat.setChildren(categories);
        
        this.getChildren().add(categoryToNode(rootCat));
    }
    
    private Node categoryToNode(final Category cat) {
        if (cat.getChildren() != null && cat.getChildren().length > 0) {
            // there is some children
            VBox root = new VBox(2);
            Text lblParent = new Text(cat.getName());
            if (cat.getId() == null) {
                lblParent.setFont(MAIN_FONT);
                lblParent.setFill(MAIN_FONT_COLOR);
            } else {
                lblParent.setFont(CATEGORY_FONT);
                lblParent.setFill(CATEGORY_FONT_COLOR);
            }
            root.getChildren().add(lblParent);
            
            for (Category c : cat.getChildren()) {
                Group g = new Group();
                Rectangle rect = new Rectangle(1, 1);
                rect.setFill(Color.TRANSPARENT);
                g.getChildren().add(rect);
                Node n = categoryToNode(c);
                n.setTranslateX(20);
                g.getChildren().add(n);
                
                root.getChildren().add(g);
            }
            return root;
        } else {
            CheckBox cbCat = new CheckBox(cat.getName());
            cbCat.setStyle("-fx-text-fill: #505050;");
            cbCat.selectedProperty().addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        if (!selectedCategories.contains(cat)) {
                            selectedCategories.add(cat);
                        }
                    } else {
                        selectedCategories.remove(cat);
                    }
                }
            });
            return cbCat;
        }
        
    }
    
}
