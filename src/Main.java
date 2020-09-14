import Lab2.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class Main {
    public static List<Integer> generateRandomList(int N){
        List<Integer> ret=new ArrayList<>();
        Random rd=new Random();
        for (int i=0;i<N;i++){
            ret.add(rd.nextInt());
        }
        return ret;
    }


    public static void main(String[] arg){
        //QuicksortSequential.main(100);
        //QuicksortForkJoin.main(200);
        List<Integer> dataArray=generateRandomList(1000);
        dataArray=QuicksortStream.quicksort(dataArray);
        for(int i:dataArray){
            System.out.println(i);
        }

    }
}

