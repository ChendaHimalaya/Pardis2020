import Lab2.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    public static int[] generateRandomList(int N) {
        int[] ret = new int[N];
        Random rd = new Random();
        for (int i = 0; i < N; i++) {
            ret[i] = rd.nextInt();
        }
        return ret;
    }


    public static void main(String[] arg) {
        int[] dataArray = generateRandomList(10000000);
        int[] clone1 = dataArray.clone();
        int[] clone2 = dataArray.clone();
        int[] clone3 = dataArray.clone();
        List<Integer> dataList = Arrays.stream(dataArray).boxed().collect(Collectors.toList());

        long startTime, endTime;

        startTime = System.nanoTime();
        QuicksortSequential.main(clone1);
        endTime = System.nanoTime();
        System.out.println("QuicksortSequential took " + (endTime - startTime) / 1000000 + " ms");
        
        startTime = System.nanoTime();
        QuicksortExecutorService.main(clone2);
        endTime = System.nanoTime();
        System.out.println("QuicksortExecutorService took " + (endTime - startTime) / 1000000 + " ms");

        startTime = System.nanoTime();
        QuicksortForkJoin.main(clone3);
        endTime = System.nanoTime();
        System.out.println("QuicksortForkJoin took " + (endTime - startTime) / 1000000 + " ms");

        /* WARNING QuicksortStream is very slow
        startTime = System.nanoTime();
        dataList = QuicksortStream.quicksort(dataList);
        endTime = System.nanoTime();
        System.out.println("QuicksortStream took " + (endTime - startTime) / 1000000 + " ms");
         */


        if (dataArray.length < 101) {
            System.out.println(Arrays.toString(clone1));
            System.out.println(Arrays.toString(clone2));
            System.out.println(Arrays.toString(clone3));
            System.out.println(Arrays.toString(dataList.toArray()));
        }
    }
}

