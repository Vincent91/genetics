package genetics.tsp;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public interface Operator {

    public void mutate();

    public double getReward();

    public void setPopulation(TspPopulation p);

    public TspPopulation getPopulation();
}
