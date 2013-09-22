/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bitlet.wetorrent.pieceChooser;

import java.util.BitSet;
import java.util.Random;
import org.bitlet.wetorrent.Torrent;
import org.bitlet.wetorrent.disk.PlainFileSystemTorrentDisk;
import org.bitlet.wetorrent.peer.Peer;

/**
 *
 * @author dipacs
 */
public class SequentialPieceChooser extends PieceChooser {

    private Object syncO = new Object();
    private long[] pieceRequestTimes;

    @Override
    protected Integer choosePiece(Peer peer, int[] piecesFrequencies) {
        if (pieceRequestTimes == null) {
            synchronized (syncO) {
                if (pieceRequestTimes == null) {
                    pieceRequestTimes = new long[piecesFrequencies.length];
                }
            }
        }
        Integer res = null;
        if (torrent.getPrefStart() > -1 && torrent.getPrefEnd() > -1) {
            // check if there is any missing piece in the random area
            boolean allAvailable = true;
            int startPiece = (int) (torrent.getPrefStart() / torrent.getMetafile().getPieceLength());
            int endPiece = (int) (torrent.getPrefEnd() / torrent.getMetafile().getPieceLength());
            for (int i = startPiece; i <= endPiece; i++) {
                if (!torrent.getTorrentDisk().isCompleted(i)) {
                    allAvailable = false;
                    break;
                }
            }
            if (allAvailable) {
                res = chooseRandomPiece(peer, piecesFrequencies);
                return res;
            }

            // checking if end downloading is finished
            int endPieceCount = (int) (1024 * 1024 * 4 / torrent.getMetafile().getPieceLength() + 1);
            allAvailable = true;
            for (int i = endPiece; i > endPiece - endPieceCount; i--) {
                if (!torrent.getTorrentDisk().isCompleted(i)) {
                    allAvailable = false;
                    break;
                }
            }
            if (!allAvailable) {
                res = chooseEndPiece(peer, piecesFrequencies);
                if (res == null) {
                    res = chooseStartPiece(peer, piecesFrequencies, 90 * 12);
                    if (res == null) {
                        res = chooseStartPiece(peer, piecesFrequencies, 90 * 6);
                    }
                }
                return res;
            }

            res = chooseStartPiece(peer, piecesFrequencies, 90 * 12);
            if (res == null) {
                res = chooseStartPiece(peer, piecesFrequencies, 90 * 6);
            }
            if (res == null) {
                res = chooseStartPiece(peer, piecesFrequencies, 90 * 3);
            }
            if (res == null) {
                res = chooseStartPiece(peer, piecesFrequencies, 90);
            }
            if (res == null) {
                res = chooseStartPiece(peer, piecesFrequencies, 45);
            }
            if (res == null) {
                res = chooseStartPiece(peer, piecesFrequencies, 20);
            }
            if (res == null) {
                res = chooseStartPiece(peer, piecesFrequencies, 10);
            }
            if (res == null) {
                res = chooseStartPiece(peer, piecesFrequencies, 5);
            }
            if (res == null) {
                res = chooseStartPiece(peer, piecesFrequencies, 2);
            }
            return res;
        } else {
            res = chooseRandomPiece(peer, piecesFrequencies);
            return res;
        }
    }

    private Integer chooseStartPiece(Peer peer, int[] piecesFrequencies, int divider) {
        int startIndex = (int) (torrent.getPrefStart() / torrent.getMetafile().getPieceLength());;
        int endIndex = (int) (torrent.getPrefEnd() / torrent.getMetafile().getPieceLength());;
        long byteCount = (endIndex - startIndex + 1) * torrent.getMetafile().getPieceLength();
        long bufferByteCount = byteCount / (90 * 1);
        int bufferCount = (int) (bufferByteCount / torrent.getMetafile().getPieceLength() + 1);
        if (bufferCount < 5) {
            bufferCount = 5;
        }

        boolean[] availablePieces = new boolean[bufferCount];
        for (int i = 0; i < availablePieces.length; i++) {
            availablePieces[i] = true;
        }
        int firstMissingIndex = -1;
        int missingPieceCount = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            if (firstMissingIndex > -1 && i >= firstMissingIndex + bufferCount) {
                break;
            }
            boolean isComplete = torrent.getTorrentDisk().isCompleted(i);
            if (!isComplete & firstMissingIndex < 0) {
                firstMissingIndex = i;
            }
            if (firstMissingIndex > -1) {
                availablePieces[i - firstMissingIndex] = isComplete;
                if (!isComplete) {
                    missingPieceCount++;
                }
            }
        }

        if (firstMissingIndex > -1) {
            int bestPiece = -1;
//            if ((double) missingPieceCount / (double) bufferCount > 0.6) {
//                // end game
//                long bestPieceTime = Long.MAX_VALUE;
//                for (int i = firstMissingIndex; i < firstMissingIndex + bufferCount; i++) {
//                    if (peer.hasPiece(i) && !torrent.getTorrentDisk().isCompleted(i)) {
//                        if (getPieceRequestTime(i) < bestPieceTime) {
//                            bestPieceTime = getPieceRequestTime(i);
//                            bestPiece = i;
//                        }
//                    }
//                }
//            } else {
            long bestPieceTime = Long.MAX_VALUE;
            for (int i = firstMissingIndex; i < firstMissingIndex + bufferCount; i++) {
                if (peer.hasPiece(i) && !torrent.getTorrentDisk().isCompleted(i) && !isCompletingPiece(i)) {
                    if (getPieceRequestTime(i) < bestPieceTime) {
                        bestPieceTime = getPieceRequestTime(i);
                        bestPiece = i;
                    }
                }
            }
//            }

            if (bestPiece > -1) {
                setPieceRequestTime(bestPiece);
                return bestPiece;
            }
        }


        return null;
    }

    private Integer chooseEndPiece(Peer peer, int[] piecesFrequencies) {
        int bufferCount = (int) (1024 * 1024 * 4 / torrent.getMetafile().getPieceLength() + 1);
        int endIndex = (int) (torrent.getPrefEnd() / torrent.getMetafile().getPieceLength());;
        int startIndex = endIndex - bufferCount;

        boolean[] availablePieces = new boolean[bufferCount];
        int firstMissingIndex = -1;
        int missingPieceCount = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            if (firstMissingIndex > -1 && i >= firstMissingIndex + bufferCount) {
                break;
            }
            boolean isComplete = torrent.getTorrentDisk().isCompleted(i);
            if (!isComplete & firstMissingIndex < 0) {
                firstMissingIndex = i;
            }
            if (firstMissingIndex > -1) {
                availablePieces[i - firstMissingIndex] = isComplete;
                if (!isComplete) {
                    missingPieceCount++;
                }
            }
        }

        if (firstMissingIndex > -1) {
            int bestPiece = -1;
//            if ((double) missingPieceCount / (double) bufferCount > 0.6) {
//                // end game
//                long bestPieceTime = Long.MAX_VALUE;
//                for (int i = firstMissingIndex; i < firstMissingIndex + bufferCount; i++) {
//                    if (peer.hasPiece(i) && !torrent.getTorrentDisk().isCompleted(i)) {
//                        if (getPieceRequestTime(i) < bestPieceTime) {
//                            bestPieceTime = getPieceRequestTime(i);
//                            bestPiece = i;
//                        }
//                    }
//                }
//            } else {
            long bestPieceTime = Long.MAX_VALUE;
            for (int i = firstMissingIndex; i < firstMissingIndex + bufferCount; i++) {
                if (peer.hasPiece(i) && !torrent.getTorrentDisk().isCompleted(i) && !isCompletingPiece(i)) {
                    if (getPieceRequestTime(i) < bestPieceTime) {
                        bestPieceTime = getPieceRequestTime(i);
                        bestPiece = i;
                    }
                }
            }
//            }

            if (bestPiece > -1) {
                setPieceRequestTime(bestPiece);
                return bestPiece;
            }
        }

        return null;
    }

    private Integer chooseRandomPiece(Peer peer, int[] piecesFrequencies) {
        int[] probabilities = piecesFrequencies.clone();
        int maxFrequency = 0;
        Torrent torrent = getTorrent();
        for (int i = 0; i < torrent.getMetafile().getPieces().size(); i++) {
            if (peer.hasPiece(i) && !torrent.getTorrentDisk().isCompleted(i) && !isCompletingPiece(i)) {
                if (maxFrequency < probabilities[i]) {
                    maxFrequency = probabilities[i];
                }
            } else {
                probabilities[i] = Integer.MAX_VALUE;
            }
        }

        int total = 0;
        for (int i = 0; i < torrent.getMetafile().getPieces().size(); i++) {
            if (probabilities[i] == Integer.MAX_VALUE) {
                probabilities[i] = 0;
            } else {
                probabilities[i] = 1 + maxFrequency - probabilities[i];
            }
            total += probabilities[i];
            probabilities[i] = total;
        }

        if (total == 0) {
            return null;
        }
        long random = new Random(System.currentTimeMillis()).nextInt(total);
        int i;
        if (random < probabilities[0]) {
            setPieceRequestTime(0);
            return 0;
        }
        for (i = 1; i < probabilities.length; i++) {
            if (probabilities[i - 1] <= random && probabilities[i] > random) {
                break;
            }
        }

        setPieceRequestTime(i);
        return i;
    }

    private long getPieceRequestTime(int index) {
        synchronized (syncO) {
            return this.pieceRequestTimes[index];
        }
    }

    private void setPieceRequestTime(int index) {
        synchronized (syncO) {
            this.pieceRequestTimes[index] = System.currentTimeMillis();
        }
    }

    public long getSequentiallyFinishedBytes() {
        long len = this.torrent.getMetafile().getPieces().size();
        long res = 0;
        long plen = this.torrent.getMetafile().getPieceLength();
        int prefStartIndex = (int) (this.torrent.getPrefStart() / this.torrent.getMetafile().getPieceLength());
        int prefEndIndex = (int) (this.torrent.getPrefEnd() / this.torrent.getMetafile().getPieceLength());
        if (prefStartIndex > -1 && prefEndIndex > -1) {
            for (int i = prefStartIndex; i <= prefEndIndex; i++) {
                if (this.torrent.getTorrentDisk().isCompleted(i)) {
                    res += plen;
                } else {
                    return res;
                }
            }
        } else {
            for (int i = 0; i < len; i++) {
                if (this.torrent.getTorrentDisk().isCompleted(i)) {
                    res += plen;
                } else {
                    return res;
                }
            }
        }
        return res;
    }

    public long getFinishedBytes() {
        long pcount =  ((PlainFileSystemTorrentDisk)this.torrent.getTorrentDisk()).getPieceCount();
        long res = 0;
        long plen = this.torrent.getMetafile().getPieceLength();
        for (int i = 0; i < pcount; i++) {
            if (this.torrent.getTorrentDisk().isCompleted(i)) {
                if (i == pcount - 1) {
                    res += this.torrent.getTorrentDisk().getLength(i);
                } else {
                    res += plen;
                }
            }
        }
        return res;
    }
}
