import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;



public class RedditBot {

    //login credentials
    static String userName = "redditBot010101";
    static String passWord = "@1234@abcd@";
    static String apiKey = "5XvsmOEf7k6-fw";
    static String secret = "pdd9_aIcfWjOMnKRAltAf1rHjRA";

    public static UserAgent userAgent = new UserAgent("desktop","name.Java-API","v0.1",userName);
    public static Credentials credentials = Credentials.script(userName,passWord, apiKey, secret);
    public static NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
    public static RedditClient redditClient = OAuthHelper.automatic(networkAdapter, credentials);


    //reddit bot constructor
    public RedditBot(){}

    public String getPosts(String subR, String category) {

        //Creating a string builder to append posts
        StringBuilder fivePosts = new StringBuilder();
        SubredditReference subreddit = redditClient.subreddit(subR);

        //check if subreddit is valid before moving on
        try{
            subreddit.about().getFullName();
        }catch (Exception E){
            return "Error";
        }
        //this object will sort the posts depending on hot, top or new
        SubredditSort cat;

        switch (category)
        {
            case "hot":
                cat = SubredditSort.HOT;
                break;
            case "top":
                cat = SubredditSort.TOP;
                break;
            case "new":
                cat = SubredditSort.NEW;
                break;
            //return error if user input anything other than hot, new or top.
            default:
                return "Error";
        }

        //Get only 5 posts from the category the user input
        DefaultPaginator<Submission> paginator = subreddit.posts().limit(5).sorting(cat).build();

        Listing<Submission> firstPage = null;

        //if the subreddit doesn't exist then return error
        try {
            firstPage = paginator.next();
        }catch (NetworkException nE) {
            return "Error";
        }

        //iterate through page to get posts
        for (Submission post : firstPage)
            fivePosts.append(post.getTitle() + " " + post.getScore()+ " --- ");

        //return string containg 5 posts from subreddit
        return fivePosts.toString();
    }
}
