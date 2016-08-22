package com.vlad;

/**
 * Created by Ladislao on 22/08/2016.
 */
public class EachBitMutator implements Mutator<SimpleBinaryTask> {

    private double threshold = 0.05;

    public EachBitMutator(double threshold){
        this.threshold = threshold;
    }

    @Override
    public void mutate(SimpleBinaryTask entity) {
        int[] values = entity.getValues();
        for (int i = 0; i < values.length; ++i) {
            if (Math.random() <= threshold) {
                values[i] = 1 - values[i];
            }
        }
        entity.calculateFitness();
    }

}
