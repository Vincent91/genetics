package genetics;

import genetics.hiff.CrossoverOperator;
import genetics.hiff.HiffIndividual;
import genetics.hiff.MutationOperator;
import genetics.hiff.Population;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.ArrayList;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class HiffRL {

    private static final int POPULATION_SIZE = 8;
    private static final int INDIVIDUAL_SIZE = 8;

    private static final MersenneTwisterRNG RNG = new MersenneTwisterRNG();

    public static void main(String[] args){
        Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
        CrossoverOperator mutation = new CrossoverOperator(p, RNG);
        MutationOperator mutation2 = new MutationOperator(p, RNG);
        ArrayList<ArrayList<Double>> Q = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> P = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < 2; ++i) {
            Q.add(new ArrayList<Double>());
            P.add(new ArrayList<Double>());
        }
        for (int i = 0; i < 2; ++i){
            for (int k = 0; k < 2; ++k) {
                Q.get(i).add(0.0);
                P.get(i).add(0.5);
            }
        }
        for (int i = 0; i < 10; ++i){
            int state = RNG.nextInt() % 2;
            for (int k = 0; k < 10; ++k){
                double probability = RNG.nextDouble();
                double sum = 0;
                int newState = -1;
                for (int z = 0; sum < probability; ++z){
                    sum += P.get(state).get(z);
                    newState = z;
                }
            }
        }
    }
}
