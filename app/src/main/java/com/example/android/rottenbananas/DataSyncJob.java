package com.example.android.rottenbananas;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DataSyncJob extends Job {

    public static final String TAG = "job_data_sync";
    // public static String para="";

    public static void scheduleAdvancedJob(String s) {
        // DataSyncJob.para = para;
//        Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(DataSyncJob.TAG);
//        if (!jobRequests.isEmpty()) {
//            return;
//        }
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putString("key", s);

        new JobRequest.Builder(DataSyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(14))
                .setUpdateCurrent(true) // calls cancelAllForTag(DataSyncJob.TAG) for you
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setExtras(extras)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
        Log.d(TAG, "Job scheduled successfully!");


//        int jobId = new JobRequest.Builder(DataSyncJob.TAG)
//                .setExecutionWindow(10_000L, 4000_000L)
//                .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
//                .setRequiresCharging(true)
//                .setRequiresDeviceIdle(false)
//                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
//                .setExtras(extras)
//                .setRequirementsEnforced(true)
//                .setUpdateCurrent(true)
//                .build()
//                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Log.d(TAG, "Params: " + params);

        // Log.d("ExactPara",para);
        // Accessing a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Accessing Firebase realtime instance
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = getParams().getExtras().get("key").toString();

        db.collection("angel")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String key_in = document.getId();
                                Log.d(TAG, key_in + " => " + document.getData());
                                if (key.equals(key_in)) {
                                    HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
                                    Double d = Double.parseDouble(map.get("days_left").toString());
                                    if (d > 0) {
                                        map.put("days_left", d - 1);
                                        if (Double.parseDouble(map.get("days_left").toString()) <= 2) {
                                            map.put("state", "over ripe");
                                        } else if (Double.parseDouble(map.get("days_left").toString()) <= 5) {
                                            map.put("state", "ripe");
                                        }

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
                                        DataSyncJob.scheduleAdvancedJob(key);
                                    } else {
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
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return Result.SUCCESS;
    }

}
