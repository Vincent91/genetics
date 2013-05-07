package genetics;

import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class Hat {


    private static final MersenneTwisterRNG rng = new MersenneTwisterRNG();

    private static final int POPULATION_SIZE = 20;
    private static final int INDIVIDUAL_SIZE = 32;

    private static final double ALPHA = 0.1;
    private static final double GAMMA = 0.1;
    private static final double BETHA = 0.1;

    private static final int STEPS = 10;

    private static final double OPTIMUM = 192;

    public static void main(String[] args) throws FileNotFoundException {
        Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
        List<Operator> operators = new ArrayList<Operator>();
        operators.add(new CrossoverOperator(new Population(p), rng));
        operators.add(new HybridOperator(new Population(p), rng));
        operators.add(new MutationOperator(new Population(p), rng));
        operators.add(new AdvancedMutationOperator(new Population(p), rng));
        operators.add(new MutationOperator(new Population(p), rng));
        operators.add(new MutationOperator(new Population(p), rng));
        operators.add(new MutationOperator(new Population(p), rng));
        operators.add(new MutationOperator(new Population(p), rng));

        for (double alpha = ALPHA; alpha < 1.0; alpha = alpha + 0.1) {
            for (double gamma = GAMMA; gamma < 1.0; gamma = gamma + 0.1) {
                for (double betha = BETHA; betha > 0.0001; betha = betha / 10) {

                    System.out.println(alpha + " " + gamma + " " + betha);

                    double[] Q = new double[operators.size()];
                    double[] P = new double[operators.size()];
                    for (int i = 0; i < operators.size(); ++i){
                        P[i] = 1.0 / operators.size();
                        Q[i] = 0.0;
                    }

                    PrintWriter writer = new PrintWriter(new File("learning " + alpha + " " + gamma + " " + betha +
                            " test.txt"));
                    for (int populations = 0; populations < 101; ++populations){
                        p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                        System.out.println(populations);
                        for (int i = 0; i < operators.size(); ++i){
                            operators.get(i).setPopulation(new Population(p));
                        }
            //            for (int i = 0; i < operators.size(); ++i){
            //                P[i] = 1.0 / operators.size();
            //                Q[i] = 0.0;
            //            }
                        for (int round = 0; round < 10000; ++round){
                            boolean flag = false;
                            for (int i = 0; i < STEPS; ++i) {
                                double selector = rng.nextDouble();
                                double sum = P[0];
                                int selected = 0;
                                while (selected + 1 < operators.size() && sum <= selector) {
                                    sum += P[++selected];
                                }
            //                    System.out.print(selected + " ");
                                operators.get(selected).mutate();
                                double max = Q[0];
                                for (int k = 0; k < operators.size(); ++k){
                                    if (operators.get(k).getPopulation().getFittest() == OPTIMUM){
                                        flag = true;
                                    }
                                    if (Q[k] > max){
                                        max = Q[k];
                                    }
                                }
                                Q[selected] = Q[selected] + alpha * (operators.get(selected).getReward() + gamma * max - Q[selected]);
                                if (flag){
                                    writer.println(round * STEPS + i);
                                    break;
                                }
                            }
                            if (flag){
                                break;
                            }
            //                System.out.println();
                            p = operators.get(0).getPopulation();
                            for (int k = 1; k < operators.size(); ++k){
                                if (operators.get(k).getPopulation().getFittest() > p.getFittest()){
                                    p = operators.get(k).getPopulation();
                                }
                            }
                            for (int k = 0; k < operators.size(); ++k){
                                operators.get(k).setPopulation(new Population(p));
                            }
                            double sum = 0;
                            for (int i = 0; i < operators.size(); ++i){
                                sum += Math.exp(betha * Q[i]);
                            }
                            for (int i = 0; i < operators.size(); ++i){
                                P[i] = Math.exp(betha * Q[i]) / sum;
                            }
//                            System.out.println(Arrays.toString(P));
            //                System.out.println(Arrays.toString(Q));
                            if (round % 1000 == 0){
                                System.out.println("Fitness: " + p.getFittest());
                            }
                //            p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
                //            for (int i = 0; i < operators.size(); ++i){
                //                operators.get(i).setPopulation(new Population(p));
                //            }
                        }
                    }
                    writer.close();
                }
            }
        }
    }
}
