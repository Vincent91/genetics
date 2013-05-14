package genetics;

import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class MaxBlockStatesGen implements Callable<String> {

    public MaxBlockStatesGen(double a, double g, double b){
        this(a, g, b, 10, 32);
    }

    public MaxBlockStatesGen(double a, double g,double b, int ps, int is){
        alpha = a;
        gamma = g;
        betha = b;
        populationSize = ps;
        individualSize = is;
        powerOfTwo = Integer.numberOfTrailingZeros(individualSize) + 1;
        OPTIMUM = new HiffIndividual(Collections.nCopies(individualSize, 1)).fitness();
    }

    private static final MersenneTwisterRNG rng = new MersenneTwisterRNG();

    private int populationSize;
    private int individualSize;

    private double alpha;
    private double gamma;
    private double betha;

    private int powerOfTwo;

    private double OPTIMUM;

    private static final int MAX_STEPS = 10000;
    private static final int ONE_STEP = 10;

    private static final int GLOBAL_STEPS = 201;

    private static int getState(HiffIndividual ind) {
        return ind.getMaxFilledBlock();
    }

    private static int getStatePop(Population p){
        int state = p.getIndividual(0).getMaxFilledBlock();
        for (int i = 1; i < p.getSize(); ++i) {
            int block = getState(p.getIndividual(i));
            state = (block > state) ? block : state;
        }
        return state;
    }

    @Override
    public String call() throws InterruptedException {
        double[] results = new double[GLOBAL_STEPS];
        double[] resultsRandom = new double[GLOBAL_STEPS];
        Arrays.fill(results, Double.POSITIVE_INFINITY);
        Arrays.fill(resultsRandom, Double.POSITIVE_INFINITY);

        for (int pop = 0; pop < GLOBAL_STEPS; ++pop){
//            if (pop % 10 == 0){
//                System.out.println(pop);
//            }
            Population p = new Population(populationSize, individualSize);
            Population randomP = new Population(p);
            AdvancedMutationOperator amo = new AdvancedMutationOperator(p, rng);
            MutationOperator mo = new MutationOperator(p, rng);
            CrossoverOperator co = new CrossoverOperator(p, rng);
            Store[] states = new Store[powerOfTwo];
            for (int i = 0; i < states.length; ++i){
                states[i] = new Store(rng);
                states[i].addOperator(amo);
                states[i].addOperator(mo);
                states[i].addOperator(co);
                states[i].setBetha(betha);
                if (pop == 0)
                    states[i].initProbabilities();
            }
            int state = getStatePop(p);
            for (int iterations = 0; iterations < MAX_STEPS / ONE_STEP; ++iterations) {
                for (int steps = 0; steps < ONE_STEP; ++steps){
                    states[state].chooseOperator().mutate();
                    double reward = states[state].getOperators().get(states[state].getChoosen()).getReward();
                    double q = states[state].getQ(states[state].getChoosen());
                    q = q + alpha * (reward + gamma * states[state].getMaxQ() - q);
                    states[state].setQ(states[state].getChoosen(), q);
                    state = getStatePop(p);
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
            AdvancedMutationOperator randomAMO = new AdvancedMutationOperator(randomP, rng);
            MutationOperator randomMO = new MutationOperator(randomP, rng);
            CrossoverOperator randomCO = new CrossoverOperator(randomP, rng);
            for (int steps = 0; steps < MAX_STEPS; ++steps){
                double r = rng.nextDouble();
                if (r < 0.33){
                    randomAMO.mutate();
                } else if (r < 0.66) {
                    randomMO.mutate();
                } else {
                    randomCO.mutate();
                }
                if (randomP.getFittest() == OPTIMUM){
                    resultsRandom[pop] = steps;
                    break;
                }
            }
        }
        Arrays.sort(results);
        Arrays.sort(resultsRandom);
//        for (int i = 0; i < GLOBAL_STEPS; ++i){
//            System.out.println(i + " " + results[i] + " " + resultsRandom[i]);
//        }

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

        if (exp < expRandom - 50){
            System.out.println(alpha + " " + gamma + " " + betha + " " + ONE_STEP + " " + exp + " " + expRandom +
                        " " + Math.sqrt(dev) + " " + Math.sqrt(devRandom));
            return alpha + " " + gamma + " " + betha + " " + ONE_STEP + " " + exp +
                    " " + expRandom + " " + Math.sqrt(dev) + " " + Math.sqrt(devRandom);
        } else {
            System.out.println("fail");
            return "fail";
        }

//        System.out.println("Mediana " + results[GLOBAL_STEPS / 2] + " " + resultsRandom[GLOBAL_STEPS / 2]);
//
//        System.out.println("Subjects: " + (maxIndex - minIndex));
//        System.out.println("Learning: exp = " + exp + " dev = " + Math.sqrt(dev));
//        System.out.println("Random:   exp = " + expRandom + " dev = " + Math.sqrt(devRandom));
    }

}