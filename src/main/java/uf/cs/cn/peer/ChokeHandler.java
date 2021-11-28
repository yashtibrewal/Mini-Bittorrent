package uf.cs.cn.peer;

import uf.cs.cn.utils.CommonConfigFileReader;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChokeHandler extends Thread{
    private ScheduledFuture<?> job;
    private ScheduledExecutorService scheduler;

    public void startJob() {
        this.job = this.scheduler.scheduleAtFixedRate(this, 10, CommonConfigFileReader.un_chocking_interval, TimeUnit.SECONDS);
    }

    @Override
    public void run() {

    }
}
