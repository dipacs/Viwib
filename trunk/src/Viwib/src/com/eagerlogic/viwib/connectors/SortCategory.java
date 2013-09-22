/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.connectors;

/**
 *
 * @author dipacs
 */
public class SortCategory {
    
    private final String id;
    private final String name;
    private boolean desc = false;

    public SortCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDesc() {
        return desc;
    }
    
}
