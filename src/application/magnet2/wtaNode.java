package application.magnet2;

/*
 * Node Class
 * A node has a public name and public list of children nodes.
 * Each children node is also a Node by itself
 */


import java.util.ArrayList;
import java.util.List;


public class wtaNode {
	public String nodeName = "Initial";
	public List<wtaNode> childNodeList;
	//public ArrayList<String> interfaceList;
	
	public wtaNode(String name){
		nodeName=name;
		childNodeList = new ArrayList<wtaNode>();
		//interfaceList = new ArrayList<String>();
	}
	
	public String toString(){
		return nodeName + ":" + childNodeList + "\n";
	}
}
