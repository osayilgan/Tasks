package okan.apps.lukkien;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okan.apps.lukkien.models.TwitterDataModel;
import twitter4j.MediaEntity;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Manager Class to Handle retrieving Tweets from a pre-defined User.
 * 
 * @author Okan SAYILGAN
 */
public class TwitterFeedManager {
	
	/** Activity Context */
	private Context mContext;
	
	/* Data To be Used from dev.twitter.com */
	private String userName;
	private String consumerKey;
	private String consumerSecret;
	
	private String accessToken;
	private String accessTokenSecret;
	
	/**
	 * Interface used as a Call Back for Retrieved Tweets from User's time line.
	 * 
	 * @author Okan SAYILGAN
	 */
	public static interface TwitterTweetFeedInterface {
		void onSuccess(ArrayList<TwitterDataModel> result);
		void onError();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	Activity Context.
	 */
	public TwitterFeedManager(Context context) {
		
		this.mContext = context;
		
		this.userName = mContext.getString(R.string.TWITTER_USER_NAME);
		this.consumerKey = mContext.getString(R.string.TWITTER_CONSUMER_KEY);
		this.consumerSecret = mContext.getString(R.string.TWITTER_CONSUMER_SECRET);
		this.accessToken = mContext.getString(R.string.TWITTER_ACCESS_TOKEN);
		this.accessTokenSecret = mContext.getString(R.string.TWITTER_ACCESS_SECRET);
	}
	
	/**
	 * Retrieves User's TimeLine Tweets.
	 * 
	 * @param twitterTweetFeedInterface
	 */
	public void getUserTimeLine(final TwitterTweetFeedInterface twitterTweetFeedInterface) {
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(consumerKey);
		cb.setOAuthConsumerSecret(consumerSecret);
		cb.setOAuthAccessToken(accessToken);
		cb.setOAuthAccessTokenSecret(accessTokenSecret);
		
		TwitterFactory tf = new TwitterFactory(cb.build());
		final Twitter twitter = tf.getInstance();
		
		/* AsyncTask for Retrieving the TimeLine Objects */
		new AsyncTask<Void, Void, Boolean>() {
			
			/* List of Tweets */
			ArrayList<TwitterDataModel> tweets;
			
			@Override
			protected void onPreExecute() {
				
				// Do Nothing, or send a feed back with TwitterTweetFeedListener.
			}
			
			@Override
			protected Boolean doInBackground(Void... params) {
				
				boolean success = true;
				
				try {
					
					ResponseList<twitter4j.Status> statuses = twitter.getUserTimeline(userName);
					
					tweets = new ArrayList<TwitterDataModel>();
					
					for (int i = 0; i < statuses.size(); i++) {
						
						twitter4j.Status status = statuses.get(i);
						twitter4j.User user = status.getUser();
						
						String userName = user.getName();
						String userScreenName = user.getScreenName();
						String profileImageURL = user.getBiggerProfileImageURL();
						
						String numberOfTweets = user.getStatusesCount() + "";
						String followCount = user.getFriendsCount() + "";
						String followersCount = user.getFollowersCount() + "";
						
						Date date = status.getCreatedAt();
						long tweetTimeInMinutes = getNumberOfMinutes(date);
						String formatedDate;
						
						/** SET THE DATE IN MINUTES, HOURS OR EXACT DATE */
						if (tweetTimeInMinutes < 60) {
							/* If it is less than an hour then calculate the minutes */
							formatedDate = (tweetTimeInMinutes) + mContext.getString(R.string.twitter_minutes_string);
						} else if ( (60 < tweetTimeInMinutes) && (tweetTimeInMinutes < (24*60))) {
							formatedDate = (tweetTimeInMinutes/60) + mContext.getString(R.string.twitter_hours_string);
						} else {
							formatedDate = formatDate(date);
						}
						
						String tweet = status.getText();
						boolean isRetweet = status.isRetweet();
						
						if (isRetweet) {
							
							twitter4j.User reTweetUser = status.getRetweetedStatus().getUser();
							
							userName = reTweetUser.getName();
							userScreenName = reTweetUser.getScreenName();
							profileImageURL = reTweetUser.getBiggerProfileImageURL();
						}
						
						/* Check for Medias */
						MediaEntity[] mediaEntities = status.getMediaEntities();
						String attachedImageUrl = "";
						
						/* Check if there any Photos Attached */
						for (MediaEntity mediaEntity : mediaEntities) {
							
							String tempMediaType = mediaEntity.getType();
							
							/* If the attached media is Photo then set the first one. */
							if (tempMediaType.equalsIgnoreCase("photo")) {
								attachedImageUrl = mediaEntity.getMediaURL();
							}
						}
						
						tweets.add(new TwitterDataModel(i, userName, userScreenName, profileImageURL, 
								numberOfTweets, followCount, followersCount, formatedDate, tweet, attachedImageUrl));
					}
					
				} catch (TwitterException e) {
					e.printStackTrace();
					success = false;
				}
				
				return success;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				if (twitterTweetFeedInterface != null) {
					
					if (result) {
						twitterTweetFeedInterface.onSuccess(this.tweets);
					} else {
						twitterTweetFeedInterface.onError();
					}
				}
			}
			
		/* Run it Globally Parallel */
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	/**
	 * Calculates the Number of days that given twitter status posted.
	 * 
	 * @param date	Posted Date.
	 * @return		Number of days.
	 */
	private long getNumberOfMinutes(Date date) {
		
		Calendar cal = Calendar.getInstance();
		Date currentDate = cal.getTime();
		
		long currentTimeInMiliseconds = currentDate.getTime();
		long dateTimeInMilisecodns = date.getTime();
		
		long differenceInMiliseconds = currentTimeInMiliseconds - dateTimeInMilisecodns;
		long differenceInMinutes = ((differenceInMiliseconds/1000)/60);
		
		return differenceInMinutes;
	}
	
	/**
	 * Formats the Given in the Twitter Date Style.
	 * 
	 * @param date		Date to be formated.
	 * @return			String in the Format that will be used in Twitter View.
	 */
	private String formatDate(Date date) {
		
		Locale netherlands = new Locale("nl", "NL");
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM", netherlands);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
		
		String[] formatedDate = simpleDateFormat.format(date.getTime()).split(" ");
		
		/* After Separating with space there will be 2 items in the array, day and the Month */
		String day = formatedDate[0];
		String month = formatedDate[1];
		
		/* First char needs to be Upper Case and Last Character (DOT) needs to be removed */
		month = month.substring(0, 1).toUpperCase() + month.substring(1, month.length()-1);
		
		return (day + " " + month);
	}
}
