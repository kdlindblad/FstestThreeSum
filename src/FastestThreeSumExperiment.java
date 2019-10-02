import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.util.Arrays;

public class FastestThreeSumExperiment {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 100;
    static int MAXINPUTSIZE  = (int) Math.pow(2,10);
    static int MININPUTSIZE  =  1;
    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/home/karson/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;


    public static void main(String[] args) {

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("FastestThreeSum-Exp1-ThrowAway.txt");
        runFullExperiment("FastestThreeSum-Exp2.txt");
        runFullExperiment("FastestThreeSum-Exp3.txt");
    }

    static void runFullExperiment(String resultsFileName){

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=2) {
            // progress message...
            System.out.println("Running test for input size "+inputSize+" ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;
            // generate a list of randomly spaced integers in ascending sorted order to use as test input
            // In this case we're generating one list to use for the entire set of trials (of a given input size)
            // but we will randomly generate the search key for each trial
            System.out.print("    Generating test data...");
            long[] testList = createSortedntegerList(inputSize);
            System.out.println("...done.");
            System.out.print("    Running trial batch...");

            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();


            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            BatchStopwatch.start(); // comment this line if timing trials individually

            // run the tirals
            for (long trial = 0; trial < numberOfTrials; trial++) {
                // generate a random key to search in the range of a the min/max numbers in the list
                long testSearchKey = (long) (0 + Math.random() * (testList[testList.length-1]));
                /* force garbage collection before each trial run so it is not included in the time */
                // System.gc();

                //TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                /* run the function we're testing on the trial input */

                long foundIndex = FastestThreeSum(testList);

                // batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }
            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    /* return index of the searched number if found, or -1 if not found */

    public static long[] createSortedntegerList(int size) {
        long[] newList = new long[size];
        newList[0] = (long) (10 * Math.random());
        for (int j = 1; j < size; j++) {
            newList[j] = newList[j - 1] + (long) (10 * Math.random());
            //if(j%100000 == 0) {resultsWriter.printf("%d  %d <<<\n",j,newList[j]);  resultsWriter.flush();}
        }
        return newList;
    }

    public static int FastestThreeSum(long[] a)
    {
        // Count triples that sum to 0.
        Arrays.sort(a);
        int N = a.length;
        int cnt = 0;

        for(int i = 0; i < N ; i++){
            int j = i + 1;
            int k = N-1;

            if(i > 0 && a[i] == a[i-1]){
                continue;
            }

            while(j < k){
                if( k < N - 1 && a[k] == a[k+1]){
                    k--;
                    continue;
                }

                if( a[i] + a[j] + a[k] > 0){
                    k--;
                } else if( a[i] + a[j] + a[k] < 0){
                    j++;
                } else {
                    cnt++;
                    j++;
                    k--;
                }
            }
        }
        return cnt;
    }

    public static int binarySearch(long key, long[] list) {
        int i = 0;
        int j= list.length-1;
        if (list[i] == key) return i;
        if (list[j] == key) return j;
        int k = (i+j)/2;
        while(j-i > 1){
            //resultsWriter.printf("%d %d %d %d %d %d\n",i,k,j, list[0], key, list[list.length-1]); resultsWriter.flush();
            if (list[k]== key) return k;
            else if (list[k] < key) i=k;
            else j=k;
            k=(i+j)/2;
        }
        return -1;
    }
}