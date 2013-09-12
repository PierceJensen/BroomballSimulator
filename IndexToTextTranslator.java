//This class translates an index from tcp data into the text it represents for entity indexes
import java.io.BufferedReader;
import java.io.FileReader;

public class IndexToTextTranslator {

	private String[] textArray;
	
	IndexToTextTranslator(){
		
		try {
			//first find the number of names with a line counter
			BufferedReader reader = new BufferedReader(new FileReader("../index/text.txt"));
			int shiplines = 0;
			while (reader.readLine() != null){
				shiplines++;
			}
			reader.close();
			
			//initiate the destination arrays
			textArray = new String[shiplines];
			
			//next parse each of the lines
			reader = new BufferedReader(new FileReader("../index/text.txt"));

			String strLine;
			int i = 0;
			
			//Read File Line By Line
			while ((strLine = reader.readLine()) != null) {
			  
				//split up the lines, using & as the delimiter
				String[] tokens = strLine.split("[&]");
			  
				textArray[i] = tokens[1];
				
				i++;
			}

			//Close the input stream
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	String getShipText(int i){
		
		return textArray[i];
	}
}
