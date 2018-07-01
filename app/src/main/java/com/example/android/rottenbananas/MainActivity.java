package com.example.android.rottenbananas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.evernote.android.job.JobManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainActivity extends Activity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton detection_fab = findViewById(R.id.main_detect_fab);
        detection_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use this template at various places in your app to record user clicks
                Bundle bundle = new Bundle();

                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra("FromActivity", "MainActivity");
                startActivity(intent);
            }
        });

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        JobManager.create(this).addJobCreator(new SyncJobCreator());
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("MainActivity","onActivityResult() called");
//        Log.d("MainActivity","requestCode: "+requestCode);
//        Log.d("MainActivity","resultCode: "+resultCode);
//        if(requestCode==3){
//            if(resultCode==1){
//                double raw = Double.parseDouble(data.getStringExtra("raw"));
//                double ripe = Double.parseDouble(data.getStringExtra("ripe"));
//                double rotten = Double.parseDouble(data.getStringExtra("rotten"));
//                double count = Double.parseDouble(data.getStringExtra("count"));
//
//                Log.d("MainActivity","Average raw score: "+String.valueOf((1.0*raw)/count));
//                Log.d("MainActivity","Average ripe score: "+String.valueOf((1.0*ripe)/count));
//                Log.d("MainActivity","Average rotten score: "+String.valueOf((1.0*rotten)/count));
//            }
//        }
//    }
}
