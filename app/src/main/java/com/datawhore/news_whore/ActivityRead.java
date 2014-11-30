package com.datawhore.news_whore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class ActivityRead extends Activity {
  private static final String TAG = "ActivityRead: ";



  @Override
  protected void onSaveInstanceState(Bundle outState) {
    // here we can save stuff
    outState.putString("key", "value");

    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onStart() {
    super.onStart();
    //Log.i(TAG, "onStart()..");
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    //Log.i(TAG, "onPostResume()..");
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  protected void onStop() {
    super.onStop();
    // completely hidden and not visible in any way to user.  in background.  all state information
    // from activity instance is retained but no code is executing
    //Log.i(TAG, "onStop()..");
  }

  @Override
  protected void onPause() {
    super.onPause();
    // partially obscured and cannot be interacted with

    // here is where video or music would be paused or the camera released
    //Log.i(TAG, "onPause()..");
  }

  @Override
  protected void onRestart() {
    super.onRestart();

    //Log.i(TAG, "onRestart()..");

  }

  @Override
  protected void onResume() {
    super.onResume();
    // 'resumed' activity is in the foreground and can be interacted with
    // here is where music or video would resume, or camera would be re-acquired
    //Log.i(TAG, "onResume..");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate()..");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_activity_read);

    Intent intent = getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    Log.i(TAG, "onCreate().. Checking intent, action: " + action + ", type: " + type);

    // check whether we're recreating a previously destroyed instance
    if(savedInstanceState != null) {
      Log.i(TAG, "onCreate().. recreating prev instance..");
      // here we can reload whatever.. not entire sure what is still there... going to have to find out..
    } else {
      Log.i(TAG, "onCreate().. this is the first run..");
      // Here we can pull down the token

    }

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_activity_read, menu);
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
}
