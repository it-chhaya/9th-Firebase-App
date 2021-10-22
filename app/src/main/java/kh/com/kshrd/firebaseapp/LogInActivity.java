package kh.com.kshrd.firebaseapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LogInActivity extends AppCompatActivity {

    private final static String TAG = LogInActivity.class.getName();

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private Button mButtonLogInWithGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // init UIs
        initUIs();

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // bind events
        onButtonLogInWithGoogleClicked();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // already login
            User myUser = new User(user.getEmail(), user.getDisplayName());
            Intent mainIntent = new Intent(LogInActivity.this, MainActivity.class);
            mainIntent.putExtra("my_user", myUser);
            startActivity(mainIntent);
        }
    }

    private void initUIs() {
        mButtonLogInWithGoogle = findViewById(R.id.buttonLoginWithGoogle);
    }

    private void onButtonLogInWithGoogleClicked() {
        mButtonLogInWithGoogle.setOnClickListener(view -> {
            logInWithGoogle();
        });
    }

    private void logInWithGoogle() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        logInLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> logInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e);
                    }
                }
            }
    );

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential =GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "Email = " + user.getEmail());
                            Log.d(TAG, "Name = " + user.getDisplayName());
                            User myUser = new User(user.getEmail(), user.getDisplayName());
                            Intent mainIntent = new Intent(LogInActivity.this, MainActivity.class);
                            mainIntent.putExtra("my_user", myUser);
                            startActivity(mainIntent);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                });

    }

}