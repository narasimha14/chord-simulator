package edu.clemson.chord;

import java.util.Hashtable;
/**
 * This is the hashtable that contains all the key value pairs 
 * of all the nodes present in the Chord network
 * @author Narasimha
 *
 */
public class NodeList {
	Hashtable<Integer, String> nodeList = new Hashtable<Integer, String>();
	/**
	 * Adds the @param node and its ID @param id to the nodelist
	 * @param node
	 * @param id
	 */
	public  void addToList(String node, int id){
		nodeList.put(id, node);
	}
	/**
	 * Returns the nodelist
	 * @return
	 */
	public  Hashtable<Integer, String> getList(){
		return nodeList;
	}
	/**
	 * Checks if the nodelist contains key @param id 
	 * @param id
	 * @return
	 */
	public  boolean containsKey(int id){
		return nodeList.containsKey(id);
	}
	/**
	 * Checks if nodelist contains value @param ip
	 * @param ip
	 * @return
	 */
	public  boolean containsValue(String ip){
		return nodeList.containsValue(ip);
	}
	/**
	 * Checks if the nodelist is empty
	 * @return
	 */
	public boolean isEmpty(){
		return nodeList.isEmpty();
	}
	/**
	 * Returns the first element in the nodelist
	 * @return
	 */
	public int getNextElement(){
		return nodeList.keys().nextElement();
	}
	/**
	 * Return the IP address of the @param id
	 * @param id
	 * @return
	 */
	public  String getIp(int id){
		return nodeList.get(id).toString();
	}
	/**
	 * Removes the key value pair of @param id from the nodelist
	 * @param id
	 */
	public  void removeKey(int id){
		nodeList.remove(id);
	}
}
