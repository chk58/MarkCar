package com.marker.markcar.map.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.marker.markcar.R;

public class Icons {

    private static boolean sInit = false;
    public static Bitmap sParkIcon;

    public static void init(Context context) {
        if (!sInit) {
            sInit = true;
            sParkIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.packing_space);
        }
    }

}
