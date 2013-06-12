package genetics.tsp;

import org.uncommons.maths.random.MersenneTwisterRNG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class GraphGenerator {

    private static int SIZE = 80;
    private static MersenneTwisterRNG rng = new MersenneTwisterRNG();

    public static void main(String[] args){
        try {
            PrintWriter writer = new PrintWriter(new File("graph.txt"));
            for (int i = 0; i < SIZE; ++i){
                for (int k = 0; k < SIZE; ++k){
                    int l = rng.nextInt(100);
                    writer.print(l + " ");
                }
            }
            writer.println();
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("An error ocured while writing data " + e.toString());
        }
    }

}
