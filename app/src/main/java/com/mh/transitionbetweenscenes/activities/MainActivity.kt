package com.mh.transitionbetweenscenes.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mh.transitionbetweenscenes.R
import com.mh.transitionbetweenscenes.fragments.SimpleHolderScenesFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, SimpleHolderScenesFragment())
                .commit()
        }
    }
}