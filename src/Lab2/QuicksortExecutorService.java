package Lab2;

import java.util.Random;
import java.util.concurrent.*;

public class QuicksortExecutorService {
    final ExecutorService pool = Executors.newCachedThreadPool();

    QuicksortExecutorService(int[] dataArray) {
        new Quicksort(dataArray,0,dataArray.length - 1).run();
    }

    static void swap(int[] dataArray, int i, int j) {
        //Swap the element at index i in dataArray with element at index j
        int temp = dataArray[i];
        dataArray[i] = dataArray[j];
        dataArray[j] = temp;
    }

    static int partition(int[] dataArray, int low, int high) {
        int pivot = dataArray[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (dataArray[j] < pivot) {
                i++;
                swap(dataArray, i, j);
            }
        }
        swap(dataArray, i + 1, high);
        return i + 1;
    }

    class Quicksort implements Runnable {
        int[] dataArray;
        int low;
        int high;

        Quicksort(int[] dataArray, int low, int high) {
            this.dataArray = dataArray;
            this.low = low;
            this.high = high;
        }

        public void run() {
            Future<?>[] tasks = new Future[2];
            if (low < high) {
                int pivotIndex = partition(dataArray, low, high);
                tasks[0] = pool.submit(new Quicksort(dataArray, low, pivotIndex - 1));
                tasks[1] = pool.submit(new Quicksort(dataArray,pivotIndex + 1, high));
                // Wait for sub arrays to become sorted
                for (Future<?> t : tasks) {
                    try {
                        t.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        int N = 100;

        Random rd = new Random();
        int[] dataArray = new int[N];
        for (int i = 0; i < N; i++) {
            dataArray[i] = rd.nextInt();
        }

        new QuicksortExecutorService(dataArray);

        for (int i : dataArray) {
            System.out.println(i);
        }
    }

}
