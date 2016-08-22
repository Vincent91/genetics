package com.vlad;

/**
 * Created by Ladislao on 22/08/2016.
 */
public class OnePointCrossover implements Crossover<SimpleBinaryTask> {

    @Override
    public void cross(SimpleBinaryTask left, SimpleBinaryTask right){
        int[] leftValues = left.getValues();
        int[] rightValues = right.getValues();
        int length = leftValues.length;
        int point = Math.abs(((int)(Integer.MAX_VALUE * Math.random()))) % length;
        for (int i = 0; i < point; ++i) {
            int temp = leftValues[i];
            leftValues[i] = rightValues[i];
            rightValues[i] = temp;
        }
    }

}
