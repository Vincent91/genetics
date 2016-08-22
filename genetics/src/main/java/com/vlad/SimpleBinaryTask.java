package com.vlad;

/**
 * Created by Ladislao on 22/08/2016.
 */
public class SimpleBinaryTask implements FitnessEntity {

    private int[] values;

    private long fitness;

    public SimpleBinaryTask(int length) {
        values = new int[length];
        for (int i = 0; i < length; ++i){
            values[i] = Math.abs((int)(Integer.MAX_VALUE * Math.random()));
        }
        calculateFitness();
    }

    @Override
    public void calculateFitness(){
        int ones = 0;
        int zeroes = 0;
        for (int i = 0; i < values.length; ++i){
            if (values[i] == 0){
                zeroes++;
            } else {
                ones++;
            }
        }
        fitness = Math.max(ones, zeroes);
    }

    @Override
    public long getFitness() {
        return fitness;
    }

    public int[] getValues(){
        return values;
    }

}
