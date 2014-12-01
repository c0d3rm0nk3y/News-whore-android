package com.datawhore.news_whore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class ReadActivity extends Activity implements WebServiceListener {
  private NewsWhoreAPI nWhore = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_read);

    // check if start or restart
    if(savedInstanceState != null) {
      // restart
    } else {
      // first run
      checkIntentForShare(getIntent());
    }
  }

  private void checkIntentForShare(Intent intent) {
    String action = intent.getAction();
    String type   = intent.getType();

    if(Intent.ACTION_SEND.equals(action) && type != null) {
      if("text/plain".equals(type)) {
        // sharing
      }
    }
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
