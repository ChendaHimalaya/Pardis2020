package Lab2;

import java.util.concurrent.*;

public class QuicksortExecutorService {
    final int n = Runtime.getRuntime().availableProcessors();
    final ExecutorService pool = Executors.newCachedThreadPool();
    int poolSize = 0;

    QuicksortExecutorService(int[] dataArray) {
        new Quicksort(dataArray, 0, dataArray.length - 1).run();
        pool.shutdown();
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
                if (poolSize < n && high - low > 1000000) {
                    tasks[0] = pool.submit(new Quicksort(dataArray, low, pivotIndex - 1));
                    tasks[1] = pool.submit(new Quicksort(dataArray, pivotIndex + 1, high));
                    poolSize++;
                    // Wait for sub arrays to become sorted
                    for (Future<?> t : tasks) {
                        try {
                            t.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    poolSize--;
                } else {
                    QuicksortSequential.quickSort(dataArray, low, pivotIndex - 1);
                    QuicksortSequential.quickSort(dataArray, pivotIndex + 1, high);
                }
            }
        }
    }

    public static void main(int[] dataArray) {
        new QuicksortExecutorService(dataArray);
    }

}
