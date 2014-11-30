package com.datawhore.news_whore;

import android.accounts.AccountManager;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by root on 11/29/14.
 */
public class WebDownloadClient extends AsyncTask<String, Void, String> {
  private static final String TAG = "Monkey-Whore";
  WebServiceListener listener = null;

  public WebDownloadClient(WebServiceListener listener) {
    this.listener = listener;
  }

  @Override
  protected String doInBackground(String... params) {
    Log.i(TAG, "WebDownloadClient.AsyncTask.doInBackground()..");
    // pick account
    String link = params[0];
    String email = params[1];
    String token = params[2];
    //String token = getToken(email);
    String sUri = "http://monkey-nodejs-71725.usw1.nitrousbox.com:8080/submitArticle/?link=" + link + "&token=" + token;
    String url = null;
    try {
      url = URLEncoder.encode(sUri, "UTF-8");
      Log.i(TAG, sUri);
    } catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    String result = getFromWeb(sUri);

    return result;
  }

  String getFromWeb(String url) {
    StringBuilder builder = new StringBuilder();
    HttpClient client = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(url);
    try {
      HttpResponse response = client.execute(httpGet);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      Log.i(TAG, "status code: " + statusCode);
      Log.i(TAG, "status line: " + statusLine);
      if(statusCode == 200) {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        while((line = reader.readLine()) != null) {
          builder.append(line);
        }
      } else {
        Log.e(TAG, "failed to download file");
      }
    }catch(ClientProtocolException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    }
    return builder.toString();
  }

//  private String getToken(String mEmail) {
//    String result = null;
//    String scopes = "oauth2:profile email";
//    String token = null;
//    try {
//      //token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
//    } catch (IOException e) {
//      Log.e(TAG, e.getMessage());
//    } catch (UserRecoverableAuthException e) {
//      //startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
//    } catch (GoogleAuthException e) {
//      Log.e(TAG, e.getMessage());
//    }
//    return token;
//    return result;
//  }



  private String convertStreamToString(InputStream is) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();
    String line = null;
    try {
      while((line = reader.readLine()) != null) {
        sb.append(line);
        Log.i(TAG, line);
      }
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      try {
        is.close();
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    return sb.toString();
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    //Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
    //((TextView)findViewById(R.id.status)).setText(s);

    listener.onFileDownloadComplete(s);
  }
}
