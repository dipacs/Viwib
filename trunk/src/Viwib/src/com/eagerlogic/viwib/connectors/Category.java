/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.connectors;

/**
 *
 * @author dipacs
 */
public class Category {
    
    private String id;
    private String name;
    private String description;
    private Category[] children;
    private boolean adult;

    public Category() {
    }

    public Category(String id, String name, String description, boolean adult) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.adult = adult;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category[] getChildren() {
        return children;
    }

    public void setChildren(Category[] children) {
        this.children = children;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }
    
}
