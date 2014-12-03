package com.datawhore.news_whore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;


public class ReadActivity extends Activity implements WebServiceListener {
  private NewsWhoreAPI nWhore = null;
  private static final String TAG = "News-whore-Read";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_read);

    // check if start or restart
    if(savedInstanceState != null) {
      // restart
      Log.i(TAG, "restart..");
    } else {
      // first run
      Log.i(TAG, "first run");
      //checkIntentForShare(getIntent());
    }
  }

  private void checkIntentForShare(Intent intent) {
    String action = intent.getAction();
    String type   = intent.getType();
    nWhore = new NewsWhoreAPI(this);
    if(Intent.ACTION_SEND.equals(action) && type != null) {
      if("text/plain".equals(type)) {
        // sharing
        String s = intent.getStringExtra(Intent.EXTRA_TEXT);
        String link = extractLinks(s)[0];

        // now send off for
        Log.i(TAG, "link: " + link);
      }
    }
  }

  public static String[] extractLinks(String text) {
    Log.i(TAG, text);
    List<String> links = new ArrayList<String>();
    Matcher m = Patterns.WEB_URL.matcher(text);
    while (m.find()) {
      String url = m.group();
      Log.d(TAG, "URL extracted: " + url);
      links.add(url);
    }

    return links.toArray(new String[links.size()]);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_read, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onFileDownloadComplete(String content) {

  }

  @Override
  public void onTokenDownloadComplete(String token) {

  }
}
