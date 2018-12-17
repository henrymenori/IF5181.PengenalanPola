package pengenalanpola.if5181.if5181pengenalanpola;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import if5181.finalproject.model.CustomBitmap;
import if5181.finalproject.util.Common;

public class EnhancementFragment extends Fragment {

    private ImageView imageViewBefore, imageViewAfter, imageViewGrayscaleBefore, imageViewGrayscaleAfter;
    private SeekBar seekBarAlpha, seekBarA, seekBarB, seekBarC;
    private CustomBitmap image, imageGrayscale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enhancement, container, false);

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

        Button buttonEnhanceA = view.findViewById(R.id.buttonEnhanceA);
        buttonEnhanceA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBitmap result = image.enhanceImage(Math.pow(2, ((double) seekBarAlpha.getProgress() - 100) / 10));
                imageViewAfter.setImageBitmap(result.getCurrentBitmap());
                CustomBitmap resultGrayscale = imageGrayscale.enhanceImage(Math.pow(2, ((double) seekBarAlpha.getProgress() - 100) / 10));
                imageViewGrayscaleAfter.setImageBitmap(resultGrayscale.getCurrentBitmap());
            }
        });

        Button buttonEnhanceB = view.findViewById(R.id.buttonEnhanceB);
        buttonEnhanceB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBitmap result = image.enhanceImage(Math.pow(2, ((double) seekBarAlpha.getProgress() - 100) / 10), seekBarA.getProgress(), seekBarB.getProgress(), seekBarC.getProgress());
                imageViewAfter.setImageBitmap(result.getCurrentBitmap());
                CustomBitmap resultGrayscale = imageGrayscale.enhanceImage(Math.pow(2, ((double) seekBarAlpha.getProgress() - 100) / 10), seekBarA.getProgress(), seekBarB.getProgress(), seekBarC.getProgress());
                imageViewGrayscaleAfter.setImageBitmap(resultGrayscale.getCurrentBitmap());
            }
        });

        Button buttonSmoothOut = view.findViewById(R.id.buttonSmoothOut);
        buttonSmoothOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBitmap result = image.smoothImage();
                imageViewAfter.setImageBitmap(result.getCurrentBitmap());
                CustomBitmap resultGrayscale = imageGrayscale.smoothImage();
                imageViewGrayscaleAfter.setImageBitmap(resultGrayscale.getCurrentBitmap());
            }
        });

        Button buttonReduceNoise = view.findViewById(R.id.buttonReduceNoise);
        buttonReduceNoise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBitmap result = image.reduceNoise();
                imageViewAfter.setImageBitmap(result.getCurrentBitmap());
                CustomBitmap resultGrayscale = imageGrayscale.reduceNoise();
                imageViewGrayscaleAfter.setImageBitmap(resultGrayscale.getCurrentBitmap());
            }
        });

        Button buttonFilter = view.findViewById(R.id.buttonFilter);
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBitmap result = image.filter();
                imageViewAfter.setImageBitmap(result.getCurrentBitmap());
                CustomBitmap resultGrayscale = imageGrayscale.filter();
                imageViewGrayscaleAfter.setImageBitmap(resultGrayscale.getCurrentBitmap());
            }
        });

        imageViewBefore = view.findViewById(R.id.imageViewBefore);
        imageViewAfter = view.findViewById(R.id.imageViewAfter);
        imageViewGrayscaleBefore = view.findViewById(R.id.imageViewGrayscaleBefore);
        imageViewGrayscaleAfter = view.findViewById(R.id.imageViewGrayscaleAfter);
        seekBarAlpha = view.findViewById(R.id.seekBarAlpha);
        seekBarA = view.findViewById(R.id.seekBarA);
        seekBarB = view.findViewById(R.id.seekBarB);
        seekBarC = view.findViewById(R.id.seekBarC);

        seekBarAlpha.setMax(100);
        seekBarAlpha.setProgress(100);
        seekBarA.setMax(255);
        seekBarA.setProgress(127);
        seekBarB.setMax(255);
        seekBarB.setProgress(127);
        seekBarC.setMax(255);
        seekBarC.setProgress(127);

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
                imageGrayscale = image.convertToGrayscale();
                imageViewBefore.setImageBitmap(bitmap);
                imageViewGrayscaleBefore.setImageBitmap(imageGrayscale.getCurrentBitmap());
            }
        }
    }

}
