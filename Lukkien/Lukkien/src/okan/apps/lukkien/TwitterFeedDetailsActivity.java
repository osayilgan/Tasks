package okan.apps.lukkien;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class TwitterFeedDetailsActivity extends Activity {
	
	private PhotoViewAttacher mAttacher;
	private ImageView mImageview;
	private String imageUrl;
	
	/**
	 * Starts this Activity
	 * 
	 * @param context	Activity Context
	 * @param imageUrl	Image URL
	 */
	public static void startActivity(Context context, String imageUrl) {
		Intent intent = new Intent(context, TwitterFeedDetailsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("IMAGE_URL", imageUrl);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_feed_item_details);
		
		mImageview = (ImageView) findViewById(R.id.twitter_item_attachedImage);
		mAttacher = new PhotoViewAttacher(mImageview);
		
		/* Get Extra Attached Data */
		Bundle intentExtras = getIntent().getExtras();
		
		/* Get Image URL from Bundle */
		if (intentExtras != null) {
			imageUrl = intentExtras.getString("IMAGE_URL");
			Log.i("Okan", "ImageUrl : " + imageUrl);
		}
		
		/* Load Image */
		Picasso.with(this).load(imageUrl).into(mImageview);
	}
}
