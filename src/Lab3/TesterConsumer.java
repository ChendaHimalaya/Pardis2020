package Lab3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TesterConsumer implements Runnable {
    SimpleQueue<LogEntry<Integer>> logs;
    List<LogEntry<Integer>> globalLog;
    TesterConsumer(SimpleQueue<LogEntry<Integer>> log,List<LogEntry<Integer>> globalLog){
        this.logs=log;
        this.globalLog=globalLog;
    }

    private void insert(List<LogEntry<Integer>> list, LogEntry<Integer> item){
        int index=Collections.binarySearch(list,item);
        if (index<0){
            index=-index-1;
        }
        list.add(index,item);
    }
    public void run(){
        System.out.println("Consumer with ID:"+Thread.currentThread().getId()+" Has started");
        while(true){
            LogEntry<Integer> item=logs.poll();
            //System.out.println(item);
            if (item!=null){
                if(item.timeStamp==-1){
                    break;
            }else {
                    insert(globalLog, item);
                }

            }
        }
        System.out.println("Consumer Thread Finished work");

    }



}
