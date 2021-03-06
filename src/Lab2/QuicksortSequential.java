package Lab2;

public class QuicksortSequential {
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

    public static void quickSort(int[] dataArray, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(dataArray, low, high);
            quickSort(dataArray, low, pivotIndex - 1);
            quickSort(dataArray, pivotIndex + 1, high);
        }
    }

    public static void main(int[] dataArray) {
        quickSort(dataArray, 0, dataArray.length - 1);
    }
}
