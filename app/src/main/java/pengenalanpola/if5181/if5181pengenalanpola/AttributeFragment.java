package pengenalanpola.if5181.if5181pengenalanpola;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import if5181.finalproject.model.CustomBitmap;
import if5181.finalproject.util.Common;

public class AttributeFragment extends Fragment {

    private GraphView graphViewRed, graphViewGreen, graphViewBlue, graphViewGrayscale;
    private ImageView imageView;
    private CustomBitmap image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attribute, container, false);

        Button buttonLoadImage = view.findViewById(R.id.buttonLoadImage);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        Button buttonOpenCamera = view.findViewById(R.id.buttonOpenCamera);
        buttonOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 2);
            }
        });

        Button buttonGetAttribute = view.findViewById(R.id.buttonGetAttribute);
        buttonGetAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[][] data = image.getColorStatistic();
                setGraphView(graphViewRed, data[0]);
                setGraphView(graphViewGreen, data[1]);
                setGraphView(graphViewBlue, data[2]);
                setGraphView(graphViewGrayscale, data[3]);
            }
        });

        graphViewRed = view.findViewById(R.id.graphViewRed);
        graphViewGreen = view.findViewById(R.id.graphViewGreen);
        graphViewBlue = view.findViewById(R.id.graphViewBlue);
        graphViewGrayscale = view.findViewById(R.id.graphViewGrayscale);
        imageView = view.findViewById(R.id.imageView);

        graphViewRed.setTitle("Red");
        graphViewGreen.setTitle("Green");
        graphViewBlue.setTitle("Blue");
        graphViewGrayscale.setTitle("Grayscale");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                bitmap = Common.loadImage(super.getContext(), data);
            } else if (requestCode == 2) {
                bitmap = Common.openCamera(data);
            }

            if (bitmap != null) {
                image = new CustomBitmap(bitmap);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void setGraphView(GraphView graphView, int[] data) {
        DataPoint[] dataPoints = new DataPoint[data.length];

        for (int i = 0; i < data.length; i++) {
            dataPoints[i] = new DataPoint(i, data[i]);
        }

        graphView.setVisibility(View.VISIBLE);
        graphView.removeAllSeries();
        graphView.addSeries(new BarGraphSeries(dataPoints));
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(data.length);
    }

}
