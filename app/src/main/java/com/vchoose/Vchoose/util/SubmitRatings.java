package com.vchoose.Vchoose.util;

import android.app.Activity;
import android.os.AsyncTask;

import com.vchoose.Vchoose.util.VcJsonReader;

/**
 * Created by vishaldhiman on 4/6/15.
 */
public class SubmitRatings extends AsyncTask<String, Void, Boolean> {
    VcJsonReader jsonReader = new VcJsonReader();

    public SubmitRatings(Activity activity) {
        //context = activity;
    }

    @Override
    protected Boolean doInBackground(final String... params) {
        int menu_item_id = Integer.parseInt(params[0]);
        int rating = (new Float(params[1])).intValue();
        String comment = params[2];
        String authentication_token = params[3];

        return jsonReader.submitRatingForDish(menu_item_id, rating, comment, authentication_token);
    }
}
