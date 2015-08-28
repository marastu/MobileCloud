package vandy.mooc.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import vandy.mooc.model.provider.VideoClient;
import vandy.mooc.provider.VideoContract.VideoEntry;

public class VideoProviderHelper {
	
	Context mContext;
	ContentResolver mContentResolver;
	
	public VideoProviderHelper(Context context){
		mContext = context;
		mContentResolver = context.getContentResolver();
	}
	
	public VideoClient getVideo(long id) {
		
		final String SELECTION_VIDEO = 
	            VideoEntry._ID
	            + " = ?";
	    	
	        // Initializes an array to contain selection arguments.
	        String[] selectionArgs = { String.valueOf(id) };
		
		try(Cursor cursor = mContentResolver.query(VideoEntry.CONTENT_URI,
									null,
									SELECTION_VIDEO, 
									selectionArgs, 
									null))
		{
			
			if (!cursor.moveToFirst())
                return null;
			else
			{
				return getVideoFromCursor(cursor);
			}
			
			
		}
	}
	
	private VideoClient getVideoFromCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		VideoClient videoClient = new VideoClient();
		
		videoClient.setId(cursor.getLong(
							cursor.getColumnIndex(VideoEntry._ID)));
		
		videoClient.setTitle(cursor.getString(
				cursor.getColumnIndex(VideoEntry.COLUMN_TITLE)));
		
		videoClient.setDuration(cursor.getLong(
				cursor.getColumnIndex(VideoEntry.COLUMN_DURATION)));
		
		videoClient.setContentType(cursor.getString(
				cursor.getColumnIndex(VideoEntry.COLUMN_CONTENTTYPE)));
		
		videoClient.setDataUrl(cursor.getString(
				cursor.getColumnIndex(VideoEntry.COLUMN_DATAURL)));
		
		videoClient.setRating(cursor.getDouble(
				cursor.getColumnIndex(VideoEntry.COLUMN_RATING)));
		
		videoClient.setCount(cursor.getInt(
				cursor.getColumnIndex(VideoEntry.COLUMN_COUNT)));
		
		videoClient.setFilepath(cursor.getString(
				cursor.getColumnIndex(VideoEntry.COLUMN_FILEPATH)));
		
		return videoClient;
	}

	public List<VideoClient> getVideos() {
		
		List<VideoClient> result = new ArrayList<>();
		
		try(Cursor cursor = mContentResolver.query(VideoEntry.CONTENT_URI,
									null,
									null, 
									null, 
									null))
		{
			
			if (!cursor.moveToFirst())
                return null;
			else
			{
				do{
					result.add(getVideoFromCursor(cursor));
				}while(cursor.moveToNext());
			}
			
			
		}
		
		
		return result;
	}
	
	public boolean addVideo(VideoClient video)
	{
		
		ContentValues cv = new ContentValues();
		cv.put(VideoEntry._ID, video.getId());
		cv.put(VideoEntry.COLUMN_TITLE, (video.getTitle()==null)?"":video.getTitle());
    	cv.put(VideoEntry.COLUMN_DURATION, video.getDuration());
    	cv.put(VideoEntry.COLUMN_CONTENTTYPE, (video.getContentType()==null)?"":video.getContentType());
    	cv.put(VideoEntry.COLUMN_DATAURL, (video.getDataUrl()==null)?"":video.getDataUrl());
    	cv.put(VideoEntry.COLUMN_RATING, video.getRating());
    	cv.put(VideoEntry.COLUMN_COUNT, video.getCount());
    	cv.put(VideoEntry.COLUMN_FILEPATH, (video.getFilepath()==null)?"":video.getFilepath());
    	
    	
		if(getVideo(video.getId())==null)
		{
	    	if(mContentResolver.insert(VideoEntry.CONTENT_URI, cv)!=null)
	    		return true;
			
		}else
		{
			final String WHERE = VideoEntry._ID
		            + " = ?";
			String[] selectionArgs = { String.valueOf(video.getId()) };
			
	    	if(mContentResolver.update(VideoEntry.CONTENT_URI, cv, WHERE, selectionArgs)>0)
	    		return true;
		}
		
		return false;
	}
	
	
}
