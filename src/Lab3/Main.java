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

    static long calculate_avg(long [] list){
        long sum=0;
        for (int i=0;i<list.length;i++){
            sum=sum+list[i];
        }
        return sum/list.length;
    }

    public static void main(String[] args) {

//        int N = 10000000;
//        long []result;
//        int range=10000000;
//        int [] numThreads=new int[]{2,12,30,46};
//        for (int numThread:numThreads) {
//            result=new long[10];
//            for (int i = 0; i < 10; i++) {
//                Tester tester = new Tester(N, range, true);
//                result[i] = tester.testTask3(numThread, 1000000, 0.5, 0.5, 0);
//            }
//            System.out.println("Number of Threads "+numThread+" with avg time"+calculate_avg(result));
//        }

        int N = 1000;
        TesterGlobalLock tester = new TesterGlobalLock(2, N, 100000, true);
        tester.runThreads(0.5, 0.25, 0.25);

    }
}
