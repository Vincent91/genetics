package genetics.hiff;

import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class CrossoverOperator implements Operator{
    private Population population;
    private MersenneTwisterRNG rng;
    private double reward;

    public CrossoverOperator(Population p, MersenneTwisterRNG r){
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
        for (int i = 0; i < population.getSize(); ++i) {
            sum += population.getPopulation().get(i).fitness();
        }
        double r = rng.nextDouble() * sum;
        double left = 0;
        double right;
        HiffIndividual parent1 = new HiffIndividual(population.getIndividual(0).getSize());
        HiffIndividual parent2 = new HiffIndividual(population.getIndividual(0).getSize());
        for (int i = 0; i < population.getSize(); ++i){
            right = population.getIndividual(i).fitness() + left;
            if (r < right && r > left){
                parent1 = population.getIndividual(i);
                break;
            } else {
                left = right;
            }
        }
        left = 0;
        r = rng.nextDouble() * sum;
        for (int i = 0; i < population.getSize(); ++i){
            right = population.getIndividual(i).fitness() + left;
            if (r < right && r > left) {
                parent2 = population.getIndividual(i);
                break;
            } else {
                left = right;
            }
        }
        double maxParentFitness = Math.max(parent1.fitness(), parent2.fitness());
        int a = Math.abs(rng.nextInt()) % parent1.getSize();
        int b = Math.abs(rng.nextInt()) % parent1.getSize();
        if (a > b){
            int temp = a;
            a = b;
            b = temp;
        }
        HiffIndividual child1 = new HiffIndividual(parent1);
        HiffIndividual child2 = new HiffIndividual(parent2);
        for (int i = a; i <= b; ++i){
            int parentOneNode = parent1.getBlock().get(i);
            int parentTwoNode = parent2.getBlock().get(i);
            child2.set(i, parentOneNode);
            child1.set(i, parentTwoNode);
        }
        double minOne = population.getIndividual(0).fitness();
        int posOne = 0;
        for (int i = 1; i < population.getSize(); ++i){
            if (population.getIndividual(i).fitness() < minOne){
                minOne = population.getIndividual(i).fitness();
                posOne = i;
            }
        }
        int posTwo;
        double minTwo;
        if (posOne == 0){
            posTwo = population.getSize() - 1;
            minTwo = population.getIndividual(posTwo).fitness();
        } else {
            posTwo = 0;
            minTwo = population.getIndividual(0).fitness();
        }
        for (int i = 0; i < population.getSize(); ++i) {
            if (i != posOne && population.getIndividual(i).fitness() < minTwo){
                posTwo = i;
                minTwo = population.getIndividual(i).fitness();
            }
        }
        double parentSum = 0;
        for (int i = 0; i < population.getSize(); ++i){
            parentSum += population.getIndividual(i).fitness();
        }
        double oldFittest = population.getFittest();
        double childrenSum = parentSum;
        if (minOne > minTwo) {
            double temp = minOne;
            minOne = minTwo;
            minTwo = temp;
            int tempPos = posOne;
            posOne = posTwo;
            posTwo = tempPos;
        }
        double childOneFit = child1.fitness();
        double childTwoFit = child2.fitness();
        if (childOneFit > childTwoFit){
            double temp = childOneFit;
            childOneFit = childTwoFit;
            childTwoFit = temp;
            HiffIndividual tempChild = child1;
            child1 = child2;
            child2 = tempChild;
        }
        if (childOneFit > minOne){
            population.setIndividual(posOne, child1);
            if (childTwoFit > minTwo){
                population.setIndividual(posTwo, child2);
//                reward = 1;
            } else {
                population.setIndividual(posOne, child2);
//                reward = 0.5;
            }
        } else {
            if (childTwoFit > minOne){
                population.setIndividual(posOne, child2);
//                reward = 0.5;
            } else {
//                reward = 0;
            }
        }
        double newFittest = population.getFittest();
        if (newFittest > oldFittest) {
            reward = 1;
        } else if (newFittest == oldFittest) {
            reward = 0.5;
        } else {
            reward = 0;
        }
//        childrenSum -= population.getIndividual(posOne).fitness();
//        childrenSum -= population.getIndividual(posTwo).fitness();
//        childrenSum += child1.fitness();
//        childrenSum += child2.fitness();
//        if (childrenSum > parentSum){
//            reward = 1;
//            population.setIndividual(posOne, child1);
//            population.setIndividual(posTwo, child2);
//        } else {
//            reward = 0;
//        }
//        reward = (childrenSum - parentSum)/* / parentSum*/;
        //double maxChildFitness = Math.max(child1.fitness(), child2.fitness());
        //reward = (maxChildFitness - maxParentFitness) / maxParentFitness;
        //reward = maxChildFitness - maxParentFitness;
    }
}
