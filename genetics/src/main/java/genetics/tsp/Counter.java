package genetics.tsp;

import genetics.tsp.Store;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class Counter implements Callable<String> {

    public static final int POPULATION_SIZE = 10;
    public static final int INDIVIDUAL_SIZE = 80;

    public Counter(double a, double b, double c){
        ALPHA = a;
        GAMMA = b;
        BETHA = c;
    }

    public double ALPHA;
    public double GAMMA;
    public double BETHA;

    public static final int GLOBAL_STEPS = 101;
    public static final int ONE_STEP = 10;
    public static final int MAX_STEPS = 6400;

    public  static final MersenneTwisterRNG RNG = new MersenneTwisterRNG();

    public static int getState(int t){
//        return 0;
        if (t < 400){
            return 0;
        } else if (t < 1600) {
            return 1;
        } else if (t < 3200) {
            return 2;
        } else {
            return 3;
        }
    }

    public static int getStateFit(long n, long o){
        double div = (double) n / o;
        if (div < 0.2){
            return 3;
        } else if (div < 0.5) {
            return 2;
        } else if (div < 0.8) {
            return 1;
        } else {
            return 0;
        }
//    return 0;
    }

    private static long[] resultsRandom;
    private static TspPopulation[] habitat = generate();

    private static TspPopulation[] generate(){
        List<Graph> graphs = new ArrayList<Graph>();
        for (int i = 0; i < GLOBAL_STEPS; ++i){
            Graph g = new Graph(INDIVIDUAL_SIZE);
            graphs.add(g);
        }
        TspPopulation[] h = new TspPopulation[GLOBAL_STEPS];
        for (int i = 0; i < GLOBAL_STEPS; ++i){
//            h[i] = new TspPopulation(POPULATION_SIZE, INDIVIDUAL_SIZE, graphs.get(i).getGraph());
            h[i] = new TspPopulation(POPULATION_SIZE, INDIVIDUAL_SIZE, graphs.get(0).getGraph());
        }
        resultsRandom = new long[GLOBAL_STEPS];
        Arrays.fill(resultsRandom, Long.MAX_VALUE);
        for (int i = 0; i < GLOBAL_STEPS; ++i){
            if (i % 100 == 0){
                System.out.println("random " + i);
            }
            TspPopulation populationRandom = new TspPopulation(h[i]);
            InverseOperator randomIO = new InverseOperator(populationRandom, RNG);
            SwapOperator randomSWO = new SwapOperator(populationRandom, RNG);
            ScrambleOperator randomSCO = new ScrambleOperator(populationRandom, RNG);
            MPX1 randomMPX1 = new MPX1(populationRandom, RNG);
            MPX2 randomMPX2 = new MPX2(populationRandom, RNG);
            for (int step = 0; step < MAX_STEPS; ++step){
                double r;
                synchronized (RNG){
                    r = RNG.nextDouble();
                }
                if (r < 0.2) {
                    randomIO.mutate();
                } else if (r < 0.4) {
                    randomSWO.mutate();
                } else if (r < 0.6) {
                    randomSCO.mutate();
                } else if (r < 0.8) {
                    randomMPX1.mutate();
                } else {
                    randomMPX2.mutate();
                }
            }
            resultsRandom[i] = populationRandom.getFittest();
        }
        Arrays.sort(resultsRandom);
        return h;
    }

    @Override
    public String call(){
        long[] result = new long[GLOBAL_STEPS];
        Arrays.fill(result, Long.MAX_VALUE);

        for (int pop = 0; pop < GLOBAL_STEPS; ++pop){

            if (pop % 100 == 0){
                System.out.println(pop);
            }

            TspPopulation population = new TspPopulation(habitat[pop]);
            InverseOperator io = new InverseOperator(population, RNG);
            SwapOperator swo = new SwapOperator(population, RNG);
            ScrambleOperator sco = new ScrambleOperator(population, RNG);
            MPX1 mpx1 = new MPX1(population, RNG);
            MPX2 mpx2 = new MPX2(population, RNG);
            Store[][] states = new Store[4][4];
            for (int i = 0; i < states.length; ++i){
                for (int k = 0; k < states[i].length; ++k){
                    states[i][k] = new Store(RNG);
                    states[i][k].addOperator(io);
                    states[i][k].addOperator(swo);
                    states[i][k].addOperator(sco);
                    states[i][k].addOperator(mpx1);
                    states[i][k].addOperator(mpx2);
                    states[i][k].initProbabilities();
                    states[i][k].setBetha(BETHA);
                }
            }

            long initFitness = population.getFittest();
            int stateTime = 0;
            int stateFit = 0;
            for (int iterations = 0; iterations < MAX_STEPS / ONE_STEP; ++iterations){
                for (int step = 0; step < ONE_STEP; ++step){
//                    states[stateTime][stateFit].chooseOperator().mutate();
                    states[stateTime][stateFit].greedyChoose().mutate();
                    double reward = states[stateTime][stateFit].getOperators().get(states[stateTime][stateFit].getChoosen()).getReward();
                    double q = states[stateTime][stateFit].getQ(states[stateTime][stateFit].getChoosen());
                    q = q + ALPHA * (reward + GAMMA * states[stateTime][stateFit].getMaxQ() - q);
                    states[stateTime][stateFit].setQ(states[stateTime][stateFit].getChoosen(), q);
                    stateTime = getState(iterations * ONE_STEP + step);
                    stateFit = getStateFit(population.getFittest(), initFitness);
                }
                for (int i = 0; i < states.length; ++i){
                    for (int k = 0; k < states[i].length; ++k){
                        states[i][k].countProbabilites();
                    }
                }
            }
            result[pop] = population.getFittest();


        }

        Arrays.sort(result);

//        for (int i = 0; i < GLOBAL_STEPS; ++i){
//            System.out.println(i + " " + result[i] + " " + resultRandom[i]);
//        }
        int minIndex = (int) (0.05 * GLOBAL_STEPS);
        int maxIndex = (int) (0.95 * GLOBAL_STEPS);

        double exp = 0, expRandom = 0;
        for (int i = minIndex; i < maxIndex; ++i) {
            exp += result[i];
            expRandom += resultsRandom[i];
        }
        exp /= (maxIndex - minIndex);
        expRandom /= (maxIndex - minIndex);
        double dev = 0, devRandom = 0;
        for (int i = minIndex; i < maxIndex; ++i) {
            dev += (result[i] - exp) * (result[i] - exp);
            devRandom += (resultsRandom[i] - expRandom) * (resultsRandom[i] - expRandom);
        }
        dev /= (maxIndex - minIndex);
        devRandom /= (maxIndex - minIndex);

//        System.out.println("Mediana " + result[GLOBAL_STEPS / 2] + " " + resultRandom[GLOBAL_STEPS / 2]);

//        System.out.println("Subjects: " + (maxIndex - minIndex));
//        System.out.println("Learning: exp = " + exp + " dev = " + Math.sqrt(dev));
//        System.out.println("Random:   exp = " + expRandom + " dev = " + Math.sqrt(devRandom));

        if (exp < expRandom){
            System.out.println(ALPHA + " " + GAMMA + " " + BETHA + " " + exp + " " + expRandom
                    + " " + Math.sqrt(dev) + " " + Math.sqrt(devRandom));
            return ALPHA + " " + GAMMA + " " + BETHA + " " + exp + " " + expRandom;
        } else {
            System.out.println("fail");
            return "fail";
        }

    }

    private static class Graph {

        private int[][] graph;

        public Graph(int n) {
            graph = new int[n][n];
            for (int i = 0; i < graph.length; ++i){
                for (int k = 0; k < graph.length; ++k){
                    graph[i][k] = RNG.nextInt(100);
                }
            }
        }

        public int[][] getGraph() {
            return graph;
        }

    }

}
