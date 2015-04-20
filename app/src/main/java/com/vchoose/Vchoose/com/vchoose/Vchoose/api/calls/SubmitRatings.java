package com.vchoose.Vchoose.com.vchoose.Vchoose.api.calls;

import android.app.Activity;
import android.os.AsyncTask;

import com.vchoose.Vchoose.MainActivity;
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

        return jsonReader.submitRatingForDish(menu_item_id,rating);
    }
}
