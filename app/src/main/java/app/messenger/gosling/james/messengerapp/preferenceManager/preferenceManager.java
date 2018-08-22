package app.messenger.gosling.james.messengerapp.preferenceManager;

import android.content.Context;
import android.content.SharedPreferences;

public class preferenceManager {

    static String location = "messenger_app_preference_Manager_userSettings";

    public static void writeUserName(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(location, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", name);
        editor.commit();
    }

    public static void setKey(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(location, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("key", name);
        editor.commit();
    } // ends here

    public static String getkey(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(location, Context.MODE_PRIVATE);
        return preferences.getString("key", "nothing");
    } // ends here

    public static String getUserName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(location, Context.MODE_PRIVATE);
        return preferences.getString("username", "user");
    } // ends here


}
