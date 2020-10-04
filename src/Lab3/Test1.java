package Lab3;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Task 2
 */
public class Test1 {
    int nThreads = 2;
    ExecutorService pool = Executors.newFixedThreadPool(nThreads);

    /**
     * populate a with ints in interval [-range/2, range/2] from uniform distribution
     */
    void generateUniform(int[] a, int range) {
        for (int i = 0; i < a.length; i++) {
            a[i] = ThreadLocalRandom.current().nextInt(-range / 2, range / 2 + 1);
        }
    }

    /**
     * populate a with ints in interval [-range/2, range/2] from gaussian distribution with mean = 0 and std = range/2
     */
    void generateGaussian(int[] a, int range) {
        double r;
        int std = range / 2;
        for (int i = 0; i < a.length; i++) {
            while (true) {
                r = ThreadLocalRandom.current().nextGaussian() * std;
                if (r >= -std && r <= std) {
                    a[i] = (int) r;
                    break;
                }
            }
        }
    }

    boolean verifyDistribution(int[] a, int mean, int var) {
        // TODO
        /*
        double avg = Arrays.stream(a).parallel().sum() / (double) a.length;
        if ((int) avg != mean) {
            System.out.println(avg);
            return false;
        }
        double v = Arrays.stream(a).parallel().reduce(0, (x, y) -> x + Math.pow(mean - y, 2)) / (double) a.length;
        */
        return false;
    }

    Test1(int N) {
        int[] uniform = new int[N];
        int[] gaussian = new int[N];
        int range = 1000;

        generateUniform(uniform, range);
        generateGaussian(gaussian, range);

        System.out.println(Arrays.toString(uniform));
        System.out.println(Arrays.toString(gaussian));


        /*
        int N = 10000;
        int[] uniform = new int[N];


        LockFreeSkipList<Integer> set = new LockFreeSkipList<>();

        Runnable t1 = () -> {
            for (int i = 0; i < N/2; i++) {
                set.add(i);
            }
        };
        Runnable t2 = () -> {
            for (int i = N/2; i < N; i++) {
                set.add(i);
            }
        };

        pool.submit(t1);
        pool.submit(t2);
        pool.shutdown();
        try {
            pool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }
}
