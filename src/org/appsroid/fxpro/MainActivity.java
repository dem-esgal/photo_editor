package org.appsroid.fxpro;

import java.lang.reflect.Method;

import org.appsroid.fxpro.library.Constants;
import org.appsroid.fxpro.library.Toaster;
import org.appsroid.fxpro.library.UriToUrl;
import org.appsroid.fxpro.utils.CameraHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class MainActivity extends Activity {

	private Animation      animation;
	private RelativeLayout top_holder;
	private RelativeLayout bottom_holder;
	private RelativeLayout step_number;
	private Uri            imageUri;
	private boolean click_status = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		top_holder = (RelativeLayout) findViewById(R.id.top_holder);
		bottom_holder = (RelativeLayout) findViewById(R.id.bottom_holder);
		step_number = (RelativeLayout) findViewById(R.id.step_number);
	}

	@Override
	protected void onStart() {
		overridePendingTransition(0, 0);
		flyIn();
		super.onStart();
	}

	@Override
	protected void onStop() {
		overridePendingTransition(0, 0);
		super.onStop();
	}

	public void startGallery(View view) {
		flyOut("displayGallery");
	}

	public void startCamera(View view) {
		flyOut("displayCamera");
	}

	@SuppressWarnings("unused")
	private void displayGallery() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !Environment.getExternalStorageState().equals(Environment.MEDIA_CHECKING)) {
			Intent intent = new Intent();
			intent.setType("image/jpeg");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, Constants.REQUEST_GALLERY);
		} else {
			Toaster.make(getApplicationContext(), R.string.no_media);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CAMERA) {
			try{
				if (resultCode == RESULT_OK) {
					displayPhotoActivity(1);
				} else {
					UriToUrl.deleteUri(getApplicationContext(), imageUri);
				}
			} catch (Exception e) {
				Toaster.make(getApplicationContext(), R.string.error_img_not_found);
			}
		} else if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_GALLERY) {
			try {
				imageUri = data.getData();
				displayPhotoActivity(2);
			} catch (Exception e) {
				Toaster.make(getApplicationContext(), R.string.error_img_not_found);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void displayCamera() {
		imageUri = getOutputMediaFile();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, Constants.REQUEST_CAMERA);
	}
	
	private Uri getOutputMediaFile(){
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "Camera Pro");
		values.put(MediaStore.Images.Media.DESCRIPTION, "www.appsroid.org");
		return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	private void displayPhotoActivity(int source_id) {
		Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
		intent.putExtra(Constants.EXTRA_KEY_IMAGE_SOURCE, source_id);
		intent.setData(imageUri);
		startActivity(intent);
		overridePendingTransition(0, 0);
		finish();
	}

	private void flyOut(final String method_name) {
		if (click_status) {
			click_status = false;
			
			animation = AnimationUtils.loadAnimation(this, R.anim.step_number_back);
			step_number.startAnimation(animation);
	
			animation = AnimationUtils.loadAnimation(this, R.anim.holder_top_back);
			top_holder.startAnimation(animation);
	
			animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom_back);
			bottom_holder.startAnimation(animation);
	
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}
	
				@Override
				public void onAnimationRepeat(Animation arg0) {
				}
	
				@Override
				public void onAnimationEnd(Animation arg0) {
					callMethod(method_name);
				}
			});
		}
	}

	private void callMethod(String method_name) {
		if (method_name.equals("finish")) {
			overridePendingTransition(0, 0);
			finish();
		} else {
			try {
				Method method = getClass().getDeclaredMethod(method_name);
				method.invoke(this, new Object[] {});
			} catch (Exception e) {}
		}
	}

	@Override
	public void onBackPressed() {
		flyOut("finish");
		super.onBackPressed();
	}

	private void flyIn() {
		click_status = true;
		
		animation = AnimationUtils.loadAnimation(this, R.anim.holder_top);
		top_holder.startAnimation(animation);

		animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom);
		bottom_holder.startAnimation(animation);

		animation = AnimationUtils.loadAnimation(this, R.anim.step_number);
		step_number.startAnimation(animation);
	}

}
