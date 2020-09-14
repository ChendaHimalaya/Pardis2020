package Lab2;
import java.util.concurrent.*;
import java.util.Random;
public class QuicksortRecursiveAction extends RecursiveAction{
    private int[] dataArray;
    private int low;
    private int high;



    public QuicksortRecursiveAction(int[] dataArray, int low, int high){
        this.dataArray=dataArray;
        this.low=low;
        this.high=high;
    }

    static void swap(int[] dataArray, int i, int j){
        //Swap the element at index i in dataArray with element at index j
        int temp=dataArray[i];
        dataArray[i]=dataArray[j];
        dataArray[j]=temp;
    }

    @Override
    protected void compute(){
        if (low<high){
            int pivot=partition(dataArray,low,high);
            invokeAll(new QuicksortRecursiveAction(dataArray,low,pivot-1),
                      new QuicksortRecursiveAction(dataArray,pivot+1,high));

        }


    }

    static int partition(int[] dataArray, int low,int high ){
        int pivot=dataArray[high];
        int i =low-1;
        for (int j=low;j<high;j++){
            if(dataArray[j]<pivot){
                i++;
                swap(dataArray,i,j);
            }
        }
        swap(dataArray,i+1,high);
        return i+1;

    }

}
