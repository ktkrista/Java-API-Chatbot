import org.jibble.pircbot.PircBot;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DecimalFormat;


public class Bot extends PircBot
{

    //Constructor for bot setting a name for the bot
    public Bot() { this.setName("Bot0100101101001010");}


    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        //convert message sent from user to lowercase
        message = message.toLowerCase().trim();
        //split number of words into string array
        String[] wordsFromUser = message.split(" ");

        //exit if there is no words
        if (wordsFromUser.length < 1) return;

        /*
         * THIS IF/ELSE STATEMENTS will include the options for the bot to answer questions regarding time, weather and other.
         * */
        if (wordsFromUser[0].equals("time")) {

            //if the user only inputs weather it will output an error
            if (wordsFromUser.length != 1){
                sendMessage(channel, "Only enter time.");
                return;
            }
            //get time and store into string format
            String time = new java.util.Date().toString();
            //output time to user on chat
            sendMessage(channel, "Time is: " + time);

            //The user entered the commmand for weather.
        } else if (wordsFromUser[0].equals("weather")) {

            //if the user only inputs weather it will output an error
            if (wordsFromUser.length != 2){
                sendMessage(channel, "Please enter a valid city, state or zipcode.");
                return;
            }

            //These two statements will look for a zipcode pattern in form of 5 consecutive digits within second argument from user.
            Pattern zipCodePattern = Pattern.compile("\\d{5}");
            Matcher zipCodeMatch = zipCodePattern.matcher(wordsFromUser[1]);

            //The second command is a zipcode
            if (zipCodeMatch.find()) {

                //storing zipcode into a string
                String zipCode = wordsFromUser[1];
                //accessing url with API key
                String weatherURL = "http://api.openweathermap.org/data/2.5/weather?zip=" + zipCode + "&appid=27974f743ee9bc4d7f174a55f48d3ada";
                //Will access API from zipcode and return a string containing a json file
                String jsonFile = StartWebRequestToRetriveJsonFile(weatherURL);
                //handle user input if error
                RespondUserInput(channel, jsonFile);

            } else {//getting weather on city

                //temp variable to hold city name
                String cityName = "\0";

                //check if there is a comma in second argument ex: Dallas,Texas. We just extract dallas.
                if (wordsFromUser[1].contains(",")) {

                    //parsing the city from second argument. first element in array of strings is city and second is state
                    String[] cityState = wordsFromUser[1].split(",", 2);
                    //storing city
                    cityName = cityState[0];

                } else//The command does not include state, only city ex: weather Dallas
                    cityName = wordsFromUser[1];


                //accessing url with API key
                String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=27974f743ee9bc4d7f174a55f48d3ada";
                //Will access API from city and return a string containing a json file
                String jsonFile = StartWebRequestToRetriveJsonFile(weatherURL);
                //handle user input if error
                RespondUserInput(channel, jsonFile);

            }

        //The user entered the command for reddit.
        }else if (wordsFromUser[0].equals("reddit")) {

            //if the user does not enter the correct amount of words, output error.
            if (wordsFromUser.length != 3) {
                sendMessage(channel, "Please enter valid format. Ex: reddit utdallas hot.");
                return;
            }

            /*
            * This command will let the user entered the subrredit name followed by hot, new, etc.
            * It will output only 5 posts from that subrredit.
            * */
            //extract subreddit
            String subreddit = wordsFromUser[1];
            //extract hot, new, top.
            String category = wordsFromUser[2];

            //create a reddit bot to extract the 5 posts from the users input.
            RedditBot rBot = new RedditBot();

            //getting top 5 posts from sureddit and category
            String Posts = rBot.getPosts(subreddit, category);

            //check for error
            if(Posts.equals("Error"))
                sendMessage(channel,"Please enter valid format. Ex: reddit utdallas hot.");
            else
                sendMessage(channel, Posts);
        }
    }

    static String StartWebRequestToRetriveJsonFile(String weatherURL)
    {

        //string to store json file
        StringBuilder result = new StringBuilder();

        try
        {
            //Connecting to url
            URL url = new URL(weatherURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //Getting information from API
            conn.setRequestMethod("GET");
            //creating variable to read file
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //temp variable to read each line in file
            String line;
            //Reading Json file until EOF
            while ((line = rd.readLine()) != null)
            {
                result.append(line);
            }
            //close buffer!!
            rd.close();

            //returning entire json file from web request
	        return result.toString();

	        //return a string indicating error if we could not find zipcode or city
        } catch(Exception e) {return "Error";}
    }

    public void RespondUserInput(String channel,String jsonFile)
    {
        //This if else statement will handle error from user in case zipcode does not exist.
        if(!jsonFile.equals("Error"))
            sendMessage(channel, getWeatherInformation(jsonFile));//Parse json file to only what we need and output to user.
        else
            sendMessage(channel,"Please enter a valid zipcode/city.");
    }

    static String getWeatherInformation(String json)
    {
        //Creating a JsonObject and parsing its content to primitive types
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();

        //Storing Name tag from Json file into a variable(getting name of city)
        String cityName = object.get("name").getAsString();

	    /*Go to "main" in json object
	          "main": {
                "temp": 296.71,
                "pressure": 1013,
                "humidity": 53,
                "temp_min": 294.82,
                "temp_max": 298.71
	     */
        JsonObject main = object.getAsJsonObject("main");

	    //Getting the temp in Kelvins from main tag in json file
        double temp = getTempFahrenheit(main.get("temp").getAsDouble());

        //Getting humidity from main tag in json file and coverting to F
        int Humidity = main.get("humidity").getAsInt();

        //Getting min temp in Kelvins from main tag in json file and converting to F
        double tempMin = getTempFahrenheit(main.get("temp_min").getAsDouble());

        //Getting max temp in Kelvins from main tag in json file and converting to F
        double tempMax = getTempFahrenheit(main.get("temp_max").getAsDouble());

        //Decimal format will only be to tenths
        DecimalFormat df = new DecimalFormat("####0.0");

        return "The weather currently in " + cityName + " is : "
                                 + df.format(temp) + " degrees, "
                                 + "Humidity: "+ Humidity + "%, "
                                 + "Min : " + df.format(tempMin) + " degrees, "
                                 + "Max : " + df.format(tempMax) + ", degrees.";

    }

    //This function will return the temperature in Fahrenheit by passing in temp as Kelvin.
    static double getTempFahrenheit(double tempInKelvin) { return (tempInKelvin-273.15) * 1.8 + 32; }
}

