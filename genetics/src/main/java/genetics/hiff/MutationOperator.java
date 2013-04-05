package genetics.hiff;

import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class MutationOperator implements Operator{

    private static final double PROBABILITY = 0.1;

    private Population population;
    private MersenneTwisterRNG rng;
    private double reward;

    public MutationOperator(Population p, MersenneTwisterRNG r){
        population = p;
        rng = r;
        reward = 0;
    }

    public Population getPopulation(){
        return population;
    }

    public void setPopulation(Population p){
        population = p;
    }

    public double getReward(){
        return reward;
    }

    public void mutate(){
        double sum = 0;
        for (HiffIndividual i : population.getPopulation()){
            sum += i.fitness();
        }
        double r = rng.nextDouble() * sum;
        double left = 0;
        double right;
        for (HiffIndividual individual : population.getPopulation()){
            double f = individual.fitness();
            right = left + f;
            if (r < right && r > left){
                r = rng.nextDouble();
                if (r > PROBABILITY){
                    break;
                }
                int possition = Math.abs(rng.nextInt()) % individual.getSize();
                HiffIndividual mutant = new HiffIndividual(individual);
                mutant.inverse(possition);
                possition = 0;
                double min = population.getIndividual(0).fitness();
                double fitnessStore;
                for (int i = 1; i < population.getSize(); ++i){
                    fitnessStore = population.getIndividual(i).fitness();
                    if (fitnessStore < min){
                        min = fitnessStore;
                        possition = i;
                    }
                }
                population.setIndividual(possition, mutant);
//                reward = (mutant.fitness() - f) / f;
                reward = mutant.fitness() - f;
                break;
            } else {
                left = right;
            }
        }
    }
}