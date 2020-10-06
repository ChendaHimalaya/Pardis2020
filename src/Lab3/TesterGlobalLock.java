package Lab3;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TesterGlobalLock {
    int nThreads;
    final ExecutorService pool;

    final LockFreeSkipListGlobalLock<Integer> set;
    int N;
    int range;
    boolean ifUniform;

    TesterGlobalLock(int nThreads, int N, int range, boolean ifUniform) {
        this.nThreads = nThreads;
        pool = Executors.newFixedThreadPool(nThreads);

        set = new LockFreeSkipListGlobalLock<>();
        this.N = N;
        this.range = range;
        this.ifUniform = ifUniform;
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

        Task(Integer[] ops) {
            operations = ops;
        }

        @Override
        public void run() {
            //System.out.println("Thread with id" + Thread.currentThread().getId());
            int x;
            for (Integer operation : operations) {
                if (ifUniform) x = generateUniform(range);
                else x = generateGaussian(range);
                if (operation == 0) {
                    set.add(x);
                } else if (operation == 1) {
                    set.remove(x);
                } else if (operation == 2) {
                    set.contains(x);
                }
            }
            //System.out.println("Thread with id:" + Thread.currentThread().getId() + " Has finished");
        }
    }

    public long runThreads(double addPercent, double removePercent, double containsPercent) {
        int addOps = (int) (N * addPercent / nThreads);
        int removeOps = (int) (N * removePercent / nThreads);
        int containsOps = (int) (N * containsPercent / nThreads);
        Integer[][] opsList = new Integer[nThreads][];
        for (int i = 0; i < nThreads; i++) {
            opsList[i] = generateOperations(addOps + removeOps + containsOps, addOps, removeOps, containsOps);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < nThreads; i++) {
            pool.submit(new Task(opsList[i]));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }
}
