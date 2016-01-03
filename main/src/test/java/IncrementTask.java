import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class IncrementTask extends RecursiveAction {
    private final long[] array;
    private final int low;
    private final int high;

    private static final int THRESHOLD = 250000;
    private static final int pow = 5;

    public IncrementTask(long[] array, int low, int high) {
        super();

        this.array = array;
        this.low = low;
        this.high= high;
    }

    @Override
    protected void compute() {
        if (high - low < THRESHOLD) {
            for (int i = low; i < high; ++i){
                array[i] = ((Double)Math.pow(array[i],pow)).intValue();
            }
        } else {
            int mid = (low + high) >>> 1;
            invokeAll(new IncrementTask(array, low, mid), new IncrementTask(array, mid, high));
        }
    }

    public static void main(String[] args){
        long startFork,doneFork,startOld,doneOld;

        int max = 1_000_000;

        Runtime.getRuntime().gc();

        long[] array = new long[max];
        long[] array_copy = new long[max];

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(cores);

        for(int i=0;i<max;i++){
            array[i] = i+1;
        }


        RecursiveAction mainTask = new IncrementTask (array, 0, array.length);
        ForkJoinPool mainPool = new ForkJoinPool();
        startFork = System.currentTimeMillis();
        mainPool.invoke(mainTask);
        doneFork = System.currentTimeMillis();
        System.out.println("Time for fork thread -> " + (doneFork - startFork) + " ms");

        for(int i=0;i<max;i++){
            array_copy[i] = i+2;
        }

        startOld = System.currentTimeMillis();

        long val;
        Integer[] integers = new Integer[array_copy.length];
        for (int i=0;i<array_copy.length;i++) {
            val = array_copy[i];
            integers[i] = ((Double)Math.pow(val,pow)).intValue();
        }
        doneOld = System.currentTimeMillis();
        System.out.println("Time for single thread -> "+ (doneOld - startOld) + " ms");
    }
}