package genetics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class MaxBlockMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException, FileNotFoundException {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Set<Future<String>> set = new HashSet<Future<String>>();
        for (int i = 1; i < 10; ++i){
            for (int k = 1; k < 10; ++k){
                for (int j = 1; j <= 1000; j = j * 10){
                    Callable<String> callable = new MaxBlockStatesGen(0.1* i, 0.1 * k, 0.001 * j);
                    Future<String> future = pool.submit(callable);
                    set.add(future);
                }
            }
        }
//        Callable<String> c = new MaxBlockStatesGen(0.3, 0.8, 0.001);
//        Future<String> f = pool.submit(c);
//        set.add(f);
        pool.shutdown();
        PrintWriter writer = new PrintWriter(new File("results.out"));
        for (Future<String> future : set){
            if (!future.get().equals("fail")){
                writer.println(future.get());
            }
        }
        writer.close();
    }
}
