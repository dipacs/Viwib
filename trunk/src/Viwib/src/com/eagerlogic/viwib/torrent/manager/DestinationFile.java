/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.torrent.manager;

import java.io.Serializable;

/**
 *
 * @author dipacs
 */
public class DestinationFile {
    
    private String path;
    private long offset;
    private long length;

    public DestinationFile(String path, long offset, long length) {
        this.path = path;
        this.offset = offset;
        this.length = length;
    }
    
    public DestinationFile() {}

    public String getPath() {
        return path;
    }

    public long getOffset() {
        return offset;
    }

    public long getLength() {
        return length;
    }
    
}
