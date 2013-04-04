package genetics;

import genetics.hiff.HiffIndividual;

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

    public static void main(String[] args){
        ArrayList<HiffIndividual> list = new ArrayList<HiffIndividual>();
        for (int i = 0; i < SIZE; ++i){
            list.add(new HiffIndividual(SIZE));
        }
        ArrayList<Integer> test = new ArrayList<Integer>();
        list.add(new HiffIndividual(test));
        for (int i = 0; i < SIZE; ++i){
            System.out.println(list.get(i) + " " + list.get(i).fitness());
        }
    }
}
