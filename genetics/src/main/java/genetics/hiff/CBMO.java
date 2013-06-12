package genetics.hiff;

import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class CBMO implements Operator {

    private static final double PROBABILITY = 1;

    private double reward;
    private MersenneTwisterRNG rng;
    private Population population;

    public CBMO(Population p, MersenneTwisterRNG r){
        population = p;
        rng = r;
        reward = 0;
    }

    @Override
    public void setPopulation(Population p){
        population = p;
    }

    @Override
    public Population getPopulation(){
        return population;
    }

    @Override
    public double getReward() {
        return  reward;
    }

    @Override
    public void mutate() {
        Population offspring = new Population();
        for (int i = 0; i < population.getSize(); ++i){
            double r;
            synchronized (rng){
                r = rng.nextDouble();
            }
            if (r < PROBABILITY){
                HiffIndividual mutant = new HiffIndividual(population.getIndividual(i));
                for (int k = 0; k < mutant.getSize(); ++k){
                    mutant.set(k, k % 2);
                }
                offspring.addIndividual(mutant);
            } else {
                offspring.addIndividual(new HiffIndividual(population.getIndividual(i)));
            }
        }
        double bestOld = population.getFittest();
        double bestNew = offspring.getFittest();
        if (bestNew > bestOld){
            reward = 1;
            for (int i = 0; i < population.getSize(); ++i){
                population.setIndividual(i, offspring.getIndividual(i));
            }
        } else if (bestNew == bestOld){
//            reward = 0.5;
            reward = 0;
        } else {
            reward = 0;
        }
    }

}
