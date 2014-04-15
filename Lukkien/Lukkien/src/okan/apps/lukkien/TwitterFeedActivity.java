package okan.apps.lukkien;

import java.util.ArrayList;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;

import okan.apps.lukkien.TwitterFeedManager.TwitterTweetFeedInterface;
import okan.apps.lukkien.models.TwitterDataModel;
import Utils.NetworkUtils;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TwitterFeedActivity extends Activity {
	
	/** Loader Indicator */
	private ProgressBar progressBar;
	
	/** Text View for no-data case */
	private TextView noDataText;
	
	/** ListView */
	private StickyListHeadersListView twitterFeedListView;
	
	/** Layout Holding some numbers related with Twitter Account */
	private RelativeLayout tweetNumbersHolder;
	
	/** Manager class as a bridge with Twitter4j API */
	private TwitterFeedManager twitterFeedManager;
	
	/** Twitter Feed Adapter */
	private TwitterFeedAdapter twitterAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.twitter_feed);
		
		/* Load UI Elements */
		loadUI();
		
		/* initialize twitter manager */
		twitterFeedManager = new TwitterFeedManager(this);
		
		if (NetworkUtils.isConnected(this)) {
			
			/* Get User Time Line Asynchronously */
			twitterFeedManager.getUserTimeLine(new TwitterTweetFeedInterface() {
				
				/** SUCCESS */
				
				@Override
				public void onSuccess(ArrayList<TwitterDataModel> result) {
					
					progressBar.setVisibility(View.GONE);
					noDataText.setVisibility(View.GONE);
					
					twitterAdapter = new TwitterFeedAdapter(TwitterFeedActivity.this, result);
					twitterFeedListView.addHeaderView(tweetNumbersHolder);
					twitterFeedListView.setAdapter(twitterAdapter);
				}
				
				/** FAIL */
				
				@Override
				public void onError() {
					
					tweetNumbersHolder.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					noDataText.setVisibility(View.VISIBLE);
					noDataText.setText(getString(R.string.no_results_found));
				}
			});
			
		} else {
			/* Device is not Connected to the Internet */
			/* Set UI to "No Internet Connection" */
			
			tweetNumbersHolder.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			noDataText.setVisibility(View.VISIBLE);
			noDataText.setText(getString(R.string.check_internet_connection));
		}
	}
	
	/**
	 * Loads UI Elements from related XML file.
	 */
	private void loadUI() {
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		noDataText = (TextView) findViewById(R.id.noData);
		
		twitterFeedListView = (StickyListHeadersListView) findViewById(R.id.twitterListView);
		
		tweetNumbersHolder = (RelativeLayout) getLayoutInflater().inflate(R.layout.twitter_feed_list_item_header, null, false);
	}
}
