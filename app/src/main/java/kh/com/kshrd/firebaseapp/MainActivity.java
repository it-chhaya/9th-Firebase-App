package kh.com.kshrd.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = (User) getIntent().getSerializableExtra("my_user");

        Log.i(TAG, "onCreate: " + user);

        findViewById(R.id.button_logout).setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });

    }

}