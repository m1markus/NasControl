package ch.m1m.nas;

public interface DriverInterface {

    enum NasStatus {SUCCESS, ERROR, UNKNOWN}

    NasStatus getStatus();

    String getVersion();

    void shutdown();
}
