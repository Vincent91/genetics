package genetics.tsp;

import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class MPX1 implements Operator {

    private static final double PROBABILITY = 0.1;

    private double reward;
    private MersenneTwisterRNG rng;
    private TspPopulation population;

    public MPX1(TspPopulation p, MersenneTwisterRNG r){
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
            int border = 0;
            synchronized (rng){
                border = rng.nextInt(ancestorOne.size() - 1);
            }
            for (int k = 0; k < border; ++k){
                int pos = k;
                for (int z = k + 1; z < path.length; ++z){
                    if (ancestorTwo.getPath()[k] == path[z]){
                        pos = z;
                        break;
                    }
                }
                int temp = path[k];
                path[k] = path[pos];
                path[pos] = temp;
            }
            for (int k = 0; k < border; ++k){
                int pos = k;
                for (int z = k + 1; z < path2.length; ++z){
                    if (ancestorOne.getPath()[k] == path2[z]){
                        pos = z;
                        break;
                    }
                }
                int temp = path2[k];
                path2[k] = path2[pos];
                path2[pos] = temp;
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
