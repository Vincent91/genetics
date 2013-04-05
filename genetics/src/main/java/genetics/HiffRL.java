package genetics;

import genetics.hiff.CrossoverOperator;
import genetics.hiff.HiffIndividual;
import genetics.hiff.MutationOperator;
import genetics.hiff.Population;
import org.uncommons.maths.random.MersenneTwisterRNG;

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
    }
}
