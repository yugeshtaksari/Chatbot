//============================================================================
//Name        : FirstBot.java
//Author      : Yugesh Taksari
//
//Description : This description shows the runtime
//
//Test case: Input: "hero weather at The University of Texas at Dallas"
//Response:			Weather at 800 W Campbell Rd, Richardson, TX 75080, US:
//					The current temperature is: 42.9 degree Fahrenheit
//					Minimum Temperature: " 32.4 and Maximum Temperature:56.1 degree Fahrenheit
//					Current wind condition: 3.4 miles/hr");
	    		
//============================================================================
import org.jibble.pircbot.*; // for using pircbot framework
import java.io.BufferedReader; // to get the user input as string
import java.io.InputStreamReader; // to enable getting input
import java.net.HttpURLConnection; // to connect to the api
import java.net.URL; // to connect to api
import com.google.gson.* ; // to be able to work with JSON objects

public class FirstBot extends PircBot{
		
	    public FirstBot() {
	        this.setName("hero"); // name of the bot as visible to other users in channel
	    }
	    //method to give results to user
	    public void printInfo(String channel, String sender, String[] result, String userString){
    		try {
    			// array to store the latitude and longitute of a location
    			String[] locationName = getCity(userString);
    			
    			//array to store results from the response of weather api
	    		result = (getWeather(locationName)); // Index 0 = name, 1=temp, 2=min, 3=max, 4=windsSpeed
	    		if(result[0]=="error")  // if an invalid city is sent for weather request
	    			sendMessage(channel, sender + " Error in City name"); 
	    		
	    		else { // for most cases, in successful api request completion, format result as follows
	    		sendMessage(channel, sender + " Weather at " + result[0]); 
	    		sendMessage(channel, sender + " The current temperature is: " + result[1] + " degree Fahrenheit");
	    		sendMessage(channel, sender + " Minimum Temperature: " + result[2]+ " and Maximum Temperature: " + result[3] + " degree Fahrenheit");
	    		sendMessage(channel, sender + " Current wind condition: " + result[4] + " miles/hr");
	    		}
	    	}
	    	catch (Exception e) { // try/catch block to handle exception
	    		sendMessage(channel, sender + " No Information Found"); 
					System.out.println("Exception Occured Error");
			}
	    }
	    
	    //method that actually processes the input and send response
	    public void onMessage(String channel, String sender,
            String login, String hostname, String message) {
	    	message = message.toLowerCase(); // change user input to lowercase so case sensitivity does not affects in runtime

	    	if (message.contains("hero")) {
	    	// String array to hold result that we want to give to user
	    	String[] result = new String[5];
	    	
	    	// conditional statement to handle "time" as input
	    	if (message.contains("time")) {
	    		String time = new java.util.Date().toString();
	    		sendMessage(channel, sender + " : Time " + time);
	    	}
	    	
	    	// conditional statement to handle "hi"/"hey" and "hello" as input
	    	if (message.contains("hi")||message.contains("hey")||message.contains("hello")) {
	    		sendMessage(channel, sender + " Hi There, How can I help you ?");
	    	}
	    	
	    	// conditional statement to handle "weather" as input
	    	else if (message.contains("weather") && (message.length() < 9)) {
		    	sendMessage(channel, sender + " Which place's weather are you looking for?" );
		    }
		    
		    else // conditional statement to handle name of the city or location as input
		    	printInfo(channel, sender, result, message);

	    }
}
	    
	    // API USE #1 Google Maps API
		/** Start of the getWeather method to request weather data of a city from open
		 * 	weather map api and parse the result using the ParseWeatherJson. we use the city's latitude value 
		 *  and city's longitude value to get the result from the open weather map api. We get this data from
		 *  getCity method which uses google api to get a valid address. From the result we received from weather api,
		 *  we only extract temperature, minimum and maximum temperature and current wind speed. the received
		 *  unit of temperature is Kelvin and unit of wind is meter/second and to make it more usable we 
		 *  convert that units to more usable fahrenhiet and miles/hour unit using appropriate conversion techniques
		 *  example from Kelving to F we use: 1.8*(temperatureInKelvin-273)+32 and 
		 *  from meter/second to miles/hour: 2.24*speedInMeterPerSecond**/
    	public String[] getWeather(String[] latAndLng) throws Exception{
 	       	StringBuilder weatherURL = new StringBuilder(""); //string builder to create a api get url
 	    	String site = "http://api.openweathermap.org/data/2.5/weather?"; // main api request site
 	        String weatherAPItoken = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; // ENTER YOUR parameter of API Key

 	        // append all parts of url to create a complete site
 	        weatherURL.append(site);
 	        weatherURL.append("lat="+latAndLng[0]+"&lon="+latAndLng[1]);
 	        weatherURL.append("&appid=");
 	        weatherURL.append(weatherAPItoken);
 	        String connectionLink = weatherURL.toString(); // get url as string       
 	        
 	        // set up the http request
 	        URL url = new URL(connectionLink);
 	        HttpURLConnection con = (HttpURLConnection)url.openConnection(); // connect to the url
 	        con.setRequestMethod("GET");
 	        //con.setRequestMethod("POST");
 	        
 	        BufferedReader read = new BufferedReader(new InputStreamReader(con.getInputStream()));
 	        String result = read.readLine(); // read all input as string
 	        
 	        // call parseWeatherJson to parse the JSON object received through API and store return value in string array temp
 	        String[] temp = parseWeatherJson(result);
 	        temp[0] = latAndLng[2];
 	        return temp; // return the filtered / useful information to the caller
    	}

		public static String[] parseWeatherJson (String json)
	    {
			String[] filteredInfo = new String[5];// array to store parsed information
	    	JsonObject object = new JsonParser().parse(json).getAsJsonObject();// get response from api site as a json object
	    	
	    	//int returnCode = object.get("cod").getAsInt();
	    	//conditional statement to check for invalid city response
	    	if(object.get("cod").getAsInt() == 404) {
	    		filteredInfo[0] = "Error";	return filteredInfo;}
	    	
	    	String cityName = object.get("name").getAsString();
	    	filteredInfo[0] = cityName;
	    	
	    	double temp, temp_min, temp_max, windSpeed; // variable to get the data from json object
	    	// now get the value of temperature from the main json object
	    	JsonObject main = object.getAsJsonObject("main");
	    	temp = main.get("temp").getAsDouble();
	    	filteredInfo[1] = String.format("%.2f", (1.8*(temp-273)+32)); //store in return array
	    	
	    	// now get the value of minimum temperature from the main json object
	    	temp_min = main.get("temp_min").getAsDouble();
	    	filteredInfo[2] = String.format("%.2f", (1.8*(temp_min-273)+32)); //store in return array
	    
	    	// now get the value of maximum temperature from the main json object	    	
	    	temp_max = main.get("temp_max").getAsDouble();
	    	filteredInfo[3] = String.format("%.2f", (1.8*(temp_max-273)+32)); //store in return array
	    
	    	// now get the value of wind speed from the main json object	    		    	
	    	JsonObject wind = object.getAsJsonObject("wind");
	    	windSpeed = wind.get("speed").getAsDouble();
	    	filteredInfo[4] = String.format("%.2f", (2.24* windSpeed)); //store in return array
	    	
	    	return filteredInfo; //return the array with all required results
	    }

		// API USE #2 OpenWeatherMap API
		/** Start of the getCity method to request a valid city data from google maps api
		 *  and parse the result using the ParseCityJson. we use the city's latitude value 
		 *  and city's longitude value so we only extract that information from the json
		 *  parser. For example if user inputs the name "The University of Texas at Dallas"
		 *  the google maps api will return the details of location and among those details we choose
		 *  the latitude and longitude of UT Dallas which is Richardson and pass it to the caller**/
    	public String[] getCity(String str) throws Exception{
    		StringBuilder cities = new StringBuilder(str); //using this string Builder to make a complete api request link
    													  // and optimize the city name to fit to google api format eg. fort worth = fort+worth
    		
    		for(int i = 0; i<str.length(); i++) { // replace all the space with '+' to make api request url more optimized
    			if(str.charAt(i) == ' ')
    				cities.setCharAt(i, '+'); //changing str to cities allows us to change the string for optimization
    		}
    		str = cities.toString(); // after optimization convert optimized city name to String object 
 	       	StringBuilder cityURL = new StringBuilder("");//string builder to create a api get url
 	    	String site = "https://maps.googleapis.com/maps/api/geocode/json?address=";
 	        StringBuilder cityInput = new StringBuilder(str); 
 	        /**
 	         * make sure this StringBuilder is being used try: just str to cityURL.append(str); rather than cityInput
 	         */
 	        String cityAPItoken = "AIzaSyDgegUsZWo_sNk1uKaZkvxWRKHuXFK6DVw";

 	   
 	        cityURL.append(site);
 	        cityURL.append(cityInput);
 	        cityURL.append("&key=");
 	        cityURL.append(cityAPItoken);
 	        
 	        
 	        // final url to request weather information
 	        String connectionLink = cityURL.toString();  
 	        
 	        
 	        // set up the http request
 	        URL url = new URL(connectionLink);
 	        HttpURLConnection con = (HttpURLConnection)url.openConnection(); // connect to the url
 	        con.setRequestMethod("GET");
 	      
 	        // read all input as string
 	        BufferedReader read = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sbuild = new StringBuilder();
            String line = null;
            while ((line = read.readLine()) != null) {
                sbuild.append(line);
            }
            String result = sbuild.toString();
           
  	       
 	        //call parseWeatherJson to parse the JSON object received through API and store return value in string array temp
 	        return  parseCityJson(result); // return the filtered / useful information to the caller i.e lat and lng
    	}
    	
		public static String[] parseCityJson (String json1)
	    {
			String[] geographicData = new String[3]; // array to hold latitude and longitude of the city whose weather we are seeking
	    	
			// now we are diving from object to object to grab required info
			// we will go from object->results->0->geometry->location--->lat--->lng
			// get the response from api request as a json object
			JsonObject object = new JsonParser().parse(json1).getAsJsonObject();
	    	
			// get sub-information "results" as a separate new JsonArray whose elements will be JsonObjects
	    	JsonArray Results = (JsonArray) object.get("results");
	    	
	    	// get sub-information "0" as a separate new JsonObject 
	        JsonObject Label0 = (JsonObject)Results.get(0);
	        
	        // get sub-information "geometry" as a separate new JsonObject
	        //JsonObject FormattedAddress = (JsonObject)Label0.get("formatted_address");
	        
	        // get sub-information "geometry" as a separate new JsonObject
	        JsonObject Geometry = (JsonObject)Label0.get("geometry");
	        
	        // get sub-information "location" as a separate new JsonObject
	        JsonObject location = (JsonObject) Geometry.get("location");
	        
	        // we have reached to the data we need now store that latitude and longitude in our returnable array
	        geographicData[0] = location.get("lat").getAsString();
	        geographicData[1] = location.get("lng").getAsString();
	        geographicData[2] = Label0.get("formatted_address").getAsString();
	    	return geographicData; // return the filtered / useful information to the caller i.e lat and lng
	    }
}
