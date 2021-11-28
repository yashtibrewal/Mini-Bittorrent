package uf.cs.cn.peer;

import uf.cs.cn.utils.CommonConfigFileReader;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ChokeHandler extends Thread {
    private ScheduledFuture<?> job;
    private ScheduledExecutorService scheduler;

    public ChokeHandler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startJob() {
        this.job = this.scheduler.scheduleAtFixedRate(this, 10, CommonConfigFileReader.un_chocking_interval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        Peer.getInstance().calculatePreferredNeighbours();
        Peer.getInstance().resetDownloadCounters();
        Peer.getInstance().getPreferredNeighborsList().forEach((pN -> {
            Peer.getInstance().outgoingConnections.forEach((outgoingConnection -> {
                if (outgoingConnection.getDestination_peer_id() == pN) {
                    if (Peer.getInstance().getPreferredNeighborsList().contains(pN)) {
                        outgoingConnection.sendUnChokeMessages();
                    }else if(!Peer.getInstance().getPreferredNeighborsList().contains(pN))
                        outgoingConnection.sendChokeMessages();
                }
            }));
        }));
    }

    public void cancelJob() {
        this.scheduler.shutdownNow();
    }
}
