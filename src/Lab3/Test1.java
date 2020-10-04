package Lab3;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Task 2
 */
public class Test1 {

    /**
     * populate set with N integers in interval [-range/2, range/2] from uniform distribution
     */
    void generateUniform(LockFreeSkipList<Integer> set, int N, int range) {
        for (int i = 0; i < N; i++) {
            set.add(ThreadLocalRandom.current().nextInt(-range / 2, range / 2 + 1));
        }
    }

    /**
     * populate set with N integers in interval [-range/2, range/2] from gaussian distribution
     * with mean = 0 and std = range/8
     */
    void generateGaussian(LockFreeSkipList<Integer> set, int N, int range) {
        int r;
        for (int i = 0; i < N; i++) {
            while (true) {
                r = (int) Math.round(ThreadLocalRandom.current().nextGaussian() * range / 8);
                if (r >= -range / 2 && r <= range / 2) {
                    set.add(r);
                    break;
                }
            }
        }
    }

    void verifyDistribution(LockFreeSkipList<Integer> set, double mean, double var) {
        int size = 0;
        double meanCalc = 0, varCalc = 0;
        for (int ignored : set)
            size++;
        for (double i : set) {
            meanCalc += i / size;
            varCalc += ((i - mean) * (i - mean)) / size;
        }
        System.out.println("mean: " + mean + " meanCalc: " + meanCalc);
        System.out.println("var: " + var + " varCalc: " + varCalc);
    }

    Test1(int N) {
        LockFreeSkipList<Integer> uniform = new LockFreeSkipList<>();
        LockFreeSkipList<Integer> gaussian = new LockFreeSkipList<>();

        int range = 10000000;
        generateUniform(uniform, N, range);
        generateGaussian(gaussian, N, range);

        verifyDistribution(uniform, 0.0, (double) range * (range + 2) / 12.0);
        System.out.println();
        verifyDistribution(gaussian, 0.0, (double) range * range / 64.0);
    }
}
