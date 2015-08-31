package applications.magnet;

/*
 * 
 * 
 */

import java.util.ArrayList;
import java.util.List;

public class Tree {

    public String treeName;
    public List<List<Node>> nodes;
    private int previousNodeCount = 1;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Tree(String treeName, Node root) {
    	nodes= new ArrayList();
    	nodes.add(new ArrayList<Node>());
    	//nodes.get(0).add(new Node("Naser"));
    	this.treeName = treeName;
    	nodes.get(0).add(root);
    	previousNodeCount = 1;
    	}
    
    public void generateTree(List<WTAClass> interfaceList){
    	//System.out.println(String.valueOf(interfaceList.size()));
    	for (int i=0; i<interfaceList.size(); i++){
    		nodes.add(new ArrayList<Node>());
    		for (int j=0; j<((interfaceList.get(i).getGroupSeen().size())*previousNodeCount); j++){
    				Node tempnode = new Node("node_" + String.valueOf(i+1)+String.valueOf(j) + "_" + 
    						interfaceList.get(i).getGroupSeen().get(j%(interfaceList.get(i).getGroupSeen().size())));    				
    				nodes.get(i+1).add(tempnode);    				
    				nodes.get(i).get((int)(j/interfaceList.get(i).getGroupSeen().size())).childNodeList.add(tempnode);
    				//System.out.println(String.valueOf(i+1)+ "-" + String.valueOf(j) + "=>" + nodes.get(i+1).get(j).nodeName);
    		}
    		previousNodeCount = previousNodeCount*interfaceList.get(i).getGroupSeen().size();
    	}
    }
}
