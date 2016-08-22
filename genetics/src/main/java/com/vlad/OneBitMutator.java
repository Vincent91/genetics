package com.vlad;

/**
 * Created by Ladislao on 22/08/2016.
 */
public class OneBitMutator implements Mutator<SimpleBinaryTask> {

    private double threshold = 0.05;

    public OneBitMutator(double threshold){
        this.threshold = threshold;
    }

    @Override
    public void mutate(SimpleBinaryTask entity) {
        if (Math.random() <= threshold) {
            int[] values = entity.getValues();
            int index = Math.abs((int)(Integer.MAX_VALUE * Math.random())) % values.length;
            values[index] = 1 - values[index];
            entity.calculateFitness();
        }
    }

}
