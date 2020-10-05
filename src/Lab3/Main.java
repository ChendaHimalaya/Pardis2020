package Lab3;

import java.util.concurrent.ThreadLocalRandom;

public class Main {

    /**
     * @return 0 with probability 3/4,
     * otherwise int in range 1-31 sampled from geometric distribution with p = 0.5
     */
    static int randomLevel() {
        if (ThreadLocalRandom.current().nextDouble() < 0.75) {
            return 0;
        }
        return Math.min(1 + (int) Math.floor(Math.log(ThreadLocalRandom.current().nextDouble()) / Math.log(0.5)), 31);
    }

    public static void main(String[] args) {
        int N = 1000;
        TesterGlobalLock tester = new TesterGlobalLock(2, N, 100000, true);
        tester.runThreads(0.5, 0.25, 0.25);
    }
}
