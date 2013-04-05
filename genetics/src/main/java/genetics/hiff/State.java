package genetics.hiff;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class State {

    private Operator operator;
    private int destination;

    public State(Operator o, int d){
        operator = o;
        destination = d;
    }

    public Operator getOperator(){
        return operator;
    }

    public int getDestination(){
        return destination;
    }

}
