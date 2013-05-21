package genetics.tsp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class TspPopulation {

    private List<TspIndividual> population;
    private int populationSize;
    private int individualSize;

    private int[][] graph;

    public TspPopulation(){
        population = new ArrayList<TspIndividual>();
    }

    public TspPopulation(int n, int m, int[][] graph){
        population = new ArrayList<TspIndividual>();
        for (int i = 0; i < n; ++i){
            population.add(new TspIndividual(m));
        }
        this.graph = graph;
    }

    public TspPopulation(TspPopulation p){
        population = new ArrayList<TspIndividual>();
        for (TspIndividual i : p.getPopulation()){
            population.add(new TspIndividual(i));
        }
        graph = p.getGraph();
    }

    public long getFittest(){
        long best = Long.MAX_VALUE;
        for (int i = 0; i < population.size(); ++i){
            long temp = population.get(i).fitness(graph);
            if (temp < best){
                best = temp;
            }
        }
        return best;
    }

    public List<TspIndividual> getPopulation(){
        return population;
    }

    public void setPopulation(List<TspIndividual> p){
        population = p;
    }

    public TspIndividual getIndividual(int i){
        if (population.size() < i + 1){
            return null;
        } else {
            return population.get(i);
        }
    }

    public void setIndividual(int i, TspIndividual t){
        population.set(i, t);
    }

    public int[][] getGraph(){
        return graph;
    }

    public void setGraph(int[][] g){
        graph = g;
    }

    public int getSize(){
        return population.size();
    }

    public void addIndividual(TspIndividual i){
        population.add(i);
    }

}
