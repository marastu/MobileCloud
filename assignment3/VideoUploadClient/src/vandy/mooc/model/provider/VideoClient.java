/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package vandy.mooc.model.provider;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import android.os.Parcel;
import android.os.Parcelable;


public class VideoClient implements Parcelable {

	private long id;
	private String title;
	private long duration;
	private String location;
	private String subject;
	private String contentType;
	private double rating;
	private int count;
	private String filepath;

	@JsonIgnore
	private String dataUrl;

	public VideoClient(){}
	
	public VideoClient(Video video){
		id = video.getId();
		title = video.getTitle();
		duration = video.getDuration();
		location = video.getDataUrl();
		subject = video.getSubject();
		contentType = video.getContentType();
		rating = video.getRating();
		count = video.getCount();
		filepath = "";
	}
	
	public VideoClient(Parcel in) {
		id = in.readLong();
		title = in.readString();
		duration = in.readLong();
		location = in.readString();
		subject = in.readString();
		contentType = in.readString();
		rating = in.readDouble();
		count = in.readInt();
		filepath = in.readString();
	}
	
	public void refresh(Video video)
	{
		id = video.getId();
		title = video.getTitle();
		duration = video.getDuration();
		location = video.getDataUrl();
		subject = video.getSubject();
		contentType = video.getContentType();
		rating = video.getRating();
		count = video.getCount();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@JsonProperty
	public String getDataUrl() {
		return dataUrl;
	}

	@JsonIgnore
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getTitle(), getDuration());
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof VideoClient)
				&& Objects.equals(getTitle(), ((VideoClient) obj).getTitle())
				&& getDuration() == ((VideoClient) obj).getDuration();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(title);
		dest.writeLong(duration);
		dest.writeString(location);
		dest.writeString(subject);
		dest.writeString(contentType);
		dest.writeDouble(rating);
		dest.writeInt(count);
		dest.writeString(filepath);
		
	}
	
	public static final Parcelable.Creator<VideoClient> CREATOR =
	        new Parcelable.Creator<VideoClient>() {
	            public VideoClient createFromParcel(Parcel in) {
	                return new VideoClient(in);
	            }

	            public VideoClient[] newArray(int size) {
	                return new VideoClient[size];
	            }
	        };

}
