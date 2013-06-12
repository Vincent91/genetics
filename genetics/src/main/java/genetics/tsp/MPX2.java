package genetics.tsp;

import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class MPX2 implements Operator {

    private static final double PROBABILITY = 0.1;

    private double reward;
    private MersenneTwisterRNG rng;
    private TspPopulation population;

    public MPX2(TspPopulation p, MersenneTwisterRNG r){
        population = p;
        rng = r;
        reward = 0;
    }

    @Override
    public void setPopulation(TspPopulation p){
        population = p;
    }

    @Override
    public TspPopulation getPopulation(){
        return population;
    }

    @Override
    public double getReward() {
        return  reward;
    }

    public TspIndividual getAncestor(){
        int a;
        int b;
        synchronized (rng) {
            a = Math.abs(rng.nextInt(population.getSize()));
            b = Math.abs(rng.nextInt(population.getSize()));
        }
        double fit1 = population.getIndividual(a).fitness(population.getGraph());
        double fit2 = population.getIndividual(b).fitness(population.getGraph());
        TspIndividual ancestor;
        double r;
        synchronized (rng){
            r = rng.nextDouble();
        }
        if (r > PROBABILITY){
            if (fit1 > fit2){
                ancestor = new TspIndividual(population.getIndividual(a));
            } else {
                ancestor = new TspIndividual(population.getIndividual(b));
            }
        } else {
            if (fit1 > fit2){
                ancestor = new TspIndividual(population.getIndividual(b));
            } else {
                ancestor = new TspIndividual(population.getIndividual(a));
            }
        }
        return ancestor;
    }

    @Override
    public void mutate() {
        TspPopulation offspring = new TspPopulation();
        for (int i = 0; i < population.getSize(); i += 2){
            TspIndividual ancestorOne = getAncestor();
            TspIndividual ancestorTwo = getAncestor();
            int[] path = new int[ancestorOne.size()];
            int[] path2 = new int[ancestorOne.size()];
            for (int k = 0; k < ancestorOne.size(); ++k){
                path[k] = ancestorOne.getPath()[k];
                path2[k] = ancestorTwo.getPath()[k];
            }
            int length;
            int a;
            synchronized (rng){
                length = rng.nextInt(3);
                length += 3;
                a = rng.nextInt(ancestorOne.size() - length + 1);
            }
            int b = a + length - 1;
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            for (int z = a; z <= b; ++z){
                map.put(ancestorOne.getPath()[z], ancestorTwo.getPath()[z]);
            }
            for (int k = 0; k < ancestorOne.size(); ++k){
                if (k >= a && k <= b){
                    continue;
                }
                int city = ancestorTwo.getPath()[k];
                while (map.containsKey(city)){
                    city = map.get(city);
                }
                path[k] = city;
            }
            map = new HashMap<Integer, Integer>();
            for (int z = a; z <= b; ++z){
                map.put(ancestorTwo.getPath()[z], ancestorOne.getPath()[z]);
            }
            for (int k = 0; k < ancestorTwo.size(); ++k){
                if (k >= a && k <= b){
                    continue;
                }
                int city = ancestorOne.getPath()[k];
                while (map.containsKey(city)){
                    city = map.get(city);
                }
                path2[k] = city;
            }
            if (!ancestorOne.check()){
                System.out.println("ALARM!!!");
            }
            if (!ancestorTwo.check()){
                System.out.println("ALARM!!!");
            }
            offspring.addIndividual(ancestorOne);
            offspring.addIndividual(ancestorTwo);
        }

        offspring.setGraph(population.getGraph());

        double bestOld = population.getFittest();
        double bestNew = offspring.getFittest();
        if (bestNew < bestOld){
            reward = 1;
//            reward = (bestOld - bestNew) / bestOld;
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
