package genetics.hiff;

import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class BadMutationOperator implements Operator{

    private static final double PROBABILITY = 0.1;

    private Population population;
    private MersenneTwisterRNG rng;
    private double reward;

    public BadMutationOperator(Population p, MersenneTwisterRNG r){
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
//                r = rng.nextDouble();
//                if (r > PROBABILITY){
//                    break;
//                }
                int possition = Math.abs(rng.nextInt()) % individual.getSize();
                HiffIndividual mutant = new HiffIndividual(individual);
                for (int i = 0; i < mutant.getSize(); ++i){
                    mutant.set(i, i % 2);
                }
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
                double parentSum = 0;
                for (int i = 0; i < population.getSize(); ++i){
                    parentSum += population.getIndividual(i).fitness();
                }
                double childrenSum = parentSum;
//                childrenSum -= population.getIndividual(possition).fitness();
                childrenSum = childrenSum - min + mutant.fitness();
                if (mutant.fitness() > min){
                    population.setIndividual(possition, mutant);

                }
                if (childrenSum > parentSum){
                    reward = 1;
                } else if (childrenSum == parentSum) {
                    reward = 0.5;
                } else {
                    reward = 0;
                }
//                reward = (childrenSum - parentSum)/* / parentSum*/;
//                reward = (mutant.fitness() - f) / f;
//                reward = mutant.fitness() - f;
                break;
            } else {
                left = right;
            }
        }
    }
}
