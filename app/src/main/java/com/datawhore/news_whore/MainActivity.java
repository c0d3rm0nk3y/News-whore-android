package com.datawhore.news_whore;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity {
  private static final String TAG = "Monkey-Whore";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    checkIntent(getIntent());
  }

  private void checkIntent(Intent intent) {
    String action = intent.getAction();
    String type   = intent.getType();
    Log.i(TAG, "Action: " + action + ", Type: " + type);
    if(Intent.ACTION_SEND.equals(action) && type != null) {
      if("text/plain".equals(type)) {

        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.i(TAG, "text: " + text);
        if(text != null) {
          String[] links = extractLinks(text);
          if(links.length > 0) {
            Log.i(TAG, "Url detected, " + links[0]);
            ((TextView)findViewById(R.id.status)).setText(links[0]);
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

  private String extractURL(String text) {
    return null;
  }

  static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

  private void pickUserAccount() {
    String[] accountTypes = new String[]{"com.google"};
    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
        accountTypes, false, null, null, null, null);
    startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
  }

  private static final int REQ_SIGN_IN_REQUIRED = 55664;
  String mEmail; // Received from newChooseAccountIntent(); passed to getToken()

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
      // Receiving a result from the AccountPicker
      if (resultCode == RESULT_OK) {
        mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
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

  private void getUsername() {
    new RetrieveTokenTask().execute(mEmail);
  }


  private class RetrieveTokenTask extends AsyncTask<String, Void, String> {
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
      ((TextView)findViewById(R.id.status)).setText(s);
    }
  }

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
