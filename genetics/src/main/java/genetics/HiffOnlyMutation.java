package genetics;

import genetics.hiff.HiffIndividual;
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
public class HiffOnlyMutation {

    public static final int SIZE = 8;
    public static final int POPULATION_SIZE = 20;
    public static final double PROBABILITY = 0.2;
    public static final double OPTIMAL = 32;
    public static final int STEPS_MAX = 100000;
    public static final String OUTPUT_FILE = "mutation";
    public static int TRIES = 50;

    public static void main(String[] args) throws FileNotFoundException {
        MersenneTwisterRNG random = new MersenneTwisterRNG();
        PrintWriter writer = new PrintWriter(new File(OUTPUT_FILE));
        for (int z = 0; z < TRIES; ++z) {
            System.out.println("#" + z);
            ArrayList<HiffIndividual> list = new ArrayList<HiffIndividual>();
            for (int i = 0; i < POPULATION_SIZE; ++i) {
                list.add(new HiffIndividual(SIZE));
            }
            System.out.println("initial population");
            double max = 0;
            for (int i = 0; i < POPULATION_SIZE; ++i) {
                double temp = list.get(i).fitness();
                if (temp > max) {
                    max = temp;
                }
                System.out.println(list.get(i) + " " + temp);
            }
            int step = 0;
            double sum = 0;
            double r;
            double segmentLeft;
            double segmentRight;
            while (max < OPTIMAL && step < STEPS_MAX) {
                step++;
                for (int i = 0; i < POPULATION_SIZE; ++i) {
                    sum += list.get(i).fitness();
                }
                r = random.nextDouble() * sum;
                segmentLeft = 0;
                for (int i = 0; i < POPULATION_SIZE; ++i) {
                    segmentRight = list.get(i).fitness() + segmentLeft;
                    if (r < segmentRight && r > segmentLeft) {
                        HiffIndividual mutant = new HiffIndividual(list.get(i));
                        //for (int k = 0; k < SIZE; ++k){
                        //    r = random.nextDouble();
                        //    if (r < PROBABILITY){
                        //        mutant.inverse(k);
                        //    }
                        //}
                        r = random.nextDouble();
                        if (r > PROBABILITY) {
                            break;
                        }
                        r = Math.abs(random.nextInt()) % SIZE;
                        mutant.inverse((int) r);
                        int pos = -1;
                        double minFitness = 0;
                        for (int k = 0; k < POPULATION_SIZE; ++k) {
                            double curFitness = list.get(k).fitness();
                            if (pos == -1) {
                                if (mutant.fitness() > curFitness) {
                                    pos = k;
                                    minFitness = curFitness;
                                }
                            } else {
                                if (curFitness < minFitness) {
                                    minFitness = curFitness;
                                    pos = k;
                                }
                            }
                        }
                        if (pos != -1) {
                            list.set(pos, mutant);
                        }
                        for (int k = 0; k < POPULATION_SIZE; ++k) {
                            double curFitness = list.get(k).fitness();
                            if (curFitness > max) {
                                max = curFitness;
                            }
                        }
                        break;
                    } else {
                        segmentLeft = segmentRight;
                    }
                }
                System.out.println(step + " " + max);
            }
            writer.println(step + " " + max);
            writer.flush();
            System.out.println("-----------------------------------------------------------------");
        }
        writer.close();
    }
}
