package com.example.android.rottenbananas;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class SyncJobCreator implements JobCreator {

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case DataSyncJob.TAG:
                return new DataSyncJob();
            default:
                return null;
        }
    }

}
