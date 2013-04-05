package genetics.hiff;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public interface Operator {

    public void mutate();

    public double getReward();

    public void setPopulation(Population p);
}
