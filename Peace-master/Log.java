package src;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Log {

	static String path;
	protected Log(String _path){
		
		path = _path;
		
	}//end constructor
	
	
	
	
	
boolean write(double d, String m){	
	
	boolean complete = false;
	
	try {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), true));
		//log the timestamp
		
		bw.write(Double.toString(d));
		bw.write(",");
		//log the message
		bw.write(m);
		bw.write("\n");
		//all good
		complete = true;
		bw.close();
		
	} catch (IOException e) {
		
		e.printStackTrace();
		System.out.println("path error accessing log");

	}
	//false breaks, true makes
	
	return complete;

}



	




}
