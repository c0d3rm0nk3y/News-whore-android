package com.datawhore.news_whore;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements WebServiceListener {
  private static final String TAG = "Monkey-Whore";
  private static final String PREF_NAME = "Pref";
  private String token = null;
  private static final int REQ_SIGN_IN_REQUIRED = 55664;
  static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
  private WebDownloadClient wdc = new WebDownloadClient(this);
  private SharedPreferences sharedPref = null;
  private SharedPreferences.Editor editor = null;

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(TAG, "onStart()\ntitle: " + this.title + "\ntext: " + this.text);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putString("title", title);
    outState.putString("text", text);
    Log.i(TAG, "onSaveInstanceState()\ntitle: " + this.title + "\ntext: " + this.text);
    // you go last..
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    Log.i(TAG, "onConfigurationChanged()..\ntitle: " + this.title + "\ntext: " + this.text + "\n\n");
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    this.title = savedInstanceState.getString("title");
    this.text = savedInstanceState.getString("text");

    Log.i(TAG, "onRestoreInstanceState()..\ntitle: " + this.title + "\ntext: " + this.text);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    iniPref();
    if(savedInstanceState != null) {
      // restoring from prev instance
      Log.i(TAG, "alright, we have been through this, don't get token or any of that crap..");
      Log.i(TAG, "onCreate()..\ntitle: " + this.title + "\ntext: " + this.text);
    } else {
      // first run!
      Log.i(TAG, "first run!!  lets get that token..");
      checkIntent(getIntent());
    }


  }

  // process incoming Text for URLs
  private void checkIntent(Intent intent) {
    String action = intent.getAction();
    String type   = intent.getType();
    Log.i(TAG, "Action: " + action + ", Type: " + type);
    if(Intent.ACTION_SEND.equals(action) && type != null) {
      if("text/plain".equals(type)) {
        Toast.makeText(getBaseContext(), "Incoming text: " + intent.getStringExtra(Intent.EXTRA_TEXT), Toast.LENGTH_LONG).show();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);

        Log.i(TAG, "text: " + text);
        if(text != null) {
          String[] links = extractLinks(text);
          if(links.length > 0) {
            Log.i(TAG, "Url detected, " + links[0]);

            Toast.makeText(getBaseContext(), "Url detected: " + links[0], Toast.LENGTH_SHORT).show();
            this.mLink = links[0];
            if(mEmail == null) { pickUserAccount(); }
            getUsername();
          } else {
            Log.i(TAG, "No, Url Detected..");
          }
        }
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

  // get the acct no (if not already chosen), and get updated refresh token

  private void pickUserAccount() {
    if(sharedPref.contains("acct") && !sharedPref.getString("acct",null).equals(null)) {
      mEmail = sharedPref.getString("acct",null);

    } else {
      String[] accountTypes = new String[]{"com.google"};
      Intent intent = AccountPicker.newChooseAccountIntent(null, null,
          accountTypes, false, null, null, null, null);
      startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }
  }

  private void iniPref() {
    try {

      sharedPref = getPreferences(Context.MODE_PRIVATE);
      editor = sharedPref.edit();
      mEmail = sharedPref.getString("acct", null);
      ((TextView)findViewById(R.id.status)).setText(mEmail);
      Toast.makeText(getBaseContext(),"email from pref is: " + mEmail, Toast.LENGTH_LONG).show();
      Log.i(TAG, "Email in pref is: " + mEmail);
    }catch(Exception e) {

    }
  }

  String mEmail; // Received from newChooseAccountIntent(); pgoogleassed to getToken()
  String mLink;
  String mToken;

  private void getUsername() {
    new RetrieveTokenTask(this).execute(mEmail);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
      // Receiving a result from the AccountPicker
      if (resultCode == RESULT_OK) {
        mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        editor.putString("acct", mEmail);
        editor.commit();
        Log.i(TAG, "wrote email to acct: " + sharedPref.getString("acct", null));
        ((TextView)findViewById(R.id.status)).setText(mEmail);
        // With the account name acquired, go get the auth token
        getUsername();
      } else if (resultCode == RESULT_CANCELED) {
        // The account picker dialog closed without selecting an account.
        // Notify users that they must pick an account to proceed.
        Toast.makeText(this, getString(R.string.pick_account), Toast.LENGTH_SHORT).show();
      }
    }
    // Later, more code will go here to handle the result from some exceptions...
  }

  String title = null;
  String text = null;

  @Override
  public void onFileDownloadComplete(String result) {
    // here is where the server response should be..
    Log.i(TAG, "file download result: " + result);
    try {
      JSONObject reader = new JSONObject(result);
      // title content text
      this.title = reader.getString("title");
      this.text = reader.getString("text");
      ((TextView)findViewById(R.id.status)).setText(title);
      ((TextView)findViewById(R.id.content)).setText(text);
    } catch(JSONException e) { e.printStackTrace(); }
  }

  @Override
  public void onTokenDownloadComplete(String token) {
    Log.i(TAG, "token obtained: " + token);
    this.mToken = token;
    this.wdc.execute(mLink, mEmail, mToken);
  }

  private class RetrieveTokenTask extends AsyncTask<String, Void, String> {
    WebServiceListener listener;

    public RetrieveTokenTask(WebServiceListener listener) { this.listener = listener; }
    @Override
    protected String doInBackground(String... params) {
      String accountName = params[0];
      String scopes = "oauth2:profile email";
      String token = null;
      try {
        token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
      } catch (IOException e) {
        Log.e(TAG, e.getMessage());
      } catch (UserRecoverableAuthException e) {
        startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
      } catch (GoogleAuthException e) {
        Log.e(TAG, e.getMessage());
      }
      return token;
    }

    @Override
    protected void onPostExecute(String s) {
      super.onPostExecute(s);
      Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
      //((TextView)findViewById(R.id.status)).setText(s);
      listener.onTokenDownloadComplete(s);

    }
  }

  /*
  * Next steps.. sharedPreferences.. save Acct No.
  * */

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
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
      pickUserAccount();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
