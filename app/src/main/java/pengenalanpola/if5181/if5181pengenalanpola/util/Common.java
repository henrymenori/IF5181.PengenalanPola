package if5181.finalproject.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

public class Common {

    public static Bitmap loadImage(Context context, Intent data) {
        Cursor cursor = context.getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        cursor.moveToFirst();
        String imageString = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return BitmapFactory.decodeFile(imageString);
    }

    public static Bitmap openCamera(Intent data) {
        return (Bitmap) data.getExtras().get("data");
    }

}
