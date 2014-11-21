package com.datawhore.news_whore;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;


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
