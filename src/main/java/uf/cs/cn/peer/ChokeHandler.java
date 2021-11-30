package uf.cs.cn.peer;

import uf.cs.cn.utils.CommonConfigFileReader;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ChokeHandler extends Thread {

    private static ChokeHandler chokeHandler;
    private final ScheduledExecutorService scheduler;

    public static ChokeHandler getInstance() {
        if(chokeHandler == null) {
            chokeHandler = new ChokeHandler();
        }
        return chokeHandler;
    }

    private ChokeHandler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startJob() {
        System.out.println("Start job has been called");
        this.scheduler.scheduleAtFixedRate(this, 10, CommonConfigFileReader.un_chocking_interval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        System.out.println("Calculating preferred neighbours");
        Peer.getInstance().calculatePreferredNeighbours();
        System.out.println("Resetting download counters");
        Peer.getInstance().resetDownloadCounters();

        // Sending un choke messages
        // TODO: Need some documentation for this logic.
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

    public static void cancelJob() {
        getInstance().scheduler.shutdownNow();
    }
}
