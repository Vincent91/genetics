package genetics;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class StatesMain {

    private static final MersenneTwisterRNG rng = new MersenneTwisterRNG();

    private static final int STATES_NUMBER = 48;

    private static final int POPULATION_SIZE = 40;
    private static final int INDIVIDUAL_SIZE = 16;

    private static final double ALPHA = 0.5;
    private static final double GAMMA = 0.5;
    private static final double BETHA = 0.05;

    private static final int STEPS = 6400;

    public static void main(String[] args){

        Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
        CrossoverOperator crossoverOperator = new CrossoverOperator(p, rng);
        MutationOperator mutationOperator = new MutationOperator(p, rng);
        HybridOperator hybridOperator = new HybridOperator(p, rng);

        Population test = new Population(p);

        Store[] states = new Store[STATES_NUMBER];

        double middle_sum = 0;

        for (int i = 0; i < STATES_NUMBER; ++i){
            states[i] = new Store(rng);
            states[i].addOperator(mutationOperator);
            states[i].addOperator(crossoverOperator);
            states[i].addOperator(hybridOperator);
            states[i].initProbabilities();
            states[i].setBetha(BETHA);
        }

        double initial_fitness = 0;

        for (int i = 0; i < POPULATION_SIZE; ++i){
            initial_fitness += p.getIndividual(i).fitness();
        }

        double entrophy = 0;

        for (int i = 0; i < POPULATION_SIZE; ++i){
            double prob = p.getIndividual(i).fitness() / initial_fitness;
            entrophy -= prob * Math.log(prob) / Math.log(2);
        }

        double entrophyStep = (Math.log(POPULATION_SIZE) / Math.log(2)) / 3;
        int entrophy_mul = 0;
        if (entrophy < entrophyStep){
            entrophy_mul = 0;
        } else if (entrophy < entrophyStep * 2){
            entrophy_mul = 1;
        } else {
            entrophy_mul = 2;
        }

        int fitness_mul = 3;
        int time_mul = 0;

        int state = entrophy_mul + 3 * fitness_mul + 12 * time_mul;

        for (int round = 0; round < 150; ++round){

            for (int i = 0; i < STEPS; ++i){
                Operator o = states[state].chooseOperator();
                o.mutate();
                double reward = o.getReward();
                entrophy = 0;
                double currentPopulation = 0;
                for (int k = 0; k < POPULATION_SIZE; ++k){
                    currentPopulation += p.getIndividual(k).fitness();
                }
                for (int k = 0; k < POPULATION_SIZE; ++k){
                    double prob= p.getIndividual(k).fitness() / currentPopulation;
                    entrophy -= prob * Math.log(prob);
                }
                entrophy = entrophy / Math.log(2);
                if (entrophy < entrophyStep){
                    entrophy_mul = 0;
                } else if (entrophy < entrophyStep * 2){
                    entrophy_mul = 1;
                } else {
                    entrophy_mul = 2;
                }
                if (i < 800){
                    time_mul = 0;
                } else if (i < 1600){
                    time_mul = 1;
                } else if (i < 3200){
                    time_mul = 2;
                } else {
                    time_mul = 3;
                }

                currentPopulation = initial_fitness / currentPopulation;

                if (currentPopulation < 0.3){
                    fitness_mul = 0;
                } else if (currentPopulation < 0.4){
                    fitness_mul = 1;
                } else if (currentPopulation < 0.6){
                    fitness_mul = 2;
                } else {
                    fitness_mul = 3;
                }

                int newState = entrophy_mul + 3 * fitness_mul + 12 * time_mul;
                double cur = states[state].getQ(states[state].getChoosen());
                double maxQ = states[newState].getMaxQ();
                states[state].setQ(states[state].getChoosen(), cur + ALPHA * (reward + GAMMA * maxQ - cur));
                state = newState;
            }

            for (int i = 0; i < STATES_NUMBER; ++i){
                states[i].countProbabilites();
            }

            System.out.println("round " + round + " fittest " + p.getFittest());
            middle_sum += p.getFittest();

            p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
            crossoverOperator.setPopulation(p);
            mutationOperator.setPopulation(p);
            hybridOperator.setPopulation(p);

        }
        System.out.println(middle_sum / 150);

    }

}
