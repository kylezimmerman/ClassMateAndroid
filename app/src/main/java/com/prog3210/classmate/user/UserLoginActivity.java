package com.prog3210.classmate.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.twitter.Twitter;
import com.prog3210.classmate.MainActivity;
import com.prog3210.classmate.R;
import com.prog3210.classmate.core.BaseActivity;
import com.prog3210.classmate.core.ClassmateUser;
import com.prog3210.classmate.courses.Course;

import java.util.ArrayList;
import java.util.List;

public class UserLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        Button login = (Button) findViewById(R.id.login_button);
        Button register = (Button) findViewById(R.id.register_button);

        login.setOnClickListener(attemptLogin);
        register.setOnClickListener(registerUser);

        Button facebookLoginButton = (Button)findViewById(R.id.facebook_login);
        facebookLoginButton.setOnClickListener(facebookLogin);

        Button twitterLoginButton = (Button)findViewById(R.id.twitter_login);
        twitterLoginButton.setOnClickListener(twitterLogin);
    }

    private View.OnClickListener attemptLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            EditText userName = (EditText) findViewById(R.id.userName_editview);
            EditText password = (EditText) findViewById(R.id.password_editview);

            if (userName.getText() == null || userName.getText().length() == 0){
                userName.requestFocus();
                userName.setError(getString(R.string.missingUserName));
            }
            else if(password.getText() == null || password.getText().length() == 0){
                password.requestFocus();
                password.setError(getString(R.string.missingPassword));
            }
            else{
                ParseUser.logInInBackground(userName.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            joinCourseChannels();
                            goToMainActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.failedLogin), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };

    private View.OnClickListener facebookLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AccessToken token = AccessToken.getCurrentAccessToken();

            if (token != null) {
                ParseFacebookUtils.logInInBackground(token);
            } else {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(UserLoginActivity.this, null, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            if (user != null) {
                                if (user.isNew()) {
                                    ClassmateUser classmateUser = (ClassmateUser) user;
                                    classmateUser.setFirstName(Profile.getCurrentProfile().getFirstName());
                                    classmateUser.setLastName(Profile.getCurrentProfile().getLastName());
                                    classmateUser.saveInBackground();
                                }

                                joinCourseChannels();
                                goToMainActivity();
                            }
                        } else {
                            Toast.makeText(UserLoginActivity.this,
                                    "An error occurred logging in to Facebook. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };

    View.OnClickListener twitterLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ParseTwitterUtils.logIn(UserLoginActivity.this, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        if (user != null) {
                            if (user.isNew()) {
                                ClassmateUser classmateUser = (ClassmateUser) user;
                                classmateUser.setFirstName(ParseTwitterUtils.getTwitter().getScreenName());
                                classmateUser.setLastName("");
                                classmateUser.saveInBackground();
                            }
                            
                            joinCourseChannels();
                            goToMainActivity();
                        }
                    } else {
                        Toast.makeText(UserLoginActivity.this,
                                "An error occurred logging in to Twitter. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener registerUser = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent registerIntent = new Intent(UserLoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        }
    };


    private void goToMainActivity() {
        Intent mainActivity = new Intent(UserLoginActivity.this, MainActivity.class);

        //This starts the main activity and clears the back stack
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    private void joinCourseChannels() {
        ParseQuery<Course> courseQuery = Course.getQuery();
        courseQuery.whereEqualTo("members", ParseUser.getCurrentUser());

        courseQuery.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> courses, ParseException e) {
                ArrayList<String> courseChannels = new ArrayList<String>();
                for (Course course : courses) {
                    courseChannels.add("course_" + course.getObjectId());
                }

                ParseInstallation.getCurrentInstallation().put("channels", courseChannels);
                ParseInstallation.getCurrentInstallation().saveInBackground();
            }
        });
    }
}
