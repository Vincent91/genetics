package genetics;

import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.ArrayList;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class HiffRL {

    private static final int POPULATION_SIZE = 60;
    private static final int INDIVIDUAL_SIZE = 16;
    private static final double RATE = 0.7;
    private static final double FACTOR = 0.5;
    private static final double BETHA = 0.1;

    private static final MersenneTwisterRNG RNG = new MersenneTwisterRNG();

    public static void main(String[] args){
        Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
        ArrayList<ArrayList<Double>> Q = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> P = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<State>> states = new ArrayList<ArrayList<State>>();
        State s0t0 = new State(new MutationOperator(p, RNG), 0);
        State s0t1 = new State(new CrossoverOperator(p, RNG), 1);
        State s1t0 = new State(new MutationOperator(p, RNG), 0);
        State s1t1 = new State(new CrossoverOperator(p, RNG), 1);

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
        for (int launches = 0; launches < 150; ++launches){
            state = Math.abs(RNG.nextInt()) % 2;
            p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
            for (int i = 0; i < 2; ++i){
                for (int k = 0; k < 2; ++k){
                    states.get(i).get(k).getOperator().setPopulation(p);
                }
            }
            for (int i = 0; i < 400; ++i){
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
            }
            for (int i = 0; i < Q.size(); ++i){
                double down = 0;
                System.out.println(i);
                for (int k = 0; k < Q.get(i).size(); ++k){
                    down += Math.exp(Q.get(i).get(k) * BETHA);
                    System.out.print(Q.get(i).get(k) + " ");
                }
                System.out.println();
                for (int k = 0; k < P.get(i).size(); ++k){
                    P.get(i).set(k, Math.exp(Q.get(i).get(k) * BETHA) / down);
                    System.out.print(P.get(i).get(k) + " ");
                }
                System.out.println();
            }
            System.out.println("Fittest " + p.getFittest());
        }

    }
}
