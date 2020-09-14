package Lab2;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class QuicksortForkJoin {
    static int N;

    public static void main(int N){
        Random rd=new Random();
        int[] dataArray=new int[N];
        for (int i=0;i<N;i++){
            dataArray[i]=rd.nextInt();
        }
        QuicksortRecursiveAction qs=new QuicksortRecursiveAction(dataArray,0,dataArray.length-1);
        ForkJoinPool pool=new ForkJoinPool();
        pool.invoke(qs);
        pool.shutdown();
        for(int i:dataArray){
            System.out.println(i);
        }

    }
}
