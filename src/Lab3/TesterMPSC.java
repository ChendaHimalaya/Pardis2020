package Lab3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TesterMPSC {
    int nThreads;
    final ExecutorService pool;

    final LockFreeSkipListLocalLog<Integer> set;
    int N;
    int range;
    boolean ifUniform;
    List<LogEntry<Integer>> globalLog;
    SimpleQueue<LogEntry<Integer>> queue;

    TesterMPSC(int nThreads, int N, int range, boolean ifUniform) {
        this.nThreads = nThreads;
        pool = Executors.newFixedThreadPool(nThreads);
        globalLog=new ArrayList<LogEntry<Integer>>();
        set = new LockFreeSkipListLocalLog<>();
        this.N = N;
        this.range = range;
        this.ifUniform = ifUniform;
        queue=new SimpleQueue<LogEntry<Integer>>();
    }


    /**
     * @return integer in interval [-range/2, range/2] sampled from uniform distribution
     */
    static int generateUniform(int range) {
        return ThreadLocalRandom.current().nextInt(-range / 2, range / 2 + 1);
    }

    /**
     * @return integer in interval [-range/2, range/2] sampled from gaussian distribution
     * with mean = 0 and std = range/8
     */
    static int generateGaussian(int range) {
        int r;
        while (true) {
            r = (int) Math.round(ThreadLocalRandom.current().nextGaussian() * range / 8);
            if (r >= -range / 2 && r <= range / 2) {
                return r;
            }
        }
    }

    static Integer[] generateOperations(int Size, int addOps, int removeOps, int containsOps) {
        Integer[] ret = new Integer[Size];
        for (int i = 0; i < Size; i++) {
            if (i < addOps) {
                ret[i] = 0;
            } else if (i < addOps + removeOps) {
                ret[i] = 1;

            } else {
                ret[i] = 2;
            }
        }
        List<Integer> list = Arrays.asList(ret);
        Collections.shuffle(list);
        ret = list.toArray(ret);
        return ret;
    }

    class Task implements Runnable {
        private final Integer[] operations;
        //private List<LogEntry<Integer>> log=new ArrayList<LogEntry<Integer>>();
        Task(Integer[] ops) {
            operations = ops;
        }

        @Override
        public void run() {
            System.out.println("Thread with id" + Thread.currentThread().getId());
            int x;
            for (Integer operation : operations) {
                if (ifUniform) x = generateUniform(range);
                else x = generateGaussian(range);
                if (operation == 0) {
                    LogEntry<Integer> eventLog=set.add(x);
                    queue.offer(eventLog);
                    //log.add(eventLog);
                } else if (operation == 1) {
                    LogEntry<Integer> eventLog=set.remove(x);
                    queue.offer(eventLog);
                    //log.add(eventLog);
                } else if (operation == 2) {
                    LogEntry<Integer> eventLog=set.contains(x);
                    queue.offer(eventLog);
                    //log.add(eventLog);
                }
            }
            //globalLog.addAll(log);
            System.out.println("Thread with id:" + Thread.currentThread().getId() + " Has finished");
        }
    }

    public void runThreads(double addPercent, double removePercent, double containsPercent) {
        int addOps = (int) (N * addPercent / nThreads);
        int removeOps = (int) (N * removePercent / nThreads);
        int containsOps = (int) (N * containsPercent / nThreads);
        Integer[][] opsList = new Integer[nThreads][];
        for (int i = 0; i < nThreads; i++) {
            opsList[i] = generateOperations(addOps + removeOps + containsOps, addOps, removeOps, containsOps);
        }
        for (int i = 0; i < nThreads; i++) {
            pool.submit(new Task(opsList[i]));
        }
        Thread consumer=new Thread(new TesterConsumer(queue,globalLog));
        consumer.start();

        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try
        {
            Thread.sleep(10);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        queue.offer(new LogEntry<Integer>(Thread.currentThread().getId(),null,null,false,-1));


        //Collections.sort(globalLog);
//        for (LogEntry<Integer> item:globalLog){
//            System.out.println(item);
//        }
        if (set.verifyLog(globalLog)) {
            System.out.println("Log is sequentially valid");
        }else{
            System.out.println("Log is not sequentially valid");
        }
    }
}