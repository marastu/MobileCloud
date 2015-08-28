package vandy.mooc.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.model.services.DownloadVideoService;
import vandy.mooc.presenter.VideoDetailOps;
import vandy.mooc.view.ui.FloatingActionButton;

/**
 * This Activity can be used upload a selected video to a Video
 * Service and also displays a list of videos available at the Video
 * Service.  The user can record a video or get a video from gallery
 * and upload it.  This Activity extends GenericActivity, which
 * provides a framework that automatically handles runtime
 * configuration changes.  It implements OnVideoSelectedListener that
 * will handle callbacks from the UploadVideoDialog Fragment.
 */
public class VideoDetailActivity 
       extends GenericActivity<VideoDetailOps>  {

	public static final String VIDEO_DATA =
            "VIDEO_DATA";
	
    public static final String ACTION_VIDEO_DETAIL =
            "vandy.mooc.services.VideoDetailActivity";

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from UploadVideoService when a video upload completes.
     */
    private DownloadResultReceiver mDownloadResultReceiver;
    
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., storing Views.
     * 
     * @param Bundle
     *            object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the default layout.
        setContentView(R.layout.activity_video_detail);

        // Register BroadcastReceiver that receives result from
        // UploadVideoService when a video upload completes.
        registerReceiver();

        // Call up to the special onCreate() method in
        // GenericActivity, passing in the VideoOps class to
        // instantiate and manage.
        super.onCreate(savedInstanceState,
                       VideoDetailOps.class);
    }

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from UploadVideoService. 
     */
    public class DownloadResultReceiver 
           extends BroadcastReceiver {
    	public static final String PATH = "path";
        /**
         * Hook method that's dispatched when the UploadService has
         * uploaded the Video.
         */
        @Override
        public void onReceive(Context context,
                              Intent intent) {
            // Starts an AsyncTask to get fresh Video list from the
            // Video Service.
        	String path = intent.getStringExtra(PATH);
            getOps().refreshVideo(path);
        }
    }

    /**
     * Register a BroadcastReceiver that receives a result from the
     * UploadVideoService when a video upload completes.
     */
    private void registerReceiver() {
        // Receiver for the notification.
        mDownloadResultReceiver = new DownloadResultReceiver();

        // Create an Intent filter that handles Intents from the
        // UploadVideoService.
        IntentFilter intentFilter =
            new IntentFilter(DownloadVideoService.ACTION_DOWNLOAD_SERVICE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.
        registerReceiver(mDownloadResultReceiver,
                         intentFilter);
    }

    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads. onDestroy() may not always be called-when
     * system kills hosting process
     */
    @Override
    protected void onDestroy() {
        // Call destroy in superclass.
        super.onDestroy();

        // Unregister BroadcastReceiver.
        unregisterReceiver(mDownloadResultReceiver);
    }
    
    public void downloadVideo(View view) {
		getOps().downloadVideo();
	}
    
    public void playVideo(View view) {
		getOps().playVideo();;
	}

}
