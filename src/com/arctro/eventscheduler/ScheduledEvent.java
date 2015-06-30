package com.arctro.eventscheduler;

import java.io.Serializable;

/**
 * Created by BenM on 3/03/15.
 */
public abstract class ScheduledEvent implements Serializable{
    public abstract int getRuntime();
    public abstract String getUniqid();
    public abstract String[][] getExtra();
    public abstract void run();
}
