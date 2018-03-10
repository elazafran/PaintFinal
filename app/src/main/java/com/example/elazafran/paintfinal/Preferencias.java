package com.example.elazafran.paintfinal;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by elazafran on 9/3/18.
 */

public class Preferencias extends PreferenceActivity {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferencias);

        }

    }

