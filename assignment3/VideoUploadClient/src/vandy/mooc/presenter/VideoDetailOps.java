package vandy.mooc.presenter;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import vandy.mooc.R;
import vandy.mooc.common.ConfigurableOps;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.common.Utils;
import vandy.mooc.model.provider.Video;
import vandy.mooc.model.provider.VideoClient;
import vandy.mooc.model.provider.VideoController;
import vandy.mooc.model.services.DownloadVideoService;
import vandy.mooc.provider.VideoProviderHelper;
import vandy.mooc.utils.VideoStorageUtils;
import vandy.mooc.view.VideoDetailActivity;

/**
 * This class implements all the Video-related operations.  It
 * implements ConfigurableOps so it can be created/managed by the
 * GenericActivity framework.  It implements Callback so it can serve
 * as the target of an asynchronous Retrofit RPC call.  It extends
 * GenericAsyncTaskOps so its doInBackground() method runs in a
 * background task.
 */
public class VideoDetailOps
       implements ConfigurableOps, GenericAsyncTaskOps<Double, Void, Video> {
    /**
     * Debugging tag used by the Android logger.
     */
    private static final String TAG =
        VideoDetailOps.class.getSimpleName();
    
    /**
     * Used to enable garbage collection.
     */
    private WeakReference<VideoDetailActivity> mActivity;
    private WeakReference<ImageView> mThumbnailImageView;
    private WeakReference<TextView> mTitleTextView;
    private WeakReference<RatingBar> mRankRatingBar;
    private WeakReference<Button> mDownloadButton;
    private WeakReference<Button> mPlayButton;
    
    
    /**
     *  It allows access to application-specific resources.
     */
    private Context mApplicationContext;
    
    
    /**
     * VideoController mediates the communication between Server and
     * Android Storage.
     */
    VideoController mVideoController;
    
    /**
     * Meta-data video
     */
    VideoClient mVideo;
    
    VideoProviderHelper mVideoProvider;
    
    private GenericAsyncTask<Double, Void, Video, VideoDetailOps> mAsyncTask;
    
    /**
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public VideoDetailOps() {
    }
    
    /**
     * Called after a runtime configuration change occurs to finish
     * the initialisation steps.
     */
    public void onConfiguration(Activity activity,
                                boolean firstTimeIn) {
        final String time =
            firstTimeIn 
            ? "first time" 
            : "second+ time";
        
        Log.d(TAG,
              "onConfiguration() called the "
              + time
              + " with activity = "
              + activity);

        // (Re)set the mActivity WeakReference.
        mActivity =
            new WeakReference<>((VideoDetailActivity) activity);
        
        mThumbnailImageView = new WeakReference<>(
        					(ImageView) activity.findViewById(R.id.thumbnail));
        
        mTitleTextView = new WeakReference<>(
				(TextView) activity.findViewById(R.id.title));
        
        mRankRatingBar = new WeakReference<>(
				(RatingBar) activity.findViewById(R.id.rank));
        
        mDownloadButton = new WeakReference<>(
				(Button) activity.findViewById(R.id.btn_download));
        
        mPlayButton = new WeakReference<>(
				(Button) activity.findViewById(R.id.btn_play));

        mRankRatingBar.get().setOnRatingBarChangeListener(new OnRatingBarChangeListener(){
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				// TODO Auto-generated method stub
				mAsyncTask = new GenericAsyncTask<>(VideoDetailOps.this);
				mAsyncTask.execute(Double.valueOf(rating));
			}
		});
        
        if (firstTimeIn) {
            // Get the Application Context.
            mApplicationContext =
                activity.getApplicationContext();
            
            mVideoProvider = new VideoProviderHelper(mApplicationContext);
            
            Video video = activity.getIntent().getParcelableExtra(VideoDetailActivity.VIDEO_DATA);
            mVideo = mVideoProvider.getVideo(video.getId());
            if(mVideo==null){
            	mVideo = new VideoClient(video);
            }else
            {
            	mVideo.refresh(video);
            	mVideoProvider.addVideo(mVideo);
            }
            
            // Create VideoController that will mediate the
            // communication between Server and Android Storage.
            Log.d(TAG, "Creating VideoController");
            mVideoController =
                new VideoController(mApplicationContext);
            
            
        }
        
        displayVideo();
    }

    /**
     * Display the Videos in ListView
     * 
     * @param videos
     */
    public void displayVideo() {
        if (mVideo != null) {
            if(mVideo.getFilepath()!=null&&
            		!mVideo.getFilepath().isEmpty()&&
            		VideoStorageUtils.fileExists(mVideo.getFilepath()))
            {
            	mDownloadButton.get().setEnabled(false);
            	mPlayButton.get().setEnabled(true);
            	mThumbnailImageView.get().setImageBitmap(
            			ThumbnailUtils.createVideoThumbnail(
            					mVideo.getFilepath(), 
            					MediaStore.Video.Thumbnails.MICRO_KIND));
            }
            else
            {
            	mDownloadButton.get().setEnabled(true);
            	mPlayButton.get().setEnabled(false);
            }
        	
        	
            mTitleTextView.get().setText(mVideo.getTitle());
            
            
        } else {
            Utils.showToast(mActivity.get(),
                           "Please connect to the Video Service");
            
            mActivity.get().finish();
        }

    }
    
    public void displayRating() {
        mRankRatingBar.get().setIsIndicator(true);
        mRankRatingBar.get().setOnRatingBarChangeListener(null);
        mRankRatingBar.get().setRating((float) mVideo.getRating());
        Log.d(TAG, "Rating: "+mVideo.getRating()+" Count: "+mVideo.getCount());
        displayVideo();
    }
    
    
    /**
     * Start a service that Uploads the Video having given Id.
     *   
     * @param videoId
     */
    public void downloadVideo(Long videoId){
        mApplicationContext.startService
            (DownloadVideoService.makeIntent 
                 (mApplicationContext,
                  videoId));
    }

	public void downloadVideo() {
		downloadVideo(mVideo.getId());
	}

	public void playVideo() {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mVideo.getFilepath()));
		intent.setDataAndType(Uri.parse(mVideo.getFilepath()), mVideo.getContentType());
		mActivity.get().startActivity(intent);
	}

	public void refreshVideo(String path) {
		if(path!=null){
			mVideo.setFilepath(path);
			mVideoProvider.addVideo(mVideo);
			displayVideo();
		}else{
			Toast.makeText(mActivity.get(), "Download Failed", Toast.LENGTH_LONG)
			.show();
		}
	}

	@Override
	public Video doInBackground(Double... params) {
		return mVideoController.rateVideo(mVideo.getId(), params[0]);
	}

	@Override
	public void onPostExecute(Video result) {
		if(result!=null){
			mVideo.refresh(result);
			displayRating();
		}else{
			Toast.makeText(mApplicationContext, "Couldnt Connect to the server", Toast.LENGTH_LONG).show();
		}
	}
}
