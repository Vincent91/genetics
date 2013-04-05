package genetics;

import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.ArrayList;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class HiffRL {

    private static final int POPULATION_SIZE = 8;
    private static final int INDIVIDUAL_SIZE = 8;
    private static final double RATE = 0.5;
    private static final double FACTOR = 0.5;
    private static final double BETHA = 0.05;

    private static final MersenneTwisterRNG RNG = new MersenneTwisterRNG();

    public static void main(String[] args){
        Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
        CrossoverOperator mutation = new CrossoverOperator(p, RNG);
        MutationOperator mutation2 = new MutationOperator(p, RNG);
        ArrayList<ArrayList<Double>> Q = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> P = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<State>> states = new ArrayList<ArrayList<State>>();
        State s = new State(mutation, 0);
        State s2 = new State(mutation2, 0);
        for (int i = 0; i < 1; ++i) {
            Q.add(new ArrayList<Double>());
            P.add(new ArrayList<Double>());
            states.add(new ArrayList<State>());
        }
        states.get(0).add(s);
        states.get(0).add(s2);
        for (int i = 0; i < 1; ++i){
            for (int k = 0; k < 2; ++k) {
                Q.get(i).add(0.0);
                P.get(i).add(0.5);
            }
        }
        double r;
        int state = 0;
        for (int launches = 0; launches < 10; ++launches){
            state = 0;
            p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
            for (int i = 0; i < 2; ++i){
                states.get(0).get(i).getOperator().setPopulation(p);
            }
            for (int i = 0; i < 10; ++i){
                r = RNG.nextDouble();
                double sum = 0;
                State toChoose = null;
                int pos = -1;
                for (int k = 0; k < states.get(state).size(); ++k){
                    sum += P.get(state).get(k);
                    toChoose = states.get(0).get(k);
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
            double down = 0;
            for (int i = 0; i < Q.get(0).size(); ++i){
                down += Math.exp(Q.get(0).get(i) * BETHA);
            }
            for (int i = 0; i < Q.get(0).size(); ++i){
                System.out.print(Q.get(0).get(i) + " ");
            }
            System.out.println();
            for (int i = 0; i < P.get(0).size(); ++i){
                P.get(0).set(i, Math.exp(P.get(0).get(i) * BETHA) / down);
                System.out.print(P.get(0).get(i) + " ");
            }
            System.out.println();
        }
    }
}
