package genetics.tsp;

import org.uncommons.maths.random.MersenneTwisterRNG;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class TspIndividual {

    private int[] path;
    private static final MersenneTwisterRNG RNG = new MersenneTwisterRNG();

    public TspIndividual(int n){
        path = new int[n];
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < n; ++i){
            list.add(i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < n; ++i){
            path[i] = list.get(i);
        }
    }

    public TspIndividual(TspIndividual i){
        path = new int[i.size()];
        for (int k = 0; k < i.size(); ++k){
            path[k] = i.getPath()[k];
        }
    }

    public long fitness(int[][] graph){
        long sum = 0;
        for (int i = 0; i < path.length - 1; ++i){
            sum += graph[path[i]][path[i + 1]];
        }
        sum += graph[path[path.length - 1]][path[0]];
        return sum;
    }

    public void setPath(int[] p){
        path = p;
    }

    public int[] getPath(){
        return path;
    }

    public int size(){
        return path.length;
    }

}
