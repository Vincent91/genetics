package genetics.tsp;

import genetics.tsp.TspIndividual;
import genetics.tsp.Operator;
import genetics.tsp.TspPopulation;
import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class SwapOperator implements Operator {

    private static double PROBABILITY = 0.3;

    private TspPopulation population;
    private MersenneTwisterRNG rng;
    private double reward;

    public SwapOperator(TspPopulation p, MersenneTwisterRNG r){
        population = p;
        rng = r;
        reward = 0;
    }

    @Override
    public TspPopulation getPopulation(){
        return population;
    }

    @Override
    public void setPopulation(TspPopulation p){
        population = p;
    }

    @Override
    public double getReward(){
        return reward;
    }

    @Override
    public void mutate(){
        TspPopulation offspring = new TspPopulation();
        for (int i = 0; i < population.getSize(); ++i){
            double r;
            synchronized (rng){
                r = rng.nextDouble();
            }
            if (r < PROBABILITY){
                TspIndividual mutant = new TspIndividual(population.getIndividual(i));
                int possition1;
                synchronized (rng){
                    possition1 = Math.abs(rng.nextInt()) % mutant.size();
                }
                int possition2;
                synchronized (rng){
                    possition2 = Math.abs(rng.nextInt()) % mutant.size();
                }
                int[] path = mutant.getPath();
                int temp = path[possition1];
                path[possition1] = path[possition2];
                path[possition2] = temp;
                mutant.setPath(path);
                offspring.addIndividual(mutant);
            } else {
                offspring.addIndividual(new TspIndividual(population.getIndividual(i)));
            }
        }

        offspring.setGraph(population.getGraph());

        double bestOld = population.getFittest();
        double bestNew = offspring.getFittest();
        if (bestNew < bestOld){
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
