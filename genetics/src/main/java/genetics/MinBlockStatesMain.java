package genetics;

import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class MinBlockStatesMain {

    private static final MersenneTwisterRNG rng = new MersenneTwisterRNG();

    private static final int POPULATION_SIZE = 10;
    private static final int INDIVIDUAL_SIZE = 64;

    private static final double ALPHA = 0.2;
    private static final double GAMMA = 0.4;
    private static final double BETHA = 0.1;

    private static final int powerOfTwo = Integer.numberOfTrailingZeros(INDIVIDUAL_SIZE) + 1;

    private static final double OPTIMUM = new HiffIndividual(Collections.nCopies(INDIVIDUAL_SIZE, 1)).fitness();

    private static final int MAX_STEPS = 10000;
    private static final int ONE_STEP = 100;

    private static final int GLOBAL_STEPS = 51;

    private static int getState(Population p) {
        int max = -1;
        for (HiffIndividual i : p.getPopulation()){
            int t = i.getMaxFilledBlock();
            if (t > max) {
                max = t;
            }
        }
        return max;
    }

    public static void main(String[] args){
        double[] results = new double[GLOBAL_STEPS];
        double[] resultsRandom = new double[GLOBAL_STEPS];
        Arrays.fill(results, Double.POSITIVE_INFINITY);
        Arrays.fill(resultsRandom, Double.POSITIVE_INFINITY);

        for (int pop = 0; pop < GLOBAL_STEPS; ++pop){
            if (pop % 10 == 0){
                System.out.println(pop);
            }
            Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
            Population randomP = new Population(p);
            CAMO amo = new CAMO(p, rng);
            CMO mo = new CMO(p, rng);
            CCO cco = new CCO(p, rng);
            Store[] states = new Store[powerOfTwo];
            for (int i = 0; i < states.length; ++i){
                states[i] = new Store(rng);
                states[i].addOperator(amo);
                states[i].addOperator(mo);
                states[i].addOperator(cco);
                states[i].setBetha(BETHA);
                states[i].initProbabilities();
            }
            int state = getState(p);
            for (int iterations = 0; iterations < MAX_STEPS / ONE_STEP; ++iterations) {
                for (int steps = 0; steps < ONE_STEP; ++steps){
                    states[state].chooseOperator().mutate();
                    double reward = states[state].getOperators().get(states[state].getChoosen()).getReward();
                    double q = states[state].getQ(states[state].getChoosen());
                    q = q + ALPHA * (reward + GAMMA * states[state].getMaxQ() - q);
                    states[state].setQ(states[state].getChoosen(), q);
                    state = getState(p);
                    if (p.getFittest() == OPTIMUM){
                        results[pop] = iterations * ONE_STEP + steps;
                        iterations = MAX_STEPS;
                        break;
                    }
                }
                for (int i = 0; i < states.length; ++i){
                    states[i].countProbabilites();
                }
            }
            CAMO randomAMO = new CAMO(randomP, rng);
            CMO randomMO = new CMO(randomP, rng);
            CCO randomCCO = new CCO(randomP, rng);
            for (int steps = 0; steps < MAX_STEPS; ++steps){
                double r = rng.nextDouble();
                if (r < 0.33){
                    randomAMO.mutate();
                } else if (r < 0.66) {
                    randomMO.mutate();
                } else {
                    randomCCO.mutate();
                }
                if (randomP.getFittest() == OPTIMUM){
                    resultsRandom[pop] = steps;
                    break;
                }
            }
        }
        Arrays.sort(results);
        Arrays.sort(resultsRandom);
        for (int i = 0; i < GLOBAL_STEPS; ++i){
            System.out.println(i + " " + results[i] + " " + resultsRandom[i]);
        }

        int minIndex = (int) (0.05 * GLOBAL_STEPS);
        int maxIndex = (int) (0.95 * GLOBAL_STEPS);

        double exp = 0, expRandom = 0;
        for (int i = minIndex; i < maxIndex; ++i) {
            exp += results[i];
            expRandom += resultsRandom[i];
        }
        exp /= (maxIndex - minIndex);
        expRandom /= (maxIndex - minIndex);
        double dev = 0, devRandom = 0;
        for (int i = minIndex; i < maxIndex; ++i) {
            dev += (results[i] - exp) * (results[i] - exp);
            devRandom += (resultsRandom[i] - expRandom) * (resultsRandom[i] - expRandom);
        }
        dev /= (maxIndex - minIndex);
        devRandom /= (maxIndex - minIndex);

        System.out.println("Mediana " + results[GLOBAL_STEPS / 2] + " " + resultsRandom[GLOBAL_STEPS / 2]);

        System.out.println("Subjects: " + (maxIndex - minIndex));
        System.out.println("Learning: exp = " + exp + " dev = " + Math.sqrt(dev));
        System.out.println("Random:   exp = " + expRandom + " dev = " + Math.sqrt(devRandom));
    }

}
