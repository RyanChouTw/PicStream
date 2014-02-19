package com.happypig.picstream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class PictureStreamMainActivity extends ActionBarActivity {
	private String mMeId;
	
	private OnClickListener mNewBtnClickListener = new OnClickListener() {
	    public void onClick(View v) {
	      // do something when imageViewSceneNearby is clicked
	    	startPictureGalleryActivity();
	    }
	};	

	private OnClickListener mPicStreamListBtnListener = new OnClickListener() {
	    public void onClick(View v) {
	      // do something when imageViewSceneArea is clicked	
	    	startPictureStreamListActivity();
	    }
	};	
	
	private OnClickListener mFbBtnClickListener = new OnClickListener() {
		public void onClick(View v) {
			// do something when Facebook button is clicked
			startFBLogin();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_stream_main);               

        printHashKey();
        
        Button btnNew = (Button) findViewById(R.id.newBtnPictureStreamMain);
        btnNew.setOnClickListener(mNewBtnClickListener);		
        Button btnPicStreamList = (Button) findViewById(R.id.listBtnPictureStreamMain);
        btnPicStreamList.setOnClickListener(mPicStreamListBtnListener);
        ImageButton btnFacebook = (ImageButton) findViewById(R.id.fbBtnPictureStreamMain);
        btnFacebook.setOnClickListener(mFbBtnClickListener);
        
        
        String action = getIntent().getStringExtra(CommonDef.EXTRA_TAG_PASS_ACTION);
        if (action != null) {
        	if (action.equals(CommonDef.ACTION_START_PICSTREAM_LIST)) {
        		startPictureStreamListActivity();
        	}
        }        
	}

	@Override
	protected void onNewIntent (Intent intent) {
		Log.d(getString(R.string.app_name), "onNewIntent");
	}

	private void startFBLogin() {
		// start Facebook Login
    	Session.openActiveSession(this, true, new Session.StatusCallback() {

			//callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					List<String> PERMISSIONS = Arrays.asList("user_photos","user_status");
					session.requestNewReadPermissions(new Session.NewPermissionsRequest(PictureStreamMainActivity.this, PERMISSIONS));
					
			        Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

			            // callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user, Response response) {
							// TODO Auto-generated method stub
							mMeId = user.getId();
						}
			          });					
				}
			}
		}); 
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	private void startPictureGalleryActivity() {
        Intent i = new Intent(PictureStreamMainActivity.this, PictureGalleryActivity.class);
        i.putExtra(CommonDef.EXTRA_TAG_PASS_FB_USER_ID, mMeId);
        startActivity(i);		
	}
	
	private void startPictureStreamListActivity() {
    	Intent i = new Intent(PictureStreamMainActivity.this, PictureStreamListActivity.class);
    	startActivity(i);		
	}
	
    /* For debugging */
    public void printHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.happypig.picstream", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("TEMPTAGHASH KEY:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }	
}
