package com.toggle.notifica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.toggle.notifica.database.DbHelper;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utilities {
    public static Date getDateTimeFromIso(String dateString) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String formatDateTimeToIso(long dateTime) {
        Date date = new Date(dateTime);
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(date) + "Z";
    }

    public static String formatMinutes(int time) {
        int hrs = time / 60;
        int min = time % 60;
        return String.format("%02d:%02d", hrs, min);
    }


    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        if (image == null)
            return null;
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static int returnColor(long id){
        int rand = ((int) id)%3;
        switch (rand){
            case 0:
                return Color.parseColor("#268b83");
            case 1:
                return Color.parseColor("#e7a403");
            default:
                return Color.parseColor("#e53935");
        }

    }

    public static String getDateTimeString(long modified_at) {
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault());
        return df.format(new Date(modified_at));
    }

    // From a Google I/O app


    private static final long SECOND_MILLIS = 1000;
    private static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long MONTH_MILLIS = 30 * DAY_MILLIS;
    private static final long YEAR_MILLIS = 12 * MONTH_MILLIS;


    public static String getTimeAgo(long time) {

        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return "just now";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if (diff < 21 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else if (diff < 31 * DAY_MILLIS) {
            return "a month ago";
        } else if (diff < 10 * MONTH_MILLIS) {
            return diff / MONTH_MILLIS + " months ago";
        } else if (diff < 12 * MONTH_MILLIS) {
            return "a year ago";
        } else {
            return diff / YEAR_MILLIS + " years ago";
        }
    }


    public static void fillProfileView(View view, int color, Bitmap avatar, String header,
                                       String title, String subTitle, String details, String shortName) {

        View headerView = view.findViewById(R.id.headerView);

        ImageView avatarView = (ImageView)view.findViewById(R.id.avatar);
        TextView titleView = (TextView)view.findViewById(R.id.title);
        TextView subTitleView = (TextView)view.findViewById(R.id.subtitle);
        TextView detailsView = (TextView)view.findViewById(R.id.details);
        TextView shortNameView = (TextView)view.findViewById(R.id.shortName);

        if (header != null) {
            ((TextView)headerView.findViewById(R.id.header)).setText(header);
            headerView.setVisibility(View.VISIBLE);
        } else if (headerView != null)
            headerView.setVisibility(View.GONE);

        if (avatar != null){
            avatarView.setImageBitmap(avatar);
            ((GradientDrawable)avatarView.getBackground()).setColor(color);
            avatarView.setVisibility(View.VISIBLE);
        } else
            avatarView.setVisibility(View.GONE);

        if (shortName != null) {
            shortNameView.setText(shortName);
            shortNameView.setBackgroundResource(R.drawable.circle_filled);
            ((GradientDrawable) shortNameView.getBackground()).setColor(color);

            shortNameView.setVisibility(View.VISIBLE);
        } else
            shortNameView.setVisibility(View.GONE);

        if (title != null) {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        } else
            titleView.setVisibility(View.GONE);

        if (subTitle != null) {
            subTitleView.setText(subTitle);
            subTitleView.setVisibility(View.VISIBLE);
        } else
            subTitleView.setVisibility(View.GONE);

        if (details != null) {
            detailsView.setText(details);
            detailsView.setVisibility(View.VISIBLE);
        } else
            detailsView.setVisibility(View.GONE);
    }

    public static void fillCurrentPeriod(RemoteViews remoteViews, String header,
                                       String title, String subTitle) {
        remoteViews.setTextViewText(R.id.headerNow, header);
        remoteViews.setTextViewText(R.id.titleNow, title);
        remoteViews.setTextViewText(R.id.subtitleNow, subTitle);
    }

    public static void fillNextPeriod(RemoteViews remoteViews, String header,
                                         String title, String subTitle) {
        remoteViews.setTextViewText(R.id.headerNext, header);
        remoteViews.setTextViewText(R.id.titleNext, title);
        remoteViews.setTextViewText(R.id.subtitleNext, subTitle);
    }

    public static void showMessage(Context context, String message) {
        Activity activity = (Activity)context;
        CoordinatorLayout coordinatorLayout = null;
        if (activity != null) {
            coordinatorLayout = (CoordinatorLayout)activity.findViewById(R.id.coordinator_layout);
        }

        if (coordinatorLayout != null) {
            Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void logout(Context context) {
        // Clear log-in preferences
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        preferences.edit()
                .putString("username", "")
                .putString("password", "")
                .putBoolean("logged_in", false).apply();
        DbHelper dbHelper = new DbHelper(context);
        dbHelper.deleteAll(dbHelper.getWritableDatabase());


        // Start login activity
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
