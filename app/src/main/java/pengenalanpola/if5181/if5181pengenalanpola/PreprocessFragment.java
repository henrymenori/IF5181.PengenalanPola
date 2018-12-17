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
import android.widget.SeekBar;
import android.widget.TextView;

import if5181.finalproject.model.CustomBitmap;
import if5181.finalproject.util.Common;

public class PreprocessFragment extends Fragment {

    private ImageView imageViewBefore, imageViewAfter;
    private TextView textView;
    private CustomBitmap image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preprocess, container, false);

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

        Button buttonChainCode = view.findViewById(R.id.buttonChainCode);
        buttonChainCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder chainCode = image.getChainCode();
                textView.setText(chainCode);
            }
        });

        Button buttonThinning = view.findViewById(R.id.buttonThinning);
        buttonThinning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBitmap result = image.convertToBinary(127).erode();
                imageViewAfter.setImageBitmap(result.getCurrentBitmap());
            }
        });

        Button buttonEdgeDetection = view.findViewById(R.id.buttonEdgeDetection);
        buttonEdgeDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBitmap result = image.detectEdge();
                imageViewAfter.setImageBitmap(result.getCurrentBitmap());
            }
        });

        imageViewBefore = view.findViewById(R.id.imageViewBefore);
        imageViewAfter = view.findViewById(R.id.imageViewAfter);
        textView = view.findViewById(R.id.textView);

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
                imageViewBefore.setImageBitmap(bitmap);
            }
        }
    }

}
