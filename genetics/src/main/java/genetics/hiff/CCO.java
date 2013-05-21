package genetics.hiff;

import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class CCO implements Operator {

    private static final double PROBABILITY = 0.1;

    private double reward;
    private MersenneTwisterRNG rng;
    private Population population;

    public CCO(Population p, MersenneTwisterRNG r){
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

    public HiffIndividual getAncestor(){
        int a;
        int b;
        synchronized (rng) {
            a = Math.abs(rng.nextInt(population.getSize()));
            b = Math.abs(rng.nextInt(population.getSize()));
        }
        double fit1 = population.getIndividual(a).fitness();
        double fit2 = population.getIndividual(b).fitness();
        HiffIndividual ancestor;
        double r;
        synchronized (rng){
            r = rng.nextDouble();
        }
        if (r > PROBABILITY){
            if (fit1 > fit2){
                ancestor = new HiffIndividual(population.getIndividual(a));
            } else {
                ancestor = new HiffIndividual(population.getIndividual(b));
            }
        } else {
            if (fit1 > fit2){
                ancestor = new HiffIndividual(population.getIndividual(b));
            } else {
                ancestor = new HiffIndividual(population.getIndividual(a));
            }
        }
        return ancestor;
    }

    @Override
    public void mutate() {
        Population offspring = new Population();
        for (int i = 0; i < population.getSize(); i += 2){
            HiffIndividual ancestorOne = getAncestor();
            HiffIndividual ancestorTwo = getAncestor();
            int a;
            int b;
            synchronized (rng){
                a = Math.abs(rng.nextInt(ancestorOne.getSize()));
                b = Math.abs(rng.nextInt(ancestorOne.getSize()));
            }
            if (a > b){
                int temp = a;
                a = b;
                b = temp;
            }
            for (int k = a; k <= b; ++k){
                int tempOne = ancestorOne.getBlock().get(k);
                int tempTwo = ancestorTwo.getBlock().get(k);
                ancestorOne.set(k, tempTwo);
                ancestorTwo.set(k, tempOne);
            }
            offspring.addIndividual(ancestorOne);
            offspring.addIndividual(ancestorTwo);
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
