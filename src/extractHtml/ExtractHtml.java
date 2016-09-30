package extractHtml;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class ExtractHtml {
	
	private static final String DECK_NAME = "msc_mr_12_040617_12001-12370";

	public static void main(String[] args) {
		Map<String, String> deckCoordinateMap = new TreeMap();
		try {
//				String content = readFile("html/msc_mr_12_040617_12001-12370.htm", StandardCharsets.UTF_8);
			
			    String content = new Scanner(new URL("https://www.icruise.com/c/deckplan2014.php?SailDate=&WMPHShipCode=542&DeckNumber=12").openStream(), "UTF-8").useDelimiter("\\A").next();
			    
				Pattern pattern = Pattern.compile("<area(.+)");
			    Matcher matcher = pattern.matcher(content);

			    while (matcher.find()) {

			    	for (int i = 1; i <= matcher.groupCount(); i++) {
		
			    		Pattern coordinatesPattern = Pattern.compile("coords=\"(.*?)\"");
		    		    Matcher coordsMatcher = coordinatesPattern.matcher(matcher.group(i));
		    		    coordsMatcher.find();
		    		    
		    		    Pattern cabinPattern = Pattern.compile("Cabin=[0-9]+");
		    		    Matcher cabinMatcher = cabinPattern.matcher(matcher.group(i));
		    		    cabinMatcher.find();
		    		    
		    		    String cabin = cabinMatcher.group(0);
		    		    cabin = cabin.replace("Cabin=", ""); 
		    		    
		    		    String coords = coordsMatcher.group(0);
		    		    coords = coords.replace("\"", "");
		    		    coords = coords.replace("coords=", "");
		    		    
		    		    deckCoordinateMap.put(cabin, coords);
			    	}
			    	
			    } 
			    
			    siwtchCalculations(deckCoordinateMap);
		      } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	private static void siwtchCalculations(Map<?, ?> deckCoordinates) {
		JSONArray arrayObjectToJson = new JSONArray();
		JSONObject mainObject = new JSONObject();
		
		List<String> deckName = new ArrayList<String>();
		deckName.add(DECK_NAME);
		
		List<String> targetCount = new ArrayList<String>();
		targetCount.add(""+deckCoordinates.size());
		mainObject.put("deck-name", deckName);
		mainObject.put("target-count", targetCount);
		
		arrayObjectToJson.add(mainObject);
		
		//TMP STATUS
		List<String> status = new ArrayList<String>();
		status.add("Appear");
		
		deckCoordinates.forEach((deck, coordinates)->{
			String[] splitedCoordinates = coordinates.toString().split(", ");
			System.out.println(deck);
			System.out.println(coordinates);
			
			int a = Integer.parseInt(splitedCoordinates[0]);
			int b = Integer.parseInt(splitedCoordinates[1]);
			int c = Integer.parseInt(splitedCoordinates[2]);
			int d = Integer.parseInt(splitedCoordinates[3]);
			
			
			JSONObject deckObject = new JSONObject();
			System.out.println("================");
			List<Integer> cabin = new ArrayList<Integer>();
			cabin.add(Integer.parseInt((String) deck));
			
			
			List<String> A = new ArrayList<String>();
			A.add(""+a);
			
			List<String> B = new ArrayList<String>();
			B.add(""+b);
			
			List<String> C = new ArrayList<String>();
			C.add(""+c);
		
			List<String> D = new ArrayList<String>();
			D.add(""+d);
			
			List<Integer> widthIntArr = new ArrayList<Integer>();
			widthIntArr.add(0);
			
			List<Integer> heightIntArr = new ArrayList<Integer>();
			heightIntArr.add(0);
			
			
			deckObject.put("x1", A);
			deckObject.put("x2", C);
			deckObject.put("x3", C);
			deckObject.put("x4", A);
			deckObject.put("y1", B);
			deckObject.put("y2", B);
			deckObject.put("y3", D);
			deckObject.put("y4", D);
			
			List<String> wdithArrEmpty = new ArrayList<String>();
			wdithArrEmpty.add("");
			List<String> heightArrEmpty = new ArrayList<String>();
			heightArrEmpty.add("");
			
			deckObject.put("width", C);
			deckObject.put("height", B);
			deckObject.put("cabin", cabin);
			deckObject.put("status", status);

			arrayObjectToJson.add(deckObject);
			

		});
		
		try {

			FileWriter file = new FileWriter(System.getProperty("user.dir") + "/" + DECK_NAME + ".json");
			file.write(arrayObjectToJson.toJSONString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("DONE");
	}
	
	static String readFile(String path, Charset encoding) 
	  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
}
