package com.example.doistchatproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.doistchatproject.Database.DatabaseRepository;
import com.example.doistchatproject.Fragments.MessageViewFragment;

public class MainActivity extends AppCompatActivity {

    private static final String FIRST_RUN_PARAM = "firstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readOnlyOnceJSONFile();

        Fragment fragment = new MessageViewFragment();
                getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.general_placeholder, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Only read json and populate database if it is the first run
     */
    private void readOnlyOnceJSONFile() {

       final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

       if(!prefs.getBoolean(FIRST_RUN_PARAM, false)) {

           final DatabaseRepository databaseRepository = new DatabaseRepository(getResources(), this);
            databaseRepository.readJsonAndPopulateTables();

           final SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(FIRST_RUN_PARAM, true);
            editor.commit();
        }
    }
}