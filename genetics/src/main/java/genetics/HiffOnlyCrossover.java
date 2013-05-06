package genetics;

import genetics.hiff.CrossoverOperator;
import genetics.hiff.HiffIndividual;
import genetics.hiff.Population;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Влад
 * Date: 05.04.13
 * Time: 1:32
 * To change this template use File | Settings | File Templates.
 */
public class HiffOnlyCrossover {

    public static final int INDIVIDUAL_SIZE = 32;
    public static final int POPULATION_SIZE = 20;
    public static final int STEPS_MAX = 100000;
    public static final String OUTPUT_FILE = "TwoPointsCrossover.txt";
    public static int ROUNDS = 101;
    public static double OPTIMUM = 192;

    public static void main(String[] args) throws FileNotFoundException {
        MersenneTwisterRNG random = new MersenneTwisterRNG();
        PrintWriter writer = new PrintWriter(new File(OUTPUT_FILE));
        Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
        CrossoverOperator operator = new CrossoverOperator(p, random);
        for (int i = 0; i < ROUNDS; ++i){
            int k = 1;
            for (; k <= STEPS_MAX; ++k){
                double fitness = p.getFittest();
                if (fitness == OPTIMUM){
                    break;
                }
                operator.mutate();
            }
            System.out.print(i + " ");
            writer.println(k);
            p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
            operator.setPopulation(p);
        }
        writer.close();
    }
}
