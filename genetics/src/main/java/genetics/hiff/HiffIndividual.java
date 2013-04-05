package genetics.hiff;

import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Влад
 * Date: 05.04.13
 * Time: 1:33
 * To change this template use File | Settings | File Templates.
 */
public class HiffIndividual {

    private List<Integer> block;
    private static final MersenneTwisterRNG RNG = new MersenneTwisterRNG();
    
    public HiffIndividual(List<Integer> array){
        block = array;
    }

    public HiffIndividual(int n){
        block = new ArrayList<Integer>();
        for (int i = 0; i < n; ++i){
            boolean a = RNG.nextBoolean();
            if (a){
                block.add(1);
            } else {
                block.add(0);
            }
        }
    }

    public HiffIndividual(HiffIndividual i){
        block = new ArrayList<Integer>();
        for (int k = 0; k < i.getSize(); ++k){
            block.add(i.getBlock().get(k));
        }
    }

    public int getSize(){
        return block.size();
    }
    
    public List<Integer> getBlock(){
        return block;
    }
    
    public void setBlock(List<Integer> array){
        block = array;
    }
    
    public double fitness(){
        return sum(0, block.size() - 1);
    }

    private double sum(int left, int right){
        if (left == right){
            return 1;
        }
        double temp = 0;
        temp += sum(left, (right - left) / 2 + left);
        temp += sum(left + (right - left) / 2 + 1, right);
        for (int i = left + 1; i <= right; ++i){
            if (block.get(i) != block.get(i - 1)){
                return temp;
            }
        }
        return ((right - left) + 1 + temp);
    }
    
    public void inverse(int i){
        block.set(i, 1 - block.get(i));
    }

    public void set(int i, int a){
        block.set(i, a);
    }

    public String toString(){
        return (block.toString());
    }
    
}
