package genetics;

import genetics.hiff.HiffIndividual;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Влад
 * Date: 05.04.13
 * Time: 1:32
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static final int SIZE = 8;
    public static final double PROBABILITY = 0.1;

    public static void main(String[] args){
        MersenneTwisterRNG random = new MersenneTwisterRNG();
        ArrayList<HiffIndividual> list = new ArrayList<HiffIndividual>();
        for (int i = 0; i < SIZE; ++i){
            list.add(new HiffIndividual(SIZE));
        }
        System.out.println("Initial population");
        for (int i = 0; i <SIZE; ++i){
            System.out.println(list.get(i) + " " + list.get(i).fitness());
        }
        for (int i = 0; i < 100; ++i){
            double sum = 0;
            for (int k = 0; k < SIZE; ++k){
                sum = sum + list.get(k).fitness();
            }
            double start = 0;
            double r = random.nextDouble() * sum;
            for (int k = 0; k < SIZE; ++k){
                if (r >= start && r <= (start + list.get(k).fitness())){
                    HiffIndividual mutant = new HiffIndividual(list.get(k));
                    System.out.println("to " + mutant + " " + mutant.fitness());
                    for (int z = 0; z < mutant.getSize(); ++z){
                        r = random.nextDouble();
                        if (r < PROBABILITY){
                            mutant.inverse(z);
                            break;
                        }
                    }
                    System.out.println("do " + mutant + " " + mutant.fitness());
                    double min = list.get(0).fitness(); 
                    int pos = 0;
                    for (int z = 1; z < SIZE; ++z){
                        double f = list.get(z).fitness();
                        if (f < min){
                            min = f;
                            pos = z;
                        }
                    }
                    list.set(pos, mutant);
                    break;
                } else {
                    start += list.get(k).fitness();
                }
            }
        }
        System.out.println("final population");
        for (int i = 0; i < SIZE; ++i){
            System.out.println(list.get(i) + " " + list.get(i).fitness());
        }
    }
}
