package com.datawhore.news_whore;

import java.util.ArrayList;

/**
 * Created by root on 11/29/14.
 */
public interface WebServiceListener {
  public void onFileDownloadComplete(String content);

  public void onTokenDownloadComplete(String token);
}
