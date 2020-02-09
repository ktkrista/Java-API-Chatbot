public class Main
{

    public static void main(String[] args) throws Exception
    {


        String nameOfChannel = "#JamesBond007007";
        String hostName = "irc.freenode.net";
        String botGreeting = "Hello, my name is AnnoyingBot, how can I help you?";
        String botMenu = "I can show the time, weather or reddit posts.";
        String botInputTime = "For time type: time";
        String botInputWeather = "For weather type: weather dallas // weather dallas,texas // weather 75240";
        String botInputReddit = "For reddit type: reddit utdallas hot // reddit utdallas top // reddit utdallas new";




        //Keeping all statements inside a try and catch just in case of any error.
        try{
            //Creating bot object
            Bot chatBot = new Bot();
            //used to debug bot
            chatBot.setVerbose(true);
            //bot will connect to this website
            chatBot.connect(hostName);
            //bot will join this channel
            chatBot.joinChannel(nameOfChannel);
            //Greet user
            chatBot.sendMessage(nameOfChannel,botGreeting);
            chatBot.sendMessage(nameOfChannel, botMenu);
            chatBot.sendMessage(nameOfChannel,botInputTime);
            chatBot.sendMessage(nameOfChannel,botInputWeather);
            chatBot.sendMessage(nameOfChannel,botInputReddit);

        }catch (Exception e) { System.out.println(e); }
    }
}
