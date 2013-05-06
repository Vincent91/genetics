package genetics.hiff;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class Population {

    List<HiffIndividual> population;

    public Population(){
        population = new ArrayList<HiffIndividual>();
    }

    public Population(int n, int m){
        population = new ArrayList<HiffIndividual>();
        for (int i = 0; i < n; ++i){
            population.add(new HiffIndividual(m));
        }
    }

    public Population(Population p){
        population = new ArrayList<HiffIndividual>();
        for (HiffIndividual i : p.getPopulation()){
            population.add(new HiffIndividual(i));
        }
    }

    public HiffIndividual getIndividual(int i){
        if (i >= population.size()){
            return null;
        }
        return population.get(i);
    }

    public void setIndividual(int i, HiffIndividual e){
        population.set(i, e);
    }

    public List<HiffIndividual> getPopulation(){
        return  population;
    }

    public int getSize(){
        return population.size();
    }

    public double getFittest(){
        double max = population.get(0).fitness();
        for (int i = 1; i < population.size(); ++i){
            double temp = population.get(i).fitness();
            if (temp > max){
                max = temp;
            }
        }
        return max;
    }

    public void addIndividual(HiffIndividual h){
        population.add(h);
    }

}
