/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.viwib.utils;

import com.eagerlogic.viwib.Viwib;
import com.eagerlogic.viwib.connectors.TorrentDescriptor;
import com.eagerlogic.viwib.ui.browse.SearchResultItem;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.scene.image.Image;

/**
 *
 * @author dipacs
 */
public class AsyncImageDownloadService {

    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    private AsyncImageDownloadService() {
    }

    public static void download(final TorrentDescriptor descriptor, final IAsyncImageDownloadCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final Image image = Viwib.getConnector().getImageFromUrl(descriptor.getCoverUrl());
                if (image != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            callback.onImageDownloadSuccess(image);
                        }
                    });
                }
            }
        });
    }

    public static void stopService() {
        executor.shutdownNow();
    }
}
