package gurpreetsk.me.selfiegeek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import static gurpreetsk.me.selfiegeek.utils.KeyConstants.SHARED_PREF_KEY;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    public Client mKinveyClient = null;

    SharedPreferences userID;

    MaterialEditText ETusername, ETpassword;
    Button BTNlogin;
    TextView TVsignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //One time Kinvey client init
        mKinveyClient = new Client.Builder(getApplicationContext()).build();

        userID = this.getSharedPreferences(getPackageName(), MODE_PRIVATE);

        if (mKinveyClient.user().isUserLoggedIn()) {
            startActivity(new Intent(UserActivity.this, GridActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_user);
            getHandles();
        }
    }

    public void loginUser(View v) {

        String username = ETusername.getText().toString();
        String password = ETpassword.getText().toString();
        if (username.length() < 4 || password.length() < 4)
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        else {
            mKinveyClient.user().login(username, password, new KinveyUserCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.i(TAG, "onSuccess: User logged-in  " + user.getId());
                    userID.edit().putString(SHARED_PREF_KEY, user.getId()).apply();
                    Toast.makeText(UserActivity.this, "Hi " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserActivity.this, GridActivity.class));
                    finish();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Toast.makeText(UserActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onFailure: Login  " + throwable);
                }
            });
        }
    }

    public void signupUser(View v) {
        String username = ETusername.getText().toString();
        String password = ETpassword.getText().toString();
        if (username.length() < 4 || password.length() < 4)
            Toast.makeText(this, "Please input credentials to sign-up", Toast.LENGTH_SHORT).show();
        else {
            mKinveyClient.user().create(username, password, new KinveyUserCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.i(TAG, "onSuccess: User created  " + user.getId());
                    userID.edit().putString(SHARED_PREF_KEY, user.getId()).apply();
                    Toast.makeText(UserActivity.this, "Hi " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserActivity.this, GridActivity.class));
                    finish();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Toast.makeText(UserActivity.this, "Signup failed\nUsername already exists", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onFailure: Signup  " + throwable);
                }
            });
        }
    }

    private void getHandles() {
        ETusername = (MaterialEditText) findViewById(R.id.edittext_username);
        ETpassword = (MaterialEditText) findViewById(R.id.edittext_password);
        BTNlogin = (Button) findViewById(R.id.button_login);
        TVsignup = (TextView) findViewById(R.id.textview_signup);
    }

}
