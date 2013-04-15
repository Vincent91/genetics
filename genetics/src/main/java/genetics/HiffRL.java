package genetics;

import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import javax.swing.plaf.metal.MetalTheme;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class HiffRL {

    private static final int POPULATION_SIZE = 60;
    private static final int INDIVIDUAL_SIZE = 16;
    private static final double RATE = 0.5;
    private static final double FACTOR = 0.5;
    private static final double BETHA = 0.05;
    
    private static final int STEPS_NUMBER = 800;
    

    private static final String ONE_STATE_OUT = "oneStateOut.out";
    private static final String TWO_STATES_OUT = "twoStatesOut.out";
    private static final String RANDOM_OUT = "randomOut.out";
    private static final String CROSSOVER_OUT = "crossoverOut.out";
    private static final String MUTATION_OUT = "mutationOut.out";

    private static final MersenneTwisterRNG RNG = new MersenneTwisterRNG();

    public static void main(String[] args) throws FileNotFoundException {

        String dir = new String(RATE + "" + FACTOR + "" + BETHA + "/");
        File a = new File(dir);
        a.mkdir();
        PrintWriter oneStateWriter = new PrintWriter(new File(dir + "" + ONE_STATE_OUT));
        PrintWriter twoStatesWriter = new PrintWriter(new File(dir + "" + TWO_STATES_OUT));
        PrintWriter randomWriter = new PrintWriter(new File(dir + "" + RANDOM_OUT));
        PrintWriter crossoverWriter = new PrintWriter(new File(dir + "" + CROSSOVER_OUT));
        PrintWriter mutationWriter = new PrintWriter(new File(dir + "" + MUTATION_OUT));


        Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
        ArrayList<ArrayList<Double>> Q = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> P = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<State>> states = new ArrayList<ArrayList<State>>();
        State s0t0 = new State(new MutationOperator(p, RNG), 0);
        State s0t1 = new State(new CrossoverOperator(p, RNG), 1);
        State s1t0 = new State(new MutationOperator(p, RNG), 0);
        State s1t1 = new State(new CrossoverOperator(p, RNG), 1);

        ArrayList<ArrayList<Double>> singleQ = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> singleP = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<State>> single = new ArrayList<ArrayList<State>>();
        single.add(new ArrayList<State>());
        single.get(0).add(new State(new MutationOperator(p, RNG), 0));
        single.get(0).add(new State(new CrossoverOperator(p, RNG), 0));
        singleQ.add(new ArrayList<Double>());
        singleP.add(new ArrayList<Double>());
        for (int i = 0; i < 2; ++i){
            singleQ.get(0).add(0.0);
            singleP.get(0).add(0.5);
        }

        MutationOperator mutationOperator = new MutationOperator(p, RNG);
        CrossoverOperator crossoverOperator = new CrossoverOperator(p, RNG);

        CrossoverOperator rndCrsOperator = new CrossoverOperator(p, RNG);
        MutationOperator rndMtnOperator = new MutationOperator(p,RNG);

        for (int i = 0; i < 2; ++i) {
            Q.add(new ArrayList<Double>());
            P.add(new ArrayList<Double>());
            states.add(new ArrayList<State>());
        }
        states.get(0).add(s0t0);
        states.get(0).add(s0t1);
        states.get(1).add(s1t0);
        states.get(1).add(s1t1);
        for (int i = 0; i < 2; ++i){
            for (int k = 0; k < 2; ++k) {
                Q.get(i).add(0.0);
                P.get(i).add(0.5);
            }
        }                                                                                                      
        double r;
        int state = 0;

        double oneResult[] = new double[STEPS_NUMBER];
        double twoResult[] = new double[STEPS_NUMBER];
        double rndResult[] = new double[STEPS_NUMBER];
        double crsResult[] = new double[STEPS_NUMBER];
        double mtnResult[] = new double[STEPS_NUMBER];

        for (int launches = 0; launches < 150; ++launches){
            System.out.println(launches);
            state = Math.abs(RNG.nextInt()) % 2;
            p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
            Population oneStatePopulation = new Population(p);
            Population randomPopulation = new Population(p);
            Population crossoverPopulation = new Population(p);
            Population mutationPopulation = new Population(p);

            for (int i = 0; i < 2; ++i){
                for (int k = 0; k < 2; ++k){
                    states.get(i).get(k).getOperator().setPopulation(p);
                }
                single.get(0).get(i).getOperator().setPopulation(oneStatePopulation);
            }

            mutationOperator.setPopulation(mutationPopulation);
            crossoverOperator.setPopulation(crossoverPopulation);

            rndCrsOperator.setPopulation(randomPopulation);
            rndMtnOperator.setPopulation(randomPopulation);

            for (int i = 0; i < STEPS_NUMBER; ++i){
                r = RNG.nextDouble();
                double sum = 0;
                State toChoose = null;
                int pos = -1;
                for (int k = 0; k < states.get(state).size(); ++k){
                    sum += P.get(state).get(k);
                    toChoose = states.get(state).get(k);
                    pos = k;
                    if (sum >= r){
                        break;
                    }
                }
                toChoose.getOperator().mutate();
                double reward = toChoose.getOperator().getReward();
                double q = Q.get(state).get(pos);
                double max = Q.get(toChoose.getDestination()).get(0);
                for (int k = 0; k < Q.get(toChoose.getDestination()).size(); ++k){
                    if (Q.get(toChoose.getDestination()).get(k) > max){
                        max = Q.get(toChoose.getDestination()).get(k);
                    }
                }
                q = q + RATE * (reward + FACTOR * max - q);
                Q.get(state).set(pos, q);
                state = toChoose.getDestination();
                max = p.getFittest();
                twoResult[i] = twoResult[i] + max;
                r = RNG.nextDouble();
                sum = 0;
                toChoose = null;
                pos = -1;
                for (int k = 0; k < single.get(0).size(); ++k){
                    sum += singleP.get(0).get(k);
                    toChoose = single.get(0).get(k);
                    pos = k;
                    if (sum >= r){
                        break;
                    }
                }
                toChoose.getOperator().mutate();
                reward = toChoose.getOperator().getReward();
                q = singleQ.get(0).get(pos);
                max = singleQ.get(toChoose.getDestination()).get(0);
                for (int k = 0; k < singleQ.get(toChoose.getDestination()).size(); ++k){
                    if (singleQ.get(toChoose.getDestination()).get(k) > max){
                        max= singleQ.get(toChoose.getDestination()).get(k);
                    }
                }
                q = q + RATE * (reward + FACTOR * max - q);
                singleQ.get(0).set(pos, q);
                max = oneStatePopulation.getFittest();
                oneResult[i] = oneResult[i] + max;

                mutationOperator.mutate();
                max = mutationPopulation.getFittest();
                mtnResult[i] = mtnResult[i] + max;

                crossoverOperator.mutate();
                max = crossoverPopulation.getFittest();
                crsResult[i] = crsResult[i] + max;

                r = RNG.nextDouble();
                if (r < 0.5){
                    rndMtnOperator.mutate();
                } else {
                    rndCrsOperator.mutate();
                }
                max = randomPopulation.getFittest();
                rndResult[i] = rndResult[i] + max;
            }
            for (int i = 0; i < Q.size(); ++i){
                double down = 0;
                for (int k = 0; k < Q.get(i).size(); ++k){
                    down += Math.exp(Q.get(i).get(k) * BETHA);
                }
                for (int k = 0; k < P.get(i).size(); ++k){
                    P.get(i).set(k, Math.exp(Q.get(i).get(k) * BETHA) / down);
                }
                System.out.println();
            }
            for (int i = 0; i < singleQ.size(); ++i){
                double down = 0;
                for (int k = 0; k < singleQ.get(i).size(); ++k){
                    down += Math.exp(singleQ.get(i).get(k) * BETHA);
                }
                for (int k = 0; k < singleP.get(i).size(); ++k){
                    singleP.get(i).set(k, Math.exp(singleQ.get(i).get(k) * BETHA) / down);
                }
            }
        }
        for (int i = 0; i < STEPS_NUMBER; ++i){
            oneStateWriter.println(oneResult[i] / 150);
            twoStatesWriter.println(twoResult[i] / 150);
            randomWriter.println(rndResult[i] / 150);
            crossoverWriter.println(crsResult[i] / 150);
            mutationWriter.println(mtnResult[i] / 150);
        }
        oneStateWriter.flush();
        twoStatesWriter.flush();
        randomWriter.flush();
        crossoverWriter.flush();
        mutationWriter.flush();
    }
}
