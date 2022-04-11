import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class mdpNode {
    int chanceNode = 0;
    int terminalNode = 0;
    int decisionNode = 0;
    double reward = 0;
    String name;
    String policy = "";
    Map<String, Double> neighbor = new HashMap<String, Double>();
    ArrayList<String> neighborName = new ArrayList<String>();

    public mdpNode(String name){
        this.name = name;
    }
}
