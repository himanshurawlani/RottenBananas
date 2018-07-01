/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.android.rottenbananas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

/** Main {@code Activity} class for the Camera app. */
public class CameraActivity extends Activity {

//   public static HashMap<String, Double> hashMap = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_camera);
    if (null == savedInstanceState) {
      getFragmentManager()
          .beginTransaction()
          .replace(R.id.container, Camera2BasicFragment.newInstance())
          .commit();
    }
  }

//    @Override
//    public void onBackPressed() {
//        int count = getFragmentManager().getBackStackEntryCount();
//        Intent intent = getIntent();
//        intent.putExtra("raw",hashMap.get("raw"));
//        intent.putExtra("ripe",hashMap.get("ripe"));
//        intent.putExtra("rotten",hashMap.get("rotten"));
//        setResult(1, intent);
//        Log.d("CameraActivity","onBackPressed() called");
//        if (count == 0) {
//            super.onBackPressed();
//            //additional code
//        } else {
//            getFragmentManager().popBackStack();
//        }
//    }
}
