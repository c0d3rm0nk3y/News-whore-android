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
public class NewsWhoreAPI extends AsyncTask<String, Integer, String> {
  private static final String TAG = "NewsWhoreAPI";
  private WebServiceListener listener = null;

  public NewsWhoreAPI(WebServiceListener listener) {
    this.listener = listener;
  }

  @Override
  protected String doInBackground(String... strings) {
    Log.i(TAG, "NewsWhoreAPI.doInBackground().." + strings);
    // GET POST       : /api/articles
    // GET PUT DELETE : /api/articles/:articles_id
    // strings[] = [get|post|put|delete, articles_id (optional)]
    String method = strings[0];
    String baseUri = "http://monkey-nodejs-71725.usw1.nitrousbox.com:8080/api/articles/";
    String result = "";
    String username = "datawhore";
    String password = "monkeyFun1!";

    // figure out which path in the api to take

    if(strings.length == 1) { // GET or POST
      if(method.equals("GET")) {
        Log.i(TAG, "GET submitted, no link included, getting all articles..");
        HttpUriRequest request = new HttpGet(baseUri);
        String credentials = username + ":" + password;
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        request.addHeader("Authorization", "Basic " + base64EncodedCredentials);

        try {
          HttpClient httpClient = new DefaultHttpClient();
          HttpResponse response = httpClient.execute(request);
          String e = EntityUtils.toString(response.getEntity());
          Log.d(TAG," login: Response: " + e);
          return e;
        }catch (ClientProtocolException e) {
          e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }
      }else if(method.equals("POST")) {

      }
    } else if(strings.length == 2) {  // GET|PUT|DELETE & articles_id
      if(method.equals("GET")) {

      } else if(method.equals("PUT")) {

      } else if(method.equals("DELETE")) {

      }
    }
    return result;
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);

    listener.onFileDownloadComplete(s);
  }
}
