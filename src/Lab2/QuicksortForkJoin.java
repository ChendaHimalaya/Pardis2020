package Lab2;

import java.util.concurrent.ForkJoinPool;

public class QuicksortForkJoin {
    public static void main(int[] dataArray) {
        QuicksortRecursiveAction qs = new QuicksortRecursiveAction(dataArray, 0, dataArray.length - 1);
        ForkJoinPool pool = new ForkJoinPool(4);
        pool.invoke(qs);
        pool.shutdown();
    }
}
