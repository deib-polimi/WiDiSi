package applications.magnet;

/*
 * Node Class
 * A node has a public name and public list of children nodes.
 * Each children node is also a Node by itself
 */


import java.util.ArrayList;
import java.util.List;


public class Node {
	public String nodeName = "Initial";
	public List<Node> childNodeList;
	//public ArrayList<String> interfaceList;
	
	public Node(String name){
		nodeName=name;
		childNodeList = new ArrayList<Node>();
		//interfaceList = new ArrayList<String>();
	}
	
	public String toString(){
		return nodeName + ":" + childNodeList + "\n";
	}
}
