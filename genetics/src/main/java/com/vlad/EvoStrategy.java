package com.vlad;

/**
 * Created by Ladislao on 22/08/2016.
 */
public abstract class EvoStrategy {

    private int mutants;

    public EvoStrategy(int mutants) {
        this.mutants = mutants;
    }

    public abstract void evolve();

}
