package okan.apps.lukkien.models;

/**
 * Twitter Data Model Class.
 * It's used to model the Data we receive from Twitter4j API.
 * 
 * @author Okan SAYILGAN
 */
public class TwitterDataModel {
	
	private long id;
	private String userName;
	
	/** Screen Name of the Twitter User, accountName */
	private String userScreenName;
	
	/** Image URL of the User's Account */
	private String userImageUrl;
	
	/** Number of Tweets */
	private String numberOfTweets;
	/** User is Following that number of Users */
	private String numberOfFollows;
	/** User is Followed by this number of users */
	private String numberOfFollowers;
	
	/** Date Tweeted */
	private String dateCreated;
	
	/** Tweet Text */
	private String tweetText;
	
	/** Attached Media URL */
	private String attachedImageUrl;
	
	public TwitterDataModel(long id, String userName, String userScreenName, String userImageURL, String numberOfTweets, 
			String numberOfFollows, String numberOfFollowers, String dateCreated, String tweetText, String attachedImageUrl) {
		
		this.id = id;
		this.userName = userName;
		this.userScreenName = userScreenName;
		
		this.userImageUrl = userImageURL;
		
		this.numberOfTweets = numberOfTweets;
		this.numberOfFollows = numberOfFollows;
		this.numberOfFollowers = numberOfFollowers;
		
		this.dateCreated = dateCreated;
		
		this.tweetText = tweetText;
		
		this.attachedImageUrl = attachedImageUrl;
	}
	
	public String getAttachedImageUrl() {
		return this.attachedImageUrl;
	}
	
	public void setAttachedImageUrl(String attachedImageUrl) {
		this.attachedImageUrl = attachedImageUrl;
	}
	
	public String getUserScreenName() {
		return userScreenName;
	}
	
	public void setUserScreenName(String userScreenName) {
		this.userScreenName = userScreenName;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserImageUrl() {
		return userImageUrl;
	}
	
	public void setUserImageUrl(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}

	public String getNumberOfTweets() {
		return numberOfTweets;
	}

	public void setNumberOfTweets(String numberOfTweets) {
		this.numberOfTweets = numberOfTweets;
	}

	public String getNumberOfFollows() {
		return numberOfFollows;
	}

	public void setNumberOfFollows(String numberOfFollows) {
		this.numberOfFollows = numberOfFollows;
	}

	public String getNumberOfFollowers() {
		return numberOfFollowers;
	}

	public void setNumberOfFollowers(String numberOfFollowers) {
		this.numberOfFollowers = numberOfFollowers;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getTweetText() {
		return tweetText;
	}

	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}
}