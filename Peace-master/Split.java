package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Split{

	static boolean makeTestData(String symbol, int length) throws IOException{

		boolean good = true;	

		String path = "src/files/" + symbol + ".csv";

		String test = "src/test/" + symbol + ".csv";

		File p = new File(path); 
		if(p.isFile()){
		BufferedReader b;
		b = new BufferedReader(new FileReader(p));
		File f = new File(test);
		FileWriter fw = new FileWriter(f);
		BufferedWriter e = new BufferedWriter(fw);	

		String line = "";

		int index = 0;

		while(((line = b.readLine()) != null) && (index < length)){

			e.write(line + "\n");
			
			index++;
		} 

		b.close();
		e.close();

		}//end if File exists
		return good;



	}//end makeTestData
	
	
}//end class def