package edu.clemson.chord;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
/**
 * This is the hashfuction that does a SHA-1 encoding of the IP address and then
 * returns a mod of 2^(number of bits required in the hashed value)
 * @author Narasimha
 *
 */
public class HashFunction {

	/**
	 * Returns the hashed value of @param str
	 * @param str
	 * @return
	 */
	static int getHash(String str){
		int sizeOfRing = getSize();
		try {
			BigInteger bigInt = new BigInteger(SHA1(str), 16);
			return Math.abs(bigInt.intValue()%sizeOfRing);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}



		return -1;
	}
	
	/**
	 * Fetch the value of max number of bits in the hashed value
	 * from the properties file and calculate max number of nodes
	 * @return
	 */
	private static int getSize() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			String filename = "chord.properties";
			input = new FileInputStream(filename);
			prop.load(input);


			int numberOfEntries = Integer.parseInt(prop.getProperty("mBitLength"));
			int sizeOfRing = (int) Math.pow(2, numberOfEntries);
			return sizeOfRing;
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

	/**
	 * Converts the SHA-1 encoded byte value to Hex
	 * @param data
	 * @return
	 */
	private static String convertToHex(byte[] data) { 
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) { 
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do { 
				if ((0 <= halfbyte) && (halfbyte <= 9)) 
					buf.append((char) ('0' + halfbyte));
				else 
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while(two_halfs++ < 1);
		} 
		return buf.toString();
	} 

	/**
	 * Returns the SHA-1 encoded byte value of @param text
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String SHA1(String text) 
			throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		sha1hash = md.digest();
		return convertToHex(sha1hash);

	} 

}
