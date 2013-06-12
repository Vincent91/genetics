package genetics.tsp;

//import genetics.MaxBlockStatesGen;
import genetics.tsp.Store;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        Set<Future<String>> set = new HashSet<Future<String>>();
//        for (int i = 1; i < 10; ++i){
//            for (int k = 1; k < 10; ++k){
//                for (int j = 1; j <= 1; j = j * 10){
//                    for (int z = 1; z <= 1; z = z * 10){
//                        Callable<String> callable = new CounterGreedy(0.1* i, 0.1 * k, 0.1 * j, z, true);
//                        Future<String> future = pool.submit(callable);
//                        set.add(future);
//                    }
//                }
//            }
//        }
        Callable<String> callable = new CounterGreedy(0.1, 0.9, 100, 10, true);
        Future<String> future = pool.submit(callable);
        pool.shutdown();
//        PrintWriter writer = new PrintWriter(new File("TSPGreedy.out"));
//        for (Future<String> future : set){
//            if (!future.get().equals("fail")){
//                writer.println(future.get());
//            }
//        }
//        writer.close();
        /*pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        set = new HashSet<Future<String>>();
        for (int i = 1; i < 10; ++i){
            for (int k = 1; k < 10; ++k){
                for (int j = 1; j <= 1000; j = j * 10){
                    for (int z = 1; z <= 100; z = z * 10){
                        Callable<String> callable = new CounterGreedy(0.1* i, 0.1 * k, 0.1 * j, z, false);
                        Future<String> future = pool.submit(callable);
                        set.add(future);
                    }
                }
            }
        }
        pool.shutdown();
        PrintWriter writerB = new PrintWriter(new File("TSPBoltzmann.out"));
        for (Future<String> future : set){
            if (!future.equals("fail")){
                writerB.println(future.get());
            }
        }
        writerB.close();  */
    }
}
