package com.upwork.dsavitski.crawler.services;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component("progressBar")
public class ProgressBar {
    private int initCount = 0;
    private volatile AtomicInteger currentCount;

    public ProgressBar() {
    }

    public int getCurrentPercent() {
        if (currentCount == null) return 0;
        final int percent = (int) (((initCount - currentCount.get()) / (double) initCount) * 100);
        return percent;
    }

    public void setInitCount(int initCount) {
        this.initCount = initCount;
        currentCount = new AtomicInteger(initCount);
    }

    public void decrement() {
        currentCount.decrementAndGet();
    }
}
