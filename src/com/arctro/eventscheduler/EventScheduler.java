package com.arctro.eventscheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.arctro.eventscheduler.*;

/**
 * Created by BenM on 3/03/15.
 */
public class EventScheduler{
    String path="";
    int checkDuration=10000;
    boolean running=true;

    public List<ScheduledEventWrapper> lastCheck = new ArrayList<ScheduledEventWrapper>();

    public EventScheduler(String path){
        this.path=path;
        final String pathLocal=path;
        Thread t = new Thread(){
            public void run(){
                while(running){
                    List<ScheduledEventWrapper> temp = getEvents(pathLocal);
                    if(temp!=null){
                        lastCheck=temp;
                    }
                    try {
                        Thread.sleep(checkDuration);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    //Save all events
    public void saveEvents(String path,List<ScheduledEventWrapper> content){
        File file = new File(path);
        PrintWriter writer;
        try {
            file.createNewFile();
            writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try{
            fout = new FileOutputStream(path, true);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(oos != null){
                try{
                    oos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveEvents(){
        saveEvents(path,lastCheck);
    }

    //Return all events
    public List<ScheduledEventWrapper> getEvents(String path){
        FileInputStream fis;
        ObjectInputStream objectinputstream = null;
        List<ScheduledEventWrapper> returnArray = new ArrayList<ScheduledEventWrapper>();
        try {
            fis = new FileInputStream(path);
            objectinputstream = new ObjectInputStream(fis);
            returnArray = (ArrayList<ScheduledEventWrapper>) objectinputstream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(objectinputstream != null){
                try{
                    objectinputstream.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return returnArray;
    }

    public List<ScheduledEventWrapper> getEvents(){
        return getEvents(path);
    }

    //Return all events before time
    public List<ScheduledEventWrapper> getEventsBefore(int unix){
        List<ScheduledEventWrapper> returnList=new ArrayList<ScheduledEventWrapper>();
        for(int i=0;i<lastCheck.size();i++){
            if(lastCheck.get(i).se.getRuntime()<unix){
                returnList.add(lastCheck.get(i));
            }
        }
        return returnList;
    }
    public List<ScheduledEventWrapper> getEventsBefore(int unix, boolean refresh){
        if(refresh){
            lastCheck=getEvents(path);
            return getEventsBefore(unix);
        }else{
            return getEventsBefore(unix);
        }
    }

    //Return all events after time
    public List<ScheduledEventWrapper> getEventsAfter(int unix){
        List<ScheduledEventWrapper> returnList=lastCheck;
        for(int i=0;i<returnList.size();i++){
            if(returnList.get(i).se.getRuntime()>unix){
                returnList.add(returnList.get(i));
            }
        }
        return returnList;
    }
    public List<ScheduledEventWrapper> getEventsAfter(int unix, boolean refresh){
        if(refresh){
            lastCheck=getEvents(path);
            return getEventsAfter(unix);
        }else{
            return getEventsAfter(unix);
        }
    }

    //Return all events between $lower and $upper
    public List<ScheduledEventWrapper> getEventsBetween(int lower, int upper){
        List<ScheduledEventWrapper> returnList=lastCheck;
        for(int i=0;i<returnList.size();i++){
            if(returnList.get(i).se.getRuntime()>lower&&returnList.get(i).se.getRuntime()<upper){
                returnList.add(returnList.get(i));
            }
        }
        return returnList;
    }
    public List<ScheduledEventWrapper> getEventsBetween(int lower, int upper, boolean refresh){
        if(refresh){
            lastCheck=getEvents(path);
            return getEventsBetween(lower, upper);
        }else{
            return getEventsBetween(lower, upper);
        }
    }

    public List<ScheduledEventWrapper> getEventsByTime(List<ScheduledEventWrapper> data, boolean sortNewest){
        int lenD = data.size();
        ScheduledEventWrapper tmp = null;
        for(int i = 0;i<lenD;i++){
            for(int j = (lenD-1);j>=(i+1);j--){
                if(data.get(j).runtime<data.get(j-1).runtime){
                    tmp = data.get(j);
                    data.set(j,data.get(j-1));
                    data.set(j-1,tmp);
                }
            }
        }
        return data;
    }
    public List<ScheduledEventWrapper> getEventsByTime(boolean sortNewest){
        return getEventsByTime(getEvents(),sortNewest);
    }

    public ScheduledEventWrapper getEventAtPosition(int position){
        if(position<lastCheck.size()){
            return lastCheck.get(position);
        }
        return null;
    }

    public void addEvent(ScheduledEventWrapper se){
        List<ScheduledEventWrapper> returnList=lastCheck;
        returnList.add(se);
        saveEvents(path,returnList);
    }
    public void addEvent(ScheduledEventWrapper se, boolean refresh){
        if(refresh){
            lastCheck=getEvents();
            addEvent(se);
        }else{
            addEvent(se);
        }
    }

    public static String getExtra(String search, ScheduledEvent se){
        for(int i=0;i<se.getExtra().length;i++){
            if(se.getExtra()[i][0].equals(search)){
                return se.getExtra()[i][1];
            }
        }
        return "";
    }
    public static String getExtra(String search, ScheduledEventWrapper sew){
        for(int i=0;i<sew.se.getExtra().length;i++){
            if(sew.se.getExtra()[i][0].equals(search)){
                return sew.se.getExtra()[i][1];
            }
        }
        return "";
    }

    public int getPosition(String uniqid){
        List<ScheduledEventWrapper> list=lastCheck;
        for(int i=0;i<list.size();i++){
            if(list.get(i).se.getUniqid().equals(uniqid)){
                return i;
            }
        }
        return -1;
    }
    public ScheduledEventWrapper getEventFromUniqid(String uniqid){
        List<ScheduledEventWrapper> list=lastCheck;
        for(int i=0;i<list.size();i++){
            if(list.get(i).se.getUniqid().equals(uniqid)){
                return list.get(i);
            }
        }
        return null;
    }

    public void removeEvent(int index){
        List<ScheduledEventWrapper> returnList=lastCheck;
        returnList.remove(index);
        saveEvents(path,returnList);
    }
    public void removeEvent(String uniqid){
        List<ScheduledEventWrapper> returnList=lastCheck;
        for(int i=0;i<returnList.size();i++){
            if(returnList.get(i).se.getUniqid().equals(uniqid)){
                returnList.remove(i);
            }
        }
        saveEvents(path,returnList);
    }
    public void removeAll(){
        List<ScheduledEventWrapper> returnList=lastCheck;
        returnList.removeAll(null);
        saveEvents(path,returnList);
    }

    public void setCheckDuration(int duration){
        checkDuration=duration;
    }

    public void setRunning(boolean running){
        this.running=running;
    }
    public void setPath(String path){
        this.path = path;
    }
}
