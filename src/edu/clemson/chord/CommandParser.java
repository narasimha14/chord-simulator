package edu.clemson.chord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;
/**
 * Used to parse the commands given to the I/O console
 * @author Narasimha
 *
 */
public class CommandParser {

	public static void main(String[] args) {

		int numberOfEntries = getNumberOfEntries();

		Chord chord = new Chord(numberOfEntries);

		System.out.println("***** Welcome to the Chord Network ***** \n \n");
		displayHelp();
		System.out.print("chord:/>");
		BufferedReader lineReader = new BufferedReader(new InputStreamReader(System.in));
		String line;

		try {
			while ((line = lineReader.readLine()) != null) {
				Scanner in = new Scanner(line); 

				String[] arguments = new String[10];

				int i =0;
				while(in.hasNext()){
					arguments[i] = in.next();

					++i;
				}

				if(i >= 1){
					if(arguments[0].equals("create")){

						if(i == 1){
							System.out.println("Not enough parameters to create");

						}else{
							System.out.println("Creating a Chord ring with "+arguments[1]+" as the root node");
							chord.create(arguments[1]);
						}


					} else if(arguments[0].equals("join")){
						if(i <= 2){
							System.out.println("Not enough parameters to join");
						}else{

							chord.join(arguments[1], arguments[2]);
						}
					}
					else if(arguments[0].equals("show")){
						if(i < 2){
							System.out.println("Not enough parameters. Give \"all\" or the node IP as the parameter");
						}else if(arguments[1].equals("all")){
							chord.showAll();
						}else{
							chord.show(arguments[1]);
						}
					}
					else if(arguments[0].equals("showkeys")){
						if(i < 2){
							System.out.println("Not enough parameters to show keys for. Give the node IP address as the parameter");
						}else{
							chord.showKeys(arguments[1]);
						}
					}
					else if(arguments[0].equals("store")){
						if(i < 2){
							System.out.println("Not enough parameters to store. Provide the file name");
						}else
							chord.store(arguments[1]);
					}
					else if(arguments[0].equals("retrieve")){
						if(i < 2){
							System.out.println("Not enough parameters to retrieve. Provide the file name");
						}else if(i == 2)
							chord.retrieve(arguments[1]);
						else if(i == 3)
							chord.retrieve(arguments[1], arguments[2]);
					}
					else if(arguments[0].equals("leave")){
						if(i < 2){
							System.out.println("Not enough parameters to leave");
						}else
							chord.leave(arguments[1]);
					}
					else if(arguments[0].equals("path")){
						if(i < 3){
							System.out.println("Not enough parameters to show the path for give the source and destination IP address");
						}else
							chord.path(arguments[1], arguments[2]);
					}
					else if(arguments[0].equals("run")){
						if(arguments[1].equals("join")){
							chord.joinMultiple(chord);
						}else if(arguments[1].equals("store")){
							chord.storeMultiple(chord);
						}else if(arguments[1].equals("retrieve")){
							if(i == 3 ){
								if(arguments[2].equals("half"))
									chord.retrieveHalf(chord);
							}
							else
								chord.retrieveMultiple(chord);
						}else if(arguments[1].equals("leave")){
							if(i == 3 ){
								if(arguments[2].equals("half"))
									chord.leaveHalf(chord);
							}
							else
								chord.leaveMultiple(chord);
						}
					}
					else if(arguments[0].equals("hash")){
						if(i >= 2)
							System.out.println("Hash of " + arguments[1] + " is " + HashFunction.getHash(arguments[1]));
						else{
							System.out.println("Not enough parameters. Give a node IP to calculate hash for");
						}
					}
					else if(arguments[0].equals("exit")){
						break;
					}else if(arguments[0].equals("help")){
						displayHelp();
					}	
					else{
						System.out.println("No such command found. Type \"help\" for a list of commands");
					}
				}
				System.out.print("chord:/>");
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			lineReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	/**
	 * Displays the list of commands available
	 */
	private static void displayHelp() {
		System.out.println("Available commands and their usage details are as follows \n");
		System.out.println("create <IP address> 					; creates the chord network \n"
				+ "join <Source IP address> <Destination IP address> 	; joins the source node to the already connected destination node\n"	
				+ "leave <IP address> 					; removes the node from the Chord Network\n"
				+ "store <filename> 					; stores the file into one of the nodes \n"
				+ "retrieve <filename> <IP address of the starting node>	; retrieves the file from the node where it is stored \n"
				+ "show <IP address> 					; displays the finger table of the Node \n"
				+ "showkeys <IP address> 					; displays the keys stored in the Node \n"
				+ "show all 						; displays all the IP addresses and their hashed identifiers \n"
				+ "exit 							; exits the Chord network");

	}
	/**
	 * Fetch the value of max number of bits in the hashed value
	 * from the properties file
	 * @return
	 */
	private static int getNumberOfEntries() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			String filename = "chord.properties";
			input = new FileInputStream(filename);
			prop.load(input);


			int numberOfEntries = Integer.parseInt(prop.getProperty("mBitLength"));

			return numberOfEntries;
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
		return -1;

	}
}
