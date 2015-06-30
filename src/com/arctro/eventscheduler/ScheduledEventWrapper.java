package com.arctro.eventscheduler;

import java.io.Serializable;

/**
 * Created by BenM on 12/03/15.
 */
public class ScheduledEventWrapper implements Serializable{
    public int runtime=0;
    public String uniqid = "";
    public String[][] extra = null;

    public ScheduledEvent se = new ScheduledEvent() {
        public int getRuntime() {
            return runtime;
        }

        public String getUniqid() {
            return uniqid;
        }

        public String[][] getExtra() {
            return extra;
        }
        public void run(){

        }
    };
}
