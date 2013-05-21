package genetics;

import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class Hat {


    private static final MersenneTwisterRNG rng = new MersenneTwisterRNG();

    private static final int POPULATION_SIZE = 10;
    private static final int INDIVIDUAL_SIZE = 64;

    private static final double ALPHA = 0.2;
    private static final double GAMMA = 0.4;
    private static final double BETHA = 25;

    private static final int STEPS = 10000;

    private static final double OPTIMUM = 448;

    public static void main(String[] args) throws FileNotFoundException {
        Population p = new Population(POPULATION_SIZE, INDIVIDUAL_SIZE);
        CAMO operatorAMO = new CAMO(p, rng);
        CMO  operatorMO = new CMO(p, rng);
        CCO operatorCCO = new CCO(p, rng);
        for (int i = 0; i < STEPS; ++i){
            double best = p.getFittest();
            System.out.println(i + " " + best);
            if (best == OPTIMUM){
                break;
            }
            double r = rng.nextDouble();
            if (r < 0.33){
                operatorAMO.mutate();
            } else if (r < 0.66){
                operatorMO.mutate();
            } else {
                operatorCCO.mutate();
            }
        }
    }
}
