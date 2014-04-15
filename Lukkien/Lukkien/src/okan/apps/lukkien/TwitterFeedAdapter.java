package okan.apps.lukkien;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import okan.apps.lukkien.models.TwitterDataModel;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.squareup.picasso.Picasso;

/**
 * Adapter class to handle Showing Twitter Feeds.
 * 
 * @author Okan SAYILGAN
 */
public class TwitterFeedAdapter extends BaseAdapter implements StickyListHeadersAdapter {
	
	/** List of TwitterDataModels */
	private ArrayList<TwitterDataModel> tweets;
	private TwitterFeedActivity mActivity;
	
	private LayoutInflater inflater;
	
	/* Adapter Constructor */
	public TwitterFeedAdapter(TwitterFeedActivity activity, ArrayList<TwitterDataModel> tweets) {
		
		this.mActivity = activity;
		this.tweets = tweets;
		this.inflater = mActivity.getLayoutInflater();
	}
	
	@Override
	public int getCount() {
		return tweets.size();
	}
	
	@Override
	public TwitterDataModel getItem(int position) {
		return tweets.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		
		if (convertView == null) {
			
			/** Inflate Layout and it's elements */
			
			convertView = inflater.inflate(R.layout.twitter_feed_item, parent, false);
			
			viewHolder = new ViewHolder();
			viewHolder.userName = (TextView) convertView.findViewById(R.id.twitter_item_user_name);
			viewHolder.screenName = (TextView) convertView.findViewById(R.id.twitter_item_screen_name);
			viewHolder.tweetTime = (TextView) convertView.findViewById(R.id.twitter_item_tweet_time);
			viewHolder.tweetText = (TextView) convertView.findViewById(R.id.twitter_item_content);
			viewHolder.userProfileImage = (ImageView) convertView.findViewById(R.id.twitter_item_user_profile_image);
			viewHolder.attachedImage = (ImageView) convertView.findViewById(R.id.twitter_item_attachedImage);
			
			convertView.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		/* Data for each Element */
		TwitterDataModel data = getItem(position);
		
		viewHolder.userName.setText(data.getUserName());
		viewHolder.screenName.setText("@" + data.getUserScreenName());
		viewHolder.tweetTime.setText(data.getDateCreated());
		
		/* Load User Image with Picasso Library */
		Picasso.with(mActivity).load(data.getUserImageUrl()).into(viewHolder.userProfileImage);
		
		final String attachedImageUrl = data.getAttachedImageUrl();
		if (attachedImageUrl.equals("")) {
			/* Check if the URL is empty String (I set it empty in the TwitterFeed Manager class.) */
			viewHolder.attachedImage.setVisibility(View.GONE);
		} else {
			
			viewHolder.attachedImage.setVisibility(View.VISIBLE);
			
			/* If it is not Empty then set tit to Load from given url */
			Picasso.with(mActivity).load(attachedImageUrl).into(viewHolder.attachedImage);
			
			/* Click Listener for Image Details */
			viewHolder.attachedImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					/* GO TO Details Activity */
					TwitterFeedDetailsActivity.startActivity(mActivity, attachedImageUrl);
				}
			});
		}
		
		/* Set Tweet Text */
		viewHolder.tweetText.setText(data.getTweetText());
		
		/* Linkify Links in the TweetText*/
		Linkify.addLinks(viewHolder.tweetText, Linkify.ALL);
		
		return convertView;
	}
	
	/* View Holder Class for UI */
	private static class ViewHolder {
		
		TextView userName;
		TextView screenName;
		TextView tweetTime;
		
		ImageView userProfileImage;
		
		TextView tweetText;
		
		ImageView attachedImage;
	}
	
	/* Header View Holder Class */
	private static class HeaderViewHolder {
		
		TextView numberOfTweetsTextView;
		TextView numberOfFollowsTextView;
		TextView numberOfFollowersTextView;
	}
	
	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		
		HeaderViewHolder headerViewHolder;
		
		if (convertView == null) {
			
			headerViewHolder = new HeaderViewHolder();
			
			/* Inflate Header View UI */
			convertView = inflater.inflate(R.layout.twitter_feed_list_item_sticky_header, null, false);
			
			headerViewHolder.numberOfTweetsTextView = (TextView) convertView.findViewById(R.id.twitter_item_tweet_count);
			headerViewHolder.numberOfFollowsTextView = (TextView) convertView.findViewById(R.id.twitter_item_number_of_follows);
			headerViewHolder.numberOfFollowersTextView = (TextView) convertView.findViewById(R.id.twitter_item_number_of_followers);
			
			convertView.setTag(headerViewHolder);
			
		} else {
			headerViewHolder = (HeaderViewHolder) convertView.getTag();
		}
		
		/* Set Header Data Here */
		setUserStatisticNumbers(getItem(0), headerViewHolder);
		
		return convertView;
	}
	
	@Override
	public long getHeaderId(int position) {
		return 0;
	}
	
	/**
	 * Sets the Number Of Tweets, Number of Follows and Number of Followers.
	 */
	private void setUserStatisticNumbers(TwitterDataModel twitterData, HeaderViewHolder headerViewHolder) {
		
		long numberOfTweets = Long.parseLong(twitterData.getNumberOfTweets());
		long numberOfFollows = Long.parseLong(twitterData.getNumberOfFollows());
		long numberOfFollowers = Long.parseLong(twitterData.getNumberOfFollowers());
		
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		
		DecimalFormat df = new DecimalFormat();
		df.setDecimalFormatSymbols(symbols);
		df.setGroupingSize(3);
		df.setMaximumFractionDigits(2);
		
		/* Set Formatted Data to UI */
		headerViewHolder.numberOfTweetsTextView.setText(df.format(numberOfTweets));
		headerViewHolder.numberOfFollowsTextView.setText(df.format(numberOfFollows));
		headerViewHolder.numberOfFollowersTextView.setText(df.format(numberOfFollowers));
	}
}
