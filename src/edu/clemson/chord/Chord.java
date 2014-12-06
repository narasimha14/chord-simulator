package edu.clemson.chord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Has all the definitions required for Chord algorithm implementation
 * @author Narasimha
 *
 */
public class Chord extends NodeList{

	
	int number_of_entries ;
	int size ;
	int[][] finger ;
	String[][] key ;
	int[] count;
	
	/**
	 * Constructor
	 * @param numberOfEntries
	 */
	public Chord(int numberOfEntries){
		 number_of_entries = 6;
		 size = (int) Math.pow(2, number_of_entries);
		 finger = new int[size][number_of_entries];
		 key = new String [size][size];
		 count = new int [size];
	}

	/**
	 * Creates a Chord network with @param node as the root node
	 * @param node
	 */
	public  void create(String node){
		int id = HashFunction.getHash(node);
		addToList(node, id);
		initFingerTable(id);
	}

	/**
	 * Initializes the finger table of @param id
	 * @param id
	 */
	private  void initFingerTable(int id) {
		for(int i=1; i <= number_of_entries ;++i){
			finger[id][i-1] = (int) ((id + Math.pow(2, i-1))%size);
		}

	}

	/**
	 * Clears the finger table entries of @param id
	 * @param id
	 */
	private void clearFingerTable(int id) {
		for(int i=0; i< number_of_entries ;++i){
			finger[id][i] = -1;
		}

	}
	/**
	 * Joins the @param node1 to @param node2
	 * @param node1
	 * @param node2
	 */
	public  void join(String node1, String node2) {
		int id = HashFunction.getHash(node1);
		if(containsKey(id)){
			System.out.println("Node with the same IP is already present");
		}else if(containsValue(node2)){
			System.out.println("Joining "+ node1 +" to the Chord network with ID "+ id);
			int successorNode = findSuccessor(id);
			addToList(node1, id);
			initFingerTable(id);
			transferKeysFrom(successorNode, id);
		}else{
			System.out.println(node2 + " is not present in this Chord network");
		}

	}
	/**
	 * Unjoins the @param node from the Chord network
	 * @param node
	 */
	public  void leave(String node){
		int id = HashFunction.getHash(node);
		if(containsValue(node)){
			System.out.println("Removing "+node+ " from the Chord network");
			if(findSuccessor(finger[id][0]) != id){
				if(count[id] > 0){
					int nodeToTransfer = findSuccessor(finger[id][0]);
					System.out.println("Transferring keys to Node "+ nodeToTransfer);
					transferKeys(id, nodeToTransfer);
				}

			}else{
				System.out.println("That was the last node in the Chord network");
			}
			removeKey(id);
			clearFingerTable(id);
			clearKeys(id);
		}else{
			System.out.println("No such node to remove");
		}
	}
	/**
	 * Clears the keys in the @param id
	 * @param id
	 */
	private void clearKeys(int id) {
		for (int i=0 ; i< count[id]; ++i){
			key[id][i] = null;
			count[id] = 0;
		}

	}

	/**
	 * Tranfers keys from @param id to @param successor
	 * @param id
	 * @param successor
	 */
	private void transferKeys(int id, int successor) {
		System.out.println("count[successor] is " + count[successor] + " count[id] is " + count[id]);
		for(int i=0; i<count[id];++i){
			key[successor][count[successor]] = key[id][i];
			incrementCount(successor);
		}

	}
	/**
	 * Finds the successor of @param id	
	 * @param id
	 * @return
	 */
	public  int findSuccessor(int id){
		for(int i=id; i < id+size ;++i){
			if(containsKey(i%size)){
				return i%size;
			}
		}
		return -1;
	}
	/**
	 * Displays the finger table of @param node
	 * @param node
	 */
	public void show(String node) {
		int id = HashFunction.getHash(node);
		int successorNode ;
		if(containsValue(node)){
			System.out.println("Finger Table of node \""+ node +"\" whose ID is "+ id + " is as below \n");
			System.out.println("| Finger Nodes | Successor Node | IP address       |");
			for(int i=0; i< number_of_entries ;++i){
				successorNode = findSuccessor(finger[id][i]);
				createTable(finger[id][i], successorNode,getIp(successorNode));
			}
		}
		else{
			System.out.println("No such node in this Chord Network");
		}
	}
	/**
	 * Displays the finger table entries
	 * @param fingerNode
	 * @param successorNode
	 * @param ip
	 */
	private void createTable(int fingerNode, int successorNode, String ip) {
		int firstAppend = 13 - String.valueOf(fingerNode).length();
		int secondAppend = 15 - String.valueOf(successorNode).length();
		int thirdAppend = 17 - String.valueOf(ip).length();

		String toPrint = "| "+fingerNode;
		for(int i=0; i<firstAppend ;++i)
			toPrint += " ";
		toPrint += "| "+successorNode;
		for(int i=0; i<secondAppend;++i)
			toPrint += " ";
		toPrint += "| " + ip;
		for(int i=0; i<thirdAppend;++i)
			toPrint += " ";

		toPrint += "| ";

		System.out.println(toPrint);

	}
	/**
	 * Returns the path followed to go from @param node to @param key
	 * @param key
	 * @param knode
	 */
	public void path(String key, String node){
		int keyId = HashFunction.getHash(key);
		int rootId = HashFunction.getHash(node);
		if(!containsKey(keyId) || !containsKey(rootId)){
			System.out.println("Sorry. No such nodes in this Chord network");
		}else{
			calculatePath( key, keyId, rootId);
		}

	}

	/**
	 * Returns the path followed to go from @param rootId to @param keyId
	 * @param key
	 * @param keyId
	 * @param rootId
	 */
	private void calculatePath( String key, int keyId, int rootId) {
		boolean found = false;
		for(int i =0; i < number_of_entries;++i){
			if(findSuccessor(finger[rootId][i]) == keyId){
				System.out.println(" ---> " + "Node " + findSuccessor(finger[rootId][i]));
				found = true;
				break;
			}
		}
		if(!found){
			int nextNode = findClosestPreceedingFinger(keyId, rootId);
			System.out.print(" ---> "+ "Node " + findSuccessor(nextNode));
			calculatePath(key, keyId, findSuccessor(nextNode));
		}
	}

	/**
	 * Calculates the path length between @param rootId and @param keyId
	 * @param keyId
	 * @param rootId
	 * @param count
	 * @return
	 */
	private int calculatePathCount( int keyId, int rootId, int count) {
		boolean found = false;
		int countN = count;

		for(int i =0; i < number_of_entries;++i){
			if(findSuccessor(finger[rootId][i]) == keyId){
				System.out.println(" ---> " + "Node " + findSuccessor(finger[rootId][i]));
				found = true;
				break;
			}
		}
		if(!found){
			int nextNode = findClosestPreceedingFinger(keyId, rootId);
			++countN;
			System.out.print(" ---> "+ "Node " + findSuccessor(nextNode));
			countN = calculatePathCount(keyId, findSuccessor(nextNode), countN);
		}
		return countN;
	}
	/**
	 * returns the closest preceeding finger to the @param keyId
	 * @param keyId
	 * @param rootId
	 * @return
	 */
	private int findClosestPreceedingFinger(int keyId, int rootId) {

		for(int i=number_of_entries-1 ; i >= 0 ; --i ){
			if(isInRange(finger[rootId][i], rootId, keyId)){
				return finger[rootId][i];
			}
		}
		return rootId;
	}

	/**
	 * Checks whether @param finger belongs to the range ( @param rootId, @param keyId)
	 * @param finger
	 * @param rootId
	 * @param keyId
	 * @return
	 */
	private boolean isInRange(int finger, int rootId, int keyId){

		if(rootId < keyId){
			if(finger > rootId && finger <= keyId)
				return true;
			else
				return false;
		}else{
			if(finger > rootId){
				if(finger > rootId && finger <= (keyId + size-1)){
					return true;
				}else
					return false;
			}else{
				if(finger+size-1 > rootId && finger <= keyId){
					return true;
				}else
					return false;
			}
		}

	}

	/**
	 * Takes the hashed value of the @param resource and stores it appropriately
	 * @param resource
	 */
	public void store(String resource) {
		int hashResource = HashFunction.getHash(resource);
		int nodeToStore = -1;
		if((nodeToStore = findSuccessor(hashResource)) >= 0){
			if(count[nodeToStore] < number_of_entries){
				if(! contains(nodeToStore,resource)){
					key[nodeToStore][count[nodeToStore]] = resource;
					incrementCount(nodeToStore);
					System.out.println(" Resource "+ resource + " with id "+hashResource+" stored in node " + nodeToStore);
				}else{
					System.out.println("A resource by the same name is already present");
				}
			}

		}else{
			nodeToStore = findNodeToStore(hashResource, getLocalId());
			if(count[nodeToStore] < number_of_entries){
				if(! contains(nodeToStore,resource)){
					key[nodeToStore][count[nodeToStore]] = resource;
					incrementCount(nodeToStore);
					System.out.println(" Resource "+ resource + " with id "+hashResource+" stored in node " + nodeToStore);
				}else{
					System.out.println("A resource by the same name is already present");
				}
			}
		}


	}
	/**
	 * Checks whether @param id contains the @param resource or not
	 * @param id
	 * @param resource
	 * @return
	 */
	public boolean contains(int id, String resource){
		for(int i=0 ; i < count[id]; ++i){
			if(key[id][i].equals(resource)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Increments the key count in @param node
	 * @param node
	 */
	private void incrementCount(int node) {
		++count[node];
	}

	/**
	 * Decrements the key count in the @param node
	 * @param node
	 */
	private void decrementCount(int node) {
		--count[node];
	}

	/**
	 * Returns the first element in the Node List
	 * @return
	 */
	private int getLocalId() {

		return getNextElement();
	}

	/**
	 * Transfers the keys from @param toTransferFrom to id
	 * @param toTransferFrom
	 * @param id
	 */
	public void transferKeysFrom(int toTransferFrom, int id){

		for(int i=0; i< count[toTransferFrom]; ++i){
			if(HashFunction.getHash(key[toTransferFrom][i]) == id){
				System.out.println(" Transferring from "+ getIp(toTransferFrom) + " to " + getIp(id));
				key[id][count[id]] = key[toTransferFrom][i];
				incrementCount(id);
				for(int j =i ; j< count[toTransferFrom]; ++j){
					key[toTransferFrom][j] = key[toTransferFrom][j+1];
				}
				key[count[toTransferFrom-1]] = null;
				decrementCount(toTransferFrom);
				--i;
			}
		}

	}

	/**
	 * Finds the node to transfer the keys in case the node with @param id leaves the Chord network
	 * @param id
	 */
	public void findPreviousNodeAndTransfer(int id){

		int toTransferFrom = findSuccessor(findNodeToStore(id, getLocalId()));
		for(int i=0; i< count[toTransferFrom]; ++i){
			if(HashFunction.getHash(key[toTransferFrom][i]) == id){
				System.out.println(" Transferring from "+ getIp(toTransferFrom) + " to " + getIp(id));
				key[id][count[id]] = key[toTransferFrom][i];
				incrementCount(id);
				for(int j =i ; j< count[toTransferFrom]; ++j){
					key[toTransferFrom][j] = key[toTransferFrom][j+1];
				}
				key[count[toTransferFrom-1]] = null;
				decrementCount(toTransferFrom);
				--i;
			}
		}

	}
	/**
	 * finds the node in which the @param hashResource has to be stored
	 * @param hashResource
	 * @param rootId
	 * @return
	 */
	public int findNodeToStore(int hashResource, int rootId){
		boolean found = false;
		int nextNode = rootId;
		for(int i =0; i < number_of_entries;++i){
			if(findSuccessor(finger[rootId][i]) == hashResource){

				found = true;
				break;
			}
		}
		if(!found){
			nextNode = findClosestPreceedingFinger(hashResource, rootId);

			findNodeToStore(hashResource, findSuccessor(nextNode));
		}

		return nextNode;

	}
	/**
	 * finds the node in which the @param hashResource has to be stored
	 * @param hashResource
	 * @param rootId
	 * @param counter
	 * @return
	 */
	public int[] findNodeToStore(int hashResource, int rootId , int counter){
		boolean found = false;
		int[] nextNode = new int[2];
		nextNode[0] = rootId;
		nextNode[1] = counter;
		for(int i =0; i < number_of_entries;++i){
			if(findSuccessor(finger[rootId][i]) == hashResource){

				found = true;
				break;
			}
		}
		if(!found){
			nextNode[0] = findClosestPreceedingFinger(hashResource, rootId);

			++nextNode[1];
			System.out.println("Count is "+ nextNode[1]);
			findNodeToStore(hashResource, findSuccessor(nextNode[0]), nextNode[1]);
		}

		return nextNode;

	}
	/**
	 * Retrieves the resource from the first node in the node list
	 * @param resource
	 */
	public void retrieve(String resource) {

		retrieve(resource,getIp(getLocalId()));

	}
	/**
	 * Retrieves the resource from the node
	 * @param resource
	 * @param node
	 */
	public void retrieve(String resource, String node) {
		if(!containsValue(node))
			node = getIp(findSuccessor(HashFunction.getHash(node)));
		int hashResource = HashFunction.getHash(resource);
		int hashNode = HashFunction.getHash(node);
		int pathCount = 0;
		int nodeToStore =  findSuccessor(hashResource);

		if(nodeToStore == hashNode){
			pathCount = 0;
		}else{
			pathCount = calculatePathCount(nodeToStore, hashNode, 1) ;
		}
		String valueRetrieved = findResource(nodeToStore, resource);

		if(null == valueRetrieved){
			System.out.println("No such resource found");
		}else{
			System.out.println("Retrieved value from node " + 
					nodeToStore + "\nValue retrieved is " + valueRetrieved +"\nHop count to find the file was " + pathCount);
			
			boolean writeEnabled = getWriteEnabled();
			if(writeEnabled)
				writeToFile("path_count.txt", pathCount);
		}


	}
	/**
	 * Checks if the path length need to be written to a file
	 * @return
	 */
	private boolean getWriteEnabled() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			String filename = "chord.properties";
			input = new FileInputStream(filename);
			prop.load(input);


			String isWriteEnabled = prop.getProperty("writePathLength");

			return isWriteEnabled.equalsIgnoreCase("true")?true:false;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	/**
	 * Finds the given resource if it is present in the node
	 * @param node
	 * @param resource
	 * @return
	 */
	private String findResource(int node, String resource) {
		for(int i =0; i< count[node];++i){
			if(! (null == (key[node][i]))){
				if(key[node][i].equals(resource)){
					return key[node][i];
				}
			}
		}
		return null;
	}
	/**
	 * Shows all the nodes in the Chord network along with their IP address
	 */
	public void showAll() {
		if(isEmpty()){
			System.out.println("No nodes present to show");
			return;
		}
		Hashtable<Integer, String> nodelistM = getList();
		for(Integer key : nodelistM.keySet()){
			System.out.println("IP address : " + nodelistM.get(key) + " hashed value " + key);
		}

	}
	/**
	 * Shows all the keys present in @param node
	 * @param node
	 */
	public void showKeys(String node) {
		int id = HashFunction.getHash(node);
		if(containsValue(node)){
			if(count[id] == 0){
				System.out.println("No keys stored in this node");
			}else{
				for(int i=0; i<count[id];++i){
					System.out.println(key[id][i]);
				}
			}
		}else{
			System.out.println("No such node in this network");
		}
	}
	/**
	 * Joins all the nodes defined in @param file to the root node
	 * @param chord
	 */
	public void joinMultiple(Chord chord){
		String file = "nodes_1_to_63.txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			String line;
			String root_node = null;
			try {
				root_node = br.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			chord.create(root_node);
			try {
				while ((line = br.readLine()) != null) {
					chord.join(line, root_node);
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * stores the keys defined in @param file into all the nodes
	 * @param chord
	 */
	public void storeMultiple(Chord chord) {
		String file = "keys_1_to_63.txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			String line;

			try {
				while ((line = br.readLine()) != null) {
					chord.store(line);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	/**
	 * retrieves all the keys defined in @param file to root node from all other nodes
	 * @param chord
	 */
	public void retrieveMultiple(Chord chord) {
		String file = "keys_1_to_63.txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			String line;
			String root_node = "192.168.10.63";

			try {
				while ((line = br.readLine()) != null) {
					chord.retrieve(line, root_node);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}


	public void retrieveHalf(Chord chord) {
		String file = "keys_1_to_63.txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			String line;
			String root_node = "192.168.10.63";
			int i = 0;
			try {
				while ((line = br.readLine()) != null) {
					if(++i%2 == 1)
						chord.retrieve(line, root_node);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void writeToFile(String file, int value){
		try
		{
			String filename= file;
			FileWriter fw = new FileWriter(filename,true); //the true will append the new data
			fw.write(Integer.toString(value)+"\n");
			fw.close();
		}
		catch(IOException ioe)
		{
			System.err.println("IOException: " + ioe.getMessage());
		}
	}

	public void leaveMultiple(Chord chord) {
		String file = "nodes_1_to_63.txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			String line;

			try {
				while ((line = br.readLine()) != null ) {

					chord.leave(line);

				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


	}

	/**
	 * @param chord
	 */
	public void leaveHalf(Chord chord) {
		String file = "nodes_1_to_63.txt";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			String line;
			int i = 0;
			try {
				while ((line = br.readLine()) != null ) {
					if(++i%2 == 1){
						chord.leave(line);
					}

				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


	}

}
