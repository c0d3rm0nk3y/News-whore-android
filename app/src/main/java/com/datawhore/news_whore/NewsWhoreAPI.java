package com.datawhore.news_whore;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by root on 12/1/14.
 */
public class NewsWhoreAPI extends AsyncTask<String, Void, String> {
  private static final String TAG = "NewsWhoreAPI";
  private WebServiceListener listener = null;

  public NewsWhoreAPI(WebServiceListener listener) {
    this.listener = listener;
  }

  @Override
  protected String doInBackground(String... strings) {
    String result = "";
    String username = "datawhore";
    String password = "monkeyFun!";
    String getArticles = "http://monkey-nodejs-71725.usw1.nitrousbox.com:8080/api/articles";
    HttpUriRequest request = new HttpGet(getArticles);
    String credentials = username + ":" + password;
    String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    request.addHeader("Authorization", "Basic " + base64EncodedCredentials);

    try {
      HttpClient httpClient = new DefaultHttpClient();
      HttpResponse response = httpClient.execute(request);
      Log.d(TAG + " login: Response", EntityUtils.toString(response.getEntity()));
      result = EntityUtils.toString(response.getEntity());
    }catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) { e.printStackTrace(); }
    return result;
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);

    listener.onFileDownloadComplete(s);
  }
}
