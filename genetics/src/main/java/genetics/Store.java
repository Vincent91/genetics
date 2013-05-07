package genetics;

import genetics.hiff.Operator;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class Store {

    private List<Operator> operators;

    private List<Double> q;

    private List<Double> p;

    private double betha;

    private MersenneTwisterRNG rng;

    private int choosen = 0;

    public Store(MersenneTwisterRNG r){
        operators = new ArrayList<Operator>();
        q = new ArrayList<Double>();
        p = new ArrayList<Double>();
        betha = 0.05;
        rng = r;
    }

    public void addOperator(Operator o){
        operators.add(o);
        q.add(0.0);
        p.add(0.0);
    }

    public void initProbabilities(){
        for (int i = 0; i < p.size(); ++i){
            p.set(i, (1.0 / p.size()));
        }
    }

    public void countProbabilites(){
        double sum = 0;
        for (int i = 0; i < q.size(); ++i){
            sum += Math.exp(q.get(i) * betha);
        }
        for (int i = 0; i < q.size(); ++i){
            p.set(i, Math.exp(q.get(i) * betha) / sum);
        }
    }

    public void setQ(int i, double v){
        q.set(i, v);
    }

    public void setBetha(double b){
        betha = b;
    }

    public List<Operator> getOperators(){
        return operators;
    }

    public Operator chooseOperator(){
        double r = rng.nextDouble();
        double left = 0;
        double right;
        Operator o = null;
        for (int i = 0; i < operators.size(); ++i){
            right = left + p.get(i);
            o = operators.get(i);
            choosen = i;
            if (r > left && r < right){
                break;
            }
            left = right;
        }
        return o;
    }

    public int getChoosen(){
        return choosen;
    }

    public double getMaxQ(){
        double max = q.get(0);
        for (int i = 1; i < q.size(); ++i){
            if (q.get(i) > max){
                max = q.get(i);
            }
        }
        return max;
    }

    public double getQ(int i){
        return q.get(i);
    }

}
