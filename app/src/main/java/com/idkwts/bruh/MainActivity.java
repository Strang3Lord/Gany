package com.idkwts.bruh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.idkwts.bruh.Fragments.ChatFragment;
import com.idkwts.bruh.Fragments.HomeFragment;
import com.idkwts.bruh.Fragments.NotificationFragment;
import com.idkwts.bruh.Fragments.ProfileFragment;
import com.idkwts.bruh.Fragments.TrendingFragment;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("not");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Bundle intent = getIntent().getExtras();
        // if intent is null
        if(intent != null){
            String author = intent.getString("authorid");

            if (author != null){
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", author);
                editor.apply();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
            else{
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).commit();
            }



        } else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.send :
                        selectorFragment = new ChatFragment();
                        break;

                    case R.id.nav_trending :
                        selectorFragment = new TrendingFragment();
                        break;

                    case R.id.nav_home :
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_heart :
                        selectorFragment = new NotificationFragment();
                        break;

                    case R.id.nav_profile :
                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        selectorFragment = new ProfileFragment();
                        break;
                }

                if (selectorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , selectorFragment).commit();
                }

                return  true;

            }
        });
    }
}
