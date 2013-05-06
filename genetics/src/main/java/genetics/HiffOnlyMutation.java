package genetics;

import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Влад
 * Date: 05.04.13
 * Time: 1:32
 * To change this template use File | Settings | File Templates.
 */
public class HiffOnlyMutation {

    public static final int INDIVIDUAL_SIZE = 32;
    public static final int POPULATION_SIZE = 20;
    public static final int STEPS_MAX = 100000;
    public static final String OUTPUT_FILE_1 = "HybridCrossover.txt";
    public static final String OUTPUT_FILE_2 = "Mutation.txt";
    public static final String OUTPUT_FILE_3 = "AdvancedMutation.txt";
    public static final String OUTPUT_FILE_4 = "RandomTrick.txt";
    public static int ROUNDS = 101;
    public static double OPTIMUM = 192;


    public static void main(String[] args) throws FileNotFoundException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter writer = null;
                try {
                    MersenneTwisterRNG rng = new MersenneTwisterRNG();
                    writer = new PrintWriter(new File(OUTPUT_FILE_1));
                    Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                    HybridOperator operator = new HybridOperator(p, rng);
                    for (int i = 0; i < ROUNDS; ++i) {
                        int k = 1;
                        for (; k <= STEPS_MAX; ++k) {
                            double fitness = p.getFittest();
                            if (fitness == OPTIMUM) {
                                break;
                            }
                            operator.mutate();
                        }
                        System.out.println(i);
                        writer.println(k);
                        p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                        operator.setPopulation(p);
                    }
                    writer.close();
                } catch (IOException e) {
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter writer = null;
                try {
                    MersenneTwisterRNG rng = new MersenneTwisterRNG();
                    writer = new PrintWriter(new File(OUTPUT_FILE_2));
                    Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                    MutationOperator operator = new MutationOperator(p, rng);
                    for (int i = 0; i < ROUNDS; ++i) {
                        int k = 1;
                        for (; k <= STEPS_MAX; ++k) {
                            double fitness = p.getFittest();
                            if (fitness == OPTIMUM) {
                                break;
                            }
                            operator.mutate();
                        }
                        System.out.println(i);
                        writer.println(k);
                        p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                        operator.setPopulation(p);
                    }
                    writer.close();
                } catch (IOException e) {
                }
            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter writer = null;
                try {
                    MersenneTwisterRNG rng = new MersenneTwisterRNG();
                    writer = new PrintWriter(new File(OUTPUT_FILE_3));
                    Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                    AdvancedMutationOperator operator = new AdvancedMutationOperator(p, rng);
                    for (int i = 0; i < ROUNDS; ++i) {
                        int k = 1;
                        for (; k <= STEPS_MAX; ++k) {
                            double fitness = p.getFittest();
                            if (fitness == OPTIMUM) {
                                break;
                            }
                            operator.mutate();
                        }
                        System.out.println(i);
                        writer.println(k);
                        p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                        operator.setPopulation(p);
                    }
                    writer.close();
                } catch (IOException e) {
                }
            }
        });
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter writer = null;
                try {
                    MersenneTwisterRNG rng = new MersenneTwisterRNG();
                    writer = new PrintWriter(new File(OUTPUT_FILE_4));
                    Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                    Operator[] operators = new Operator[8];
                    operators[0] = new CrossoverOperator(p, rng);
                    operators[1] = new HybridOperator(p, rng);
                    operators[2] = new MutationOperator(p, rng);
                    operators[3] = new AdvancedMutationOperator(p, rng);
                    operators[4] = new MutationOperator(p, rng);
                    operators[5] = new MutationOperator(p, rng);
                    operators[6] = new MutationOperator(p, rng);
                    operators[7] = new MutationOperator(p, rng);
                    double[] score = new double[101];
                    for (int i = 0; i < ROUNDS; ++i) {
                        int k = 1;
                        for (; k <= STEPS_MAX; ++k) {
                            double max = operators[0].getPopulation().getFittest();
                            if (max == OPTIMUM) {
                                break;
                            }
                            double prob = rng.nextDouble();
                            if (prob < 0.125){
                                operators[0].mutate();
                            } else if (prob < 0.25){
                                operators[1].mutate();
                            } else if (prob < 0.375){
                                operators[2].mutate();
                            } else if (prob < 0.5){
                                operators[3].mutate();
                            } else if (prob < 0.625){
                                operators[4].mutate();
                            } else if (prob < 0.75){
                                operators[5].mutate();
                            } else if (prob < 0.875){
                                operators[6].mutate();
                            } else {
                                operators[7].mutate();
                            }

                        }
                        System.out.println(i);
                        writer.println(k);
                        score[i] = k;
                        p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                        for (int z = 0; z < operators.length; ++z){
                            operators[z].setPopulation(p);
                        }
                    }
                    writer.close();
                    Arrays.sort(score);
                    System.out.println("51 " + score[50]);
                } catch (IOException e) {
                }
            }
        });
//        t3.start();
        t4.start();
    }
}
