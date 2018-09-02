package if5181.pengenalanpola02;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class Activity1 extends AppCompatActivity {

    public final int LOAD_IMAGE_CODE = 1;
    public final int OPEN_CAMERA_CODE = 2;

    GraphView graphView;
    ImageView imageViewOriginal, imageViewTransform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        graphView = findViewById(R.id.graphView);
        imageViewOriginal = findViewById(R.id.imageViewOriginal);
        imageViewTransform = findViewById(R.id.imageViewTransform);

        graphView.setTitle("Cumulative Histogram");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LOAD_IMAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && data != null) {
                if (requestCode == LOAD_IMAGE_CODE && data.getData() != null) {
                    Cursor cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);

                    if (cursor == null)
                        return;

                    cursor.moveToFirst();
                    String imageString = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    cursor.close();
                    imageViewOriginal.setImageBitmap(ImageUtil.getGrayscaleBitmap(BitmapFactory.decodeFile(imageString)));
                } else if (requestCode == OPEN_CAMERA_CODE && data.getExtras().get("data") != null) {
                    imageViewOriginal.setImageBitmap(ImageUtil.getGrayscaleBitmap((Bitmap) data.getExtras().get("data")));
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, String.format("Error : %s", e.getMessage()), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void loadImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, LOAD_IMAGE_CODE);
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, OPEN_CAMERA_CODE);
    }

    public void transform(View view) {
        Bitmap original = ((BitmapDrawable) imageViewOriginal.getDrawable()).getBitmap();
        Bitmap result = ImageUtil.getLinearTransform(original);

        imageViewTransform.setImageBitmap(result);
        setGraphView(graphView, ImageUtil.getCumulativeHistogram(original));
    }

    private void setGraphView(GraphView graphView, int[] data) {
        DataPoint[] dataPoints = new DataPoint[data.length];

        for (int i = 0; i < data.length; i++) {
            dataPoints[i] = new DataPoint(i, data[i]);
        }

        graphView.removeAllSeries();
        graphView.addSeries(new BarGraphSeries(dataPoints));
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(data.length);
    }
}
