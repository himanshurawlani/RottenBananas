package com.example.android.rottenbananas;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DataSyncJob extends Job {

    public static final String TAG = "job_data_sync";
    // public static String para="";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.d(TAG, "Params: " + params);

        // Log.d("ExactPara",para);
        // Accessing a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Accessing Firebase realtime instance
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        db.collection("angel")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                HashMap<String,Object> map = (HashMap<String, Object>) document.getData();
                                Double d =Double.parseDouble(map.get("days_left").toString());
                                if(d>0){
                                    map.put("days_left", d-1);

                                    // Add a new document with a generated ID
                                    db.collection("angel").document(document.getId())
                                            .set(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });

                                    mDatabase.child("angel").child(document.getId()).setValue(map);
                                    //Schedule Data Syncing
                                    //DataSyncJob.scheduleJob();
                                }else{
                                    db.collection("angel").document(document.getId())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error deleting document", e);
                                                }
                                            });
                                    mDatabase.child("angel").child(document.getId()).removeValue();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        // DataSyncJob.para = para;
        Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(DataSyncJob.TAG);
        if (!jobRequests.isEmpty()) {
            return;
        }
        new JobRequest.Builder(DataSyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(15))
                .setUpdateCurrent(true) // calls cancelAllForTag(DataSyncJob.TAG) for you
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
        Log.d(TAG, "Job scheduled successfully!");
    }

}
