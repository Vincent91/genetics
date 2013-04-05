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
public class HiffOnlyCrossover {

    public static final int SIZE = 8;
    public static final int POPULATION_SIZE = 20;
    public static final double PROBABILITY = 0.2;
    public static final double OPTIMAL = 32;
    public static final int STEPS_MAX = 100000;
    public static final String OUTPUT_FILE = "TwoPointsCrossover";
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
                HiffIndividual parent1 = new HiffIndividual(SIZE);
                HiffIndividual parent2 = new HiffIndividual(SIZE);
                for (int i = 0; i < POPULATION_SIZE; ++i){
                    segmentRight = list.get(i).fitness() + segmentLeft;
                    if (r < segmentRight && r > segmentLeft){
                        parent1 = list.get(i);
                    } else {
                        segmentLeft = segmentRight;
                    }
                }
                segmentLeft = 0;
                r = random.nextDouble() * sum;
                for (int i = 0; i < POPULATION_SIZE; ++i){
                    segmentRight = list.get(i).fitness() + segmentLeft;
                    if (r < segmentRight && r > segmentLeft) {
                        parent2 = list.get(i);
                    } else {
                        segmentLeft = segmentRight;
                    }
                }
                int a = Math.abs(random.nextInt()) % SIZE;
                int b = Math.abs(random.nextInt()) % SIZE;
                if (a > b){
                    int temp = a;
                    a = b;
                    b = a;
                }
                HiffIndividual child1 = new HiffIndividual(parent1);
                HiffIndividual child2 = new HiffIndividual(parent2);
                for (int i = a; i <= b; ++i){
                    int parentOneNode = parent1.getBlock().get(i);
                    int parentTwoNode = parent2.getBlock().get(i);
                    child2.set(i, parentOneNode);
                    child1.set(i, parentTwoNode);
                }
                double minOne = list.get(0).fitness();
                int posOne = 0;
                for (int i = 1; i < POPULATION_SIZE; ++i){
                    if (list.get(i).fitness() < minOne){
                        minOne = list.get(i).fitness();
                        posOne = i;
                    }
                }
                int posTwo;
                double minTwo;
                if (posOne == 0){
                    posTwo = SIZE - 1;
                    minTwo = list.get(SIZE - 1).fitness();
                } else {
                    posTwo = 0;
                    minTwo = list.get(0).fitness();
                }
                for (int i = 0; i < POPULATION_SIZE; ++i) {
                    if (i != posOne && list.get(i).fitness() < minTwo){
                        posTwo = i;
                        minTwo = list.get(i).fitness();
                    }
                }
                list.set(posOne, child1);
                list.set(posTwo, child2);
                for (int i = 0; i < POPULATION_SIZE; ++i){
                    if (list.get(i).fitness() > max){
                        max = list.get(i).fitness();
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
