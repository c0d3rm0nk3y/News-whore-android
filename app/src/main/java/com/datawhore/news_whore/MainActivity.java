package com.datawhore.news_whore;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;

import java.io.IOException;


public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

  private void pickUserAccount() {
    String[] accountTypes = new String[]{"com.google"};
    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
        accountTypes, false, null, null, null, null);
    startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
  }

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

  private static final String TAG = "RetrieveAccessToken";
  private static final int REQ_SIGN_IN_REQUIRED = 55664;

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
