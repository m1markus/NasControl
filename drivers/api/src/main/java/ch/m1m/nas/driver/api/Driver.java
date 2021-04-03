package ch.m1m.nas.driver.api;

public interface Driver {

    enum NasStatus {SUCCESS, ERROR, UNKNOWN}

    NasStatus getStatus();

    String getVersion();

    void shutdown();
}
