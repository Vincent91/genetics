package genetics;

import com.sun.org.apache.bcel.internal.generic.PopInstruction;
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

    public MaxBlockStatesGen(double a, double g,double b, int learning, boolean gr){
        alpha = a;
        gamma = g;
        betha = b;
        ONE_STEP = learning;
        greedy = gr;
    }

    private boolean greedy;

    private static final MersenneTwisterRNG rng = new MersenneTwisterRNG();

    private static int populationSize = 1;
    private static int individualSize = 64;

    private double alpha;
    private double gamma;
    private double betha;

    private static int powerOfTwo = Integer.numberOfTrailingZeros(individualSize) + 1;

    private static double OPTIMUM = new HiffIndividual(Collections.nCopies(individualSize, 1)).fitness();;

    private static final int MAX_STEPS = 10000;
    private int ONE_STEP = 100;

    private static final int GLOBAL_STEPS = 101;

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

    private static int OPERATIONS_NUMBER = 3;

    private static Population[] habitat = generate();
    private static double[] resultsRandom;
    private static double[] operationCounter;

    private static Population[] generate(){
        Population[] h = new Population[GLOBAL_STEPS];
        operationCounter = new double[OPERATIONS_NUMBER];
        for (int pop = 0; pop < GLOBAL_STEPS; ++pop){
            h[pop] = new Population(populationSize, individualSize);
        }
        resultsRandom = new double[GLOBAL_STEPS];
        Arrays.fill(resultsRandom, Double.POSITIVE_INFINITY);
        for (int pop = 0; pop < GLOBAL_STEPS; ++pop){
            if (pop % 100 == 0){
                System.out.println("random pop " + pop);
            }
            Population randomP = new Population(h[pop]);
            Store store = new Store(rng);
            CAMO randomAMO = new CAMO(randomP, rng);
            CMO randomMO = new CMO(randomP, rng);
            CBMO randomCBMO = new CBMO(randomP, rng);
            CCO randomCO = new CCO(randomP, rng);
            CHO randomCHO = new CHO(randomP, rng);
            store.addOperator(randomAMO);
            store.addOperator(randomMO);
//            store.addOperator(randomCHO);
//            store.addOperator(randomCO);
            store.addOperator(randomCBMO);
            store.initProbabilities();
            store.initQ();
            for (int steps = 0; steps < MAX_STEPS; ++steps){
//                double r;
//                synchronized (rng){
//                    r = rng.nextDouble();
//                }
//                if (r < 0.2){
//                    randomAMO.mutate();
//                    operationCounter[0]++;
//                } else if (r < 0.40) {
//                    randomMO.mutate();
//                    operationCounter[1]++;
//                } else if (r < 0.60) {
//                    randomCHO.mutate();
//                    operationCounter[2]++;
//                } else if (r < 0.8) {
//                    randomCO.mutate();
//                    operationCounter[3]++;
//                } else {
//                    randomCBMO.mutate();
//                    operationCounter[4]++;
//                }
                store.chooseOperator().mutate();
                operationCounter[store.getChoosen()]++;
                if (randomP.getFittest() == OPTIMUM){
                    resultsRandom[pop] = steps;
                    break;
                }
            }
        }
        Arrays.sort(resultsRandom);
        return h;
    }

    @Override
    public String call() throws InterruptedException {
        double[] results = new double[GLOBAL_STEPS];
//        double[] resultsRandom = new double[GLOBAL_STEPS];
        Arrays.fill(results, Double.POSITIVE_INFINITY);
//        Arrays.fill(resultsRandom, Double.POSITIVE_INFINITY);

//        Population[] habitat = new Population[GLOBAL_STEPS];

//        for (int pop = 0; pop < GLOBAL_STEPS; ++pop){
//            habitat[pop] = new Population(populationSize, individualSize);
//        }

        Population p = new Population(habitat[0]);
        CAMO amo = new CAMO(p, rng);
        CMO mo = new CMO(p, rng);
        CBMO cbmo = new CBMO(p, rng);
        CCO co = new CCO(p, rng);
        CHO cho = new CHO(p, rng);

        double[] oCounter = new double[OPERATIONS_NUMBER];
        Store[] states = new Store[powerOfTwo];
        for (int i = 0; i < states.length; ++i){
            states[i] = new Store(rng);
            states[i].addOperator(amo);
            states[i].addOperator(mo);
//            states[i].addOperator(cho);
//            states[i].addOperator(co);
            states[i].addOperator(cbmo);
            states[i].initProbabilities();
            states[i].setBetha(betha);
        }


        for (int pop = 0; pop < GLOBAL_STEPS; ++pop){
            if (pop % 100 == 0){
                System.out.println(pop);
            }
            p = new Population(habitat[pop]);
            for (int i = 0; i < states.length; ++i){
                states[i].setPopulation(p);
                states[i].initQ();
                states[i].initProbabilities();
            }
            int state = getStatePop(p);
            for (int iterations = 0; iterations < MAX_STEPS / ONE_STEP; ++iterations) {
                for (int steps = 0; steps < ONE_STEP; ++steps){
                    if (!greedy){
                        states[state].chooseOperator().mutate();
                    } else {
                        states[state].greedyChoose().mutate();
                    }
                    oCounter[states[state].getChoosen()]++;
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
        }

        Arrays.sort(results);

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

        double overall = 0;
        double overallR = 0;
//        for (int i = 0; i < OPERATIONS_NUMBER; ++i){
//            overall += oCounter[i];
//            overallR += operationCounter[i];
//        }
//        for (int i = 0; i < OPERATIONS_NUMBER; ++i){
//            System.out.println("method " + oCounter[i] / overall * 100);
//            System.out.println("random " + operationCounter[i] / overallR * 100);
//        }

//        double good = 0;
//        for (int i = 0; i < GLOBAL_STEPS; ++i){
//            if (results[i] != MAX_STEPS){
//                ++good;
//            }
//        }
//
//        System.out.println(good);
//        System.out.println(exp);
//        System.out.println(Math.sqrt(dev));

        double tempO = 0;
        System.out.println(results[50] + " " + exp);
        for (int i = minIndex; i <= maxIndex; ++i){
            if (results[i] <= exp + 3 * Math.sqrt(dev) && results[i] >= exp - 3 * Math.sqrt(dev)){
                ++tempO;
            }
        }
        System.out.println(tempO / (maxIndex - minIndex));
        return "atata";


//        if (exp < expRandom * 0.9){
//            System.out.println(alpha + " " + gamma + " " + betha + " " + ONE_STEP + " " + exp + " " + expRandom +
//                        " " + Math.sqrt(dev) + " " + Math.sqrt(devRandom));
//            return alpha + " " + gamma + " " + betha + " " + ONE_STEP + " " + exp +
//                    " " + expRandom + " " + Math.sqrt(dev) + " " + Math.sqrt(devRandom);
//        } else {
//            System.out.println("fail");
//            return "fail";
//        }


//        System.out.println("Mediana " + results[GLOBAL_STEPS / 2] + " " + resultsRandom[GLOBAL_STEPS / 2]);
//
//        System.out.println("Subjects: " + (maxIndex - minIndex));
//        System.out.println("Learning: exp = " + exp + " dev = " + Math.sqrt(dev));
//        System.out.println("Random:   exp = " + expRandom + " dev = " + Math.sqrt(devRandom));
    }

}
