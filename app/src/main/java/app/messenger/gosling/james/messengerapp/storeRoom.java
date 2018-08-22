package app.messenger.gosling.james.messengerapp;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

public class storeRoom {

    public static String getDate(Long d) {
        SimpleDateFormat mFormat = new SimpleDateFormat("EEEE d,MMM yyyy ' at ' h:mm a");
        return mFormat.format(d);
    }

    public static String getTimeForMessages(Long d) {
        SimpleDateFormat mFormat = new SimpleDateFormat("h:mm a");
        return mFormat.format(d);
    }

    public static String getMonth(Long d) {
        SimpleDateFormat mFormat = new SimpleDateFormat("MMM");
        return mFormat.format(d);
    }

    public static String getYear(Long d) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy");
        return mFormat.format(d);
    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;
            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    } // function ends here

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static String getFileType(Uri uri, Context context) {
        ContentResolver c = context.getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(c.getType(uri));
    } // method for getting the type of file


}
