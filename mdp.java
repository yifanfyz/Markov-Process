import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class mdp {
    
    public static void mdpSolver(ArrayList<mdpNode> mdp, double df, int min, double tol, int iter){
        ArrayList<Double> valueTable = new ArrayList<Double>();
        for(mdpNode a: mdp){
            valueTable.add(a.reward);
        }
        ArrayList<mdpNode> currentMdp = new ArrayList<mdpNode>();
        for(int i=0; i<mdp.size(); i++){
            currentMdp.add(mdp.get(i));
        }
        while(true){
            ArrayList<Double> newValueTable = valueIteration(currentMdp, valueTable, df, tol, iter);
            ArrayList<mdpNode> newMdp = greedyPolicyComputation(currentMdp, newValueTable, min);
            if(reachMdpTol(valueTable,newValueTable, tol) && samePolicy(currentMdp,newMdp)){
                printMdpResult(currentMdp, valueTable);
                System.exit(1);
            }
            currentMdp = newMdp;
            valueTable = newValueTable;
        } 
    }

    public static Boolean samePolicy(ArrayList<mdpNode> first, ArrayList<mdpNode> second){
        for(int i=0; i<first.size(); i++){
            if(!first.get(i).policy.equals(second.get(i).policy)){
                return false;
            }
        }
        return true;
    }
    
    public static ArrayList<mdpNode> greedyPolicyComputation(ArrayList<mdpNode> mdp, ArrayList<Double> valueTable, int min){    
        ArrayList<mdpNode> newMdp = new ArrayList<mdpNode>();
        for(int i=0; i<mdp.size(); i++){
            newMdp.add(mdp.get(i));
        }
        for(int i=0; i<newMdp.size(); i++){
            if(newMdp.get(i).decisionNode==1){
                int targetNeighborIndex = 0;
                String targetNeighborName = "";
                double maxP = Collections.max(newMdp.get(i).neighbor.values());
                if(min==0){
                    double maxTableValue = valueTable.get(checkMdpNodeIndex(newMdp.get(i).neighborName.get(0), newMdp));
                    for(int j=0; j<newMdp.get(i).neighborName.size(); j++){
                        double currentTableValue = valueTable.get(checkMdpNodeIndex(newMdp.get(i).neighborName.get(j), newMdp));
                        if(currentTableValue>=maxTableValue){
                            maxTableValue = currentTableValue;
                            targetNeighborName = newMdp.get(i).neighborName.get(j);
                        }
                    }
                    newMdp.get(i).neighbor.replace(targetNeighborName, maxP); 
                }else if(min==1){
                    double minTableValue = valueTable.get(checkMdpNodeIndex(newMdp.get(i).neighborName.get(0), newMdp));
                    for(int j=0; j<newMdp.get(i).neighborName.size(); j++){
                        double currentTableValue = valueTable.get(checkMdpNodeIndex(newMdp.get(i).neighborName.get(j), newMdp));
                        if(currentTableValue<minTableValue){
                            minTableValue = currentTableValue;
                            targetNeighborIndex = j;
                        }
                    }
                    targetNeighborName = newMdp.get(i).neighborName.get(targetNeighborIndex);
                    newMdp.get(i).neighbor.replace(targetNeighborName, maxP); 
                }
                for(int j=0; j<newMdp.get(i).neighborName.size(); j++){
                    if(!newMdp.get(i).neighborName.get(j).equals(targetNeighborName)){
                        double remainedP = (1-maxP)/(newMdp.get(i).neighborName.size()-1);
                        newMdp.get(i).neighbor.replace(newMdp.get(i).neighborName.get(j), remainedP);
                    }
                }
                newMdp.get(i).policy = targetNeighborName;
            }
        }
        return newMdp;
    }

    public static ArrayList<Double> valueIteration(ArrayList<mdpNode> mdp, ArrayList<Double> oldValueTable, double df, double tol, int iter){
        ArrayList<Double> valueTable = new ArrayList<Double>();
        for(int i=0; i<oldValueTable.size(); i++){
            valueTable.add(oldValueTable.get(i));
        }
        for(int i=0; i<iter; i++){
            ArrayList<Double> newValueTable = new ArrayList<Double>();
            for(int j=0; j<mdp.size(); j++){
                mdpNode node = mdp.get(j);
                double tableValue = node.reward;
                for(int k=0; k<node.neighborName.size(); k++){
                    mdpNode neighborNode = mdp.get(checkMdpNodeIndex(node.neighborName.get(k), mdp));
                    double neighborTableValue = valueTable.get(checkMdpNodeIndex(node.neighborName.get(k), mdp));
                    tableValue = tableValue+ df*node.neighbor.get(neighborNode.name)*neighborTableValue;
                }
                newValueTable.add(tableValue);
            }
            if(reachMdpTol(valueTable, newValueTable, tol)){
                return newValueTable;
            }
            valueTable = newValueTable;
        }
        return valueTable;
    }

    public static boolean reachMdpTol(ArrayList<Double> valueTable, ArrayList<Double> newValueTable, double tol){
        for(int i=0; i<valueTable.size(); i++){
            if(Math.abs(valueTable.get(i)-newValueTable.get(i))>tol){
                return false;
            }
        }
        return true;
    }

    public static void printMdpResult(ArrayList<mdpNode> mdp, ArrayList<Double> valueTable){
        for(int i=0; i<mdp.size(); i++){
            if(mdp.get(i).decisionNode==1 && mdp.get(i).neighbor.size()>1){
                System.out.println(mdp.get(i).name+"->"+mdp.get(i).policy);
            }
        }
        System.out.println();
        for(int i=0; i<mdp.size(); i++){
            System.out.print(mdp.get(i).name+"="+valueTable.get(i)+" ");
        }
        System.out.println();
    }

    public static int checkMdpNodeIndex(String name, ArrayList<mdpNode> mdp){
        for(int i=0; i<mdp.size(); i++){
            if(mdp.get(i).name.equals(name)){
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<mdpNode>buildMDP(ArrayList<String> input, int min){
        ArrayList<mdpNode> mdp = new ArrayList<mdpNode>();
        for(String a: input){
            if(a.contains("=")){
                int index = a.indexOf("=");
                String name = a.substring(0, index);
                int reward = Integer.valueOf(a.substring(index+1));
                if(checkMdpNodeIndex(name, mdp)>=0){
                    mdp.get(checkMdpNodeIndex(name, mdp)).reward = reward;
                }else{
                    mdpNode node = new mdpNode(name);
                    node.reward = reward;
                    mdp.add(node);
                }
            }
        }
        for(String a: input){
            if(a.contains(":")){
                int firstSpaceIndex = a.indexOf(" ");
                String name = a.substring(0, firstSpaceIndex);
                if(name.charAt(name.length()-1) == ':'){
                    name = name.substring(0, name.length()-1);
                }
                if(checkMdpNodeIndex(name, mdp)>=0){
                    int leftIndex = a.indexOf("[");
                    String stringNeighbor = a.substring(leftIndex+1);
                    String[] stringsNeighbor = stringNeighbor.split(" ");
                    for(String b: stringsNeighbor){
                        mdp.get(checkMdpNodeIndex(name, mdp)).neighbor.put(b.substring(0, b.length()-1), (double) 1/stringsNeighbor.length);
                        mdp.get(checkMdpNodeIndex(name, mdp)).neighborName.add(b.substring(0, b.length()-1));
                    }
                }else{
                    mdpNode node = new mdpNode(name);
                    int leftIndex = a.indexOf("[");
                    String stringNeighbor = a.substring(leftIndex+1);
                    String[] stringsNeighbor = stringNeighbor.split(" ");
                    for(String b: stringsNeighbor){
                        node.neighbor.put(b.substring(0, b.length()-1), (double) 1/stringsNeighbor.length);
                        node.neighborName.add(b.substring(0, b.length()-1));
                    }
                    mdp.add(node);
                }
            }
        }
        for(String a: input){
            if(a.contains("%")){
                int firstSpaceIndex = a.indexOf(" ");
                String name = a.substring(0, firstSpaceIndex);
                String[] stringsProbability = a.split(" ");
                String[] modifiedStringsProbability = null;
                if(name.charAt(name.length()-1) == '%'){
                    name = name.substring(0, name.length()-1);
                    modifiedStringsProbability = new String[stringsProbability.length-1];
                    for(int i = 0; i<modifiedStringsProbability.length; i++){
                        modifiedStringsProbability[i] = stringsProbability[i+1];
                    }
                }else{
                    modifiedStringsProbability = new String[stringsProbability.length-2];
                    for(int i = 0; i<modifiedStringsProbability.length; i++){
                        modifiedStringsProbability[i] = stringsProbability[i+2];
                    }
                }
                if(modifiedStringsProbability.length==1){
                    double probability = Double.valueOf(modifiedStringsProbability[0]);
                    mdpNode node = null;
                    if(checkMdpNodeIndex(name, mdp)>=0){
                        node = mdp.get(checkMdpNodeIndex(name, mdp));
                        node.decisionNode = 1;
                        String policyNodeName = findPolicyNode(node, mdp, min);
                        node.policy = policyNodeName;
                        node.neighbor.replace(policyNodeName, probability);
                        int remainedNeighbor = node.neighborName.size()-1;
                        double remainedProbability = (1-probability)/remainedNeighbor;
                        for(String s: node.neighborName){
                            if(!s.equals(policyNodeName)){
                                node.neighbor.replace(s, remainedProbability);
                            }
                        }
                    }
                }else if(modifiedStringsProbability.length > 1){
                    mdpNode node = null;                   
                    if(checkMdpNodeIndex(name, mdp)>=0){
                        node = mdp.get(checkMdpNodeIndex(name, mdp));
                        node.chanceNode = 1;
                        for(int i=0; i<node.neighborName.size(); i++){
                            node.neighbor.replace(node.neighborName.get(i), Double.valueOf(modifiedStringsProbability[i]));
                        }
                    }
                }
            }
        }
        for(mdpNode node: mdp){
            if(node.neighborName.size()==0){
                node.terminalNode = 1;
            }
            if(node.neighborName.size()!=0 && node.chanceNode == 0 && node.terminalNode==0 && node.decisionNode == 0){
                node.decisionNode = 1;
                String policyNodeName = findPolicyNode(node, mdp, min);
                node.policy = policyNodeName;
                node.neighbor.replace(policyNodeName, (double)1);
                for(String s: node.neighborName){
                    if(!s.equals(policyNodeName)){
                        node.neighbor.replace(s, (double)0);
                    }
                }
            }
            if(node.chanceNode == 1){
                double sum = 0;
                for(int n=0; n<node.neighborName.size(); n++){
                    sum = sum + (double)node.neighbor.get(node.neighborName.get(n));
                }
                if(sum!=1){
                    return null;
                }
            }
        }
        Comparator<mdpNode> compareByName = new Comparator<mdpNode>() {
            @Override
            public int compare(mdpNode o1, mdpNode o2) {
                return o1.name.compareTo(o2.name);
            }
        };
        Collections.sort(mdp, compareByName);
        return mdp;
    }

    public static String findPolicyNode(mdpNode node, ArrayList<mdpNode> mdp, int min){
        double reward = mdp.get(checkMdpNodeIndex(node.neighborName.get(0), mdp)).reward;
        String targetNodeName = node.neighborName.get(0);
        for(String neighborName: node.neighborName){
            mdpNode neighbor = mdp.get(checkMdpNodeIndex(neighborName, mdp));
            if(min==0){
                if(reward<=neighbor.reward){
                    reward = neighbor.reward;
                    targetNodeName = neighbor.name;
                }
            }else if(min==1){
                if(reward>=neighbor.reward){
                    reward = neighbor.reward;
                    targetNodeName = neighbor.name;
                }
            }
        }
        return targetNodeName;
    }

    public static ArrayList<String> readFile(String[] args) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(args[args.length-1]));
        ArrayList<String> fileContent = new ArrayList<String>();
        try{
            String line = reader.readLine();
            while (line != null) {
                if(!(line.contains("#")|| line.isBlank())){
                    fileContent.add(line);
                }
                line = reader.readLine();
            }
        }finally{
            reader.close();
        }
        Collections.sort(fileContent, Collections.reverseOrder());
        return fileContent;
    }  

    public static void checkMdpProbabilitySum(ArrayList<mdpNode> mdp){
        if(mdp == null){
            System.out.println("input file error: Chance node sum of probabiliy is not 1");
            System.exit(0);
        }
    }

    public static void main(String[] args) throws IOException{
        ArrayList<String> input = readFile(args);
        double df = 1.0;
        int min = 0;
        double tol = 0.01;
        int iter = 100;
        for(int i=0; i<args.length; i++){
            if(args[i].equals("-df")){
                df = Double.valueOf(args[i+1]);
            }
            else if(args[i].equals("-tol")){
                tol = Double.valueOf(args[i+1]);
            }
            else if(args[i].equals("-min")){
                min = 1;
            }
            else if(args[i].equals("-iter")){
                iter = Integer.valueOf(args[i+1]);
            }
        }
        ArrayList<mdpNode> mdp = buildMDP(input, min);
        checkMdpProbabilitySum(mdp);
        mdpSolver(mdp, df, min, tol, iter);
    }
}