package com.chilloutrecords.models;

import androidx.annotation.Keep;

public class VideoModel {

  public String id = "";
  public String art = "";
  public String info = "";
  public String lyrics = "";
  public String name = "";
  public String url = "";

  public int play_count;

  public long release_date;
  public boolean is_live;

  @Keep
  public VideoModel(){
    // Default constructor required for calls to DataSnapshot.getValue(VideoModel.class)
  }

}
