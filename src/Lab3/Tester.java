package Lab3;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tester{
    private static LockFreeSkipList<Integer> list=new LockFreeSkipList<>();

    static int N;
    static int range;


    /**
     * @return integer in interval [-range/2, range/2] sampled from uniform distribution
     */
    static int generateUniform(int range) {
        return ThreadLocalRandom.current().nextInt(-range / 2, range / 2 + 1);
    }

    /**
     * @return integer in interval [-range/2, range/2] sampled from gaussian distribution
     * with mean = 0 and std = range/8
     */
    static int generateGaussian(int range) {
        int r;
        while (true) {
            r = (int) Math.round(ThreadLocalRandom.current().nextGaussian() * range / 8);
            if (r >= -range / 2 && r <= range / 2) {
                return r;
            }
        }
    }

    public Tester(int N, int range,boolean ifUniform){
//        if (ifUniform){
//            this.generateUniform(list,N,range);
//
//        }else{
//            this.generateGaussian(list,N,range);
//        }

        this.N=N;
        this.range=range;


    }

    public static Integer[] generateOperations(int Size, int addOps,int removeOps, int containsOps){
        Integer[] ret=new Integer[Size];
        for (int i=0;i<Size;i++){
            if (i<addOps){
                ret[i]=0;
            }else if(i<addOps+removeOps){
                ret[i]=1;

            }else{
                ret[i]=2;
            }
        }
        List<Integer> list=Arrays.asList(ret);
        Collections.shuffle(list);
        ret=list.toArray(ret);
        return ret;

    }

    class myThreads implements Runnable{
        private Integer[] operations;
        public myThreads(Integer[] ops){
            operations=ops;

        }
        @Override
        public void run() {
            System.out.println("Thread with id"+Thread.currentThread().getId());
            testThread(operations);
        }
    }

    public long testTask3(int numThreads,int numOps, double addPercent, double removePercent, double containsPercent){
        Thread[] threads=new Thread[numThreads];
        int addOps=(int) (numOps*addPercent/numThreads);
        int removeOps=(int)(numOps*removePercent/numThreads);
        int containsOps=(int)(numOps*containsPercent/numThreads);
        Integer [][] opsList=new Integer[numThreads][];
        for (int i=0;i<numThreads;i++){
            opsList[i]=generateOperations(addOps+removeOps+containsOps,addOps,removeOps,containsOps);
        }
        long time=System.currentTimeMillis();
        for (int i =0; i<numThreads;i++){

            threads[i]=new Thread(new myThreads(opsList[i]));

            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long time_used=System.currentTimeMillis()-time;
        System.out.println("Time used was"+time_used);
        return time_used;



    }

    public static void testThread(Integer[] operations){
        int l=operations.length;
        for (int i=0; i<l;i++){
            if (operations[i]==0){
                list.add(ThreadLocalRandom.current().nextInt(range));
            }else if(operations[i]==1){
                list.remove(ThreadLocalRandom.current().nextInt(range));
            }else if(operations[i]==2){
                list.contains(ThreadLocalRandom.current().nextInt(range));
            }
        }
        System.out.println("Thread with id:"+Thread.currentThread().getId()+" Has finished");
    }

}