package genetics.hiff;

import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class AdvancedMutationOperator implements Operator{

    private Population population;
    private MersenneTwisterRNG rng;
    private double reward;

    public AdvancedMutationOperator(Population p, MersenneTwisterRNG r){
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
                for (int i = possition;i < individual.getSize(); ++i) {
                    mutant.inverse(i);
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
                double oldFittest = population.getFittest();
                double childrenSum = parentSum;
                childrenSum -= min;
                childrenSum += mutant.fitness();
                if (childrenSum > parentSum) {
                    population.setIndividual(possition, mutant);
                }
                double newFittest = population.getFittest();
//                if (newFittest > oldFittest){
//                    reward = 1;
//                } else if (newFittest == oldFittest) {
//                    reward = 0.5;
//                } else {
//                    reward = 0;
//                }
                if (childrenSum > parentSum){
                    reward = 1;
                } else if (childrenSum == parentSum) {
                    reward = 0.5;
                } else {
                    reward = 0;
                }
//                reward = population.getIndividual(possition).fitness() - f;
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
