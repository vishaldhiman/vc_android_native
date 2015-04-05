package com.vchoose.Vchoose;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by vishaldhiman on 2/25/15.
 */
public class DishViewWrapper {
    View base;
    RatingBar rate=null;
    TextView vehicleType=null;
    TextView vehicleColor=null;
    TextView fuel=null;
    TextView description = null;

    DishViewWrapper(View base) {
        this.base = base;
    }

    RatingBar getRatingBar() {
        if (rate==null) {
            rate=(RatingBar)base.findViewById(R.id.ratingBar);
        }
        return(rate);
    }

    TextView getVehicleType() {
        if (vehicleType==null) {
            vehicleType = (TextView)base.findViewById(R.id.Dish_name);
        }
        return(vehicleType);
    }

    TextView getVehicleColor() {
        if (vehicleColor==null) {
            vehicleColor = (TextView)base.findViewById(R.id.vehicleColor);
        }
        return(vehicleColor);
    }

    TextView getDescription() {
        if (description==null) {
            description = (TextView)base.findViewById(R.id.description);
        }
        return(description);
    }
}
