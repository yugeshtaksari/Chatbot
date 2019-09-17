//============================================================================
//Name        : FirstBotMain.java
//Author      : Yugesh Taksari
//
//Description : This is a program that acts as a interactive chat bot that will 
// simulate conversations, specially regarding the weather. this program requires connection
// to the Internet for complete use. so the char engine we use is the PricBot which is a 
// popular java IRC API. In simple words, pircbot is a java framework for writing IRC bots
// at first the program sets up the bot which is able to connect to the #pircbot channel in IRC server
// after that in other class FirstBot we have event handling when users sends certain
// words or strings. we then process the user requests based on the api rest request and
// display the results to users making it more like human conversation.
// we receive response from out api request in JSON(Javascript object notation) format
// then we parse the Json object to make it more usefulness and understanding.
// We have used two Api request in this program by using method getCity and getWeather and
// we have used two parser parseCityJson and parseWeatherJson to parse the json respond respectively.

//============================================================================



public class FirstBotMain {
    
    public static void main(String[] args) throws Exception {

        // tart our bot up.
        FirstBot bot = new FirstBot();
        
        // Enable debugging output.
        bot.setVerbose(true);
        
        // Connect to the IRC server.
        bot.connect("irc.freenode.net");

        // Join the #pircbot channel.
        bot.joinChannel("#pircbot");
    } // end of main

}

