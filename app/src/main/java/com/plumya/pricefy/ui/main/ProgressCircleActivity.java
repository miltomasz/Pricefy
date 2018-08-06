package com.plumya.pricefy.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.plumya.pricefy.R;
import com.plumya.pricefy.data.PricefyRepository;
import com.plumya.pricefy.data.local.VisionModelMapper;
import com.plumya.pricefy.di.Injector;
import com.plumya.pricefy.utils.BitmapUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by miltomasz on 15/07/18.
 */

public class ProgressCircleActivity extends AppCompatActivity {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyDcusOmzydK7WdJS3pONSnCPL67KkB5pow";
    public static final String PROCESSING_RESULT = "processedResult";
    public static final String IMAGE_ID = "imageId";
    public static final String LOG_TAG = ProgressCircleActivity.class.getSimpleName();

    private PricefyRepository repository;

    @BindView(R.id.progressText) TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_circle);
        ButterKnife.bind(this);

        repository = Injector.provideRepository(getApplicationContext());

        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.PHOTO_PATH_EXTRA)) {
            String photoPath = intent.getStringExtra(MainActivity.PHOTO_PATH_EXTRA);
            progressText.setText(R.string.compressing_image_progress_msg);
            new ImageRecognitionTask(this).execute(photoPath);
        }
    }

    private class ImageRecognitionTask extends AsyncTask<String, String, Long> {

        private static final int QUALITY = 75;
        private Activity activity;

        public ImageRecognitionTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Long doInBackground(String... params) {
            String photoPath = params[0];
            if (photoPath == null) {
                Log.e(LOG_TAG, "Photo path is null");
                return null;
            }

            Bitmap bitmap = BitmapUtil.resamplePic(activity, photoPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, QUALITY, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            publishProgress(new String[] {getString(R.string.recognizing_image_progress_msg)});

            Image base64EncodedImage = new Image();
            base64EncodedImage.encodeContent(imageBytes);

            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
            annotateImageRequest.setImage(base64EncodedImage);

            List<Feature> features = new ArrayList<>();
            features.add(new Feature().setType("LABEL_DETECTION"));
            // next type detections for future releases
//            features.add(new Feature().setType("TEXT_DETECTION"));
//            features.add(new Feature().setType("IMAGE_PROPERTIES"));
//            features.add(new Feature().setType("WEB_DETECTION"));
            annotateImageRequest.setFeatures(features);

            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(annotateImageRequest);

            BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
            batchRequest.setRequests(requests);

            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            Vision.Builder visionBuilder =  new Vision.Builder(httpTransport, jsonFactory, null);
            VisionRequestInitializer requestInitializer =  new VisionRequestInitializer(CLOUD_VISION_API_KEY);
            visionBuilder.setVisionRequestInitializer(requestInitializer);

            Vision vision = visionBuilder.build();

            BatchAnnotateImagesResponse response;
            try {
                Vision.Images.Annotate annotateRequest = vision.images().annotate(batchRequest);
                annotateRequest.setDisableGZipContent(true);
                response = annotateRequest.execute();
            } catch (IOException e) {
                Log.d(LOG_TAG, "Exception while recognizing image: " + e.getMessage());
                return null;
            }
            List<AnnotateImageResponse> responses = response.getResponses();
            if (responses.size() > 0) {
                publishProgress(new String[] {getString(R.string.saving_image_progress_msg)});
                AnnotateImageResponse annotateImageResponse = responses.get(0);
                String labels = VisionModelMapper.mapLabels(annotateImageResponse);
                // no labels recognized
                if (labels == null) return null;
                com.plumya.pricefy.data.local.model.Image image =
                        new com.plumya.pricefy.data.local.model.Image(
                                photoPath, new Date(), labels, null, null, null
                        );
                com.plumya.pricefy.data.local.model.Image savedImage = repository.insertImage(image);
                if (savedImage != null) {
                    return savedImage.getId();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            progressText.setText(progress[0]);
        }

        @Override
        protected void onPostExecute(Long imageId) {
            super.onPostExecute(imageId);
            Intent processedDataIntent = new Intent();
            if (imageId == null || imageId == -1) {
                activity.setResult(ProgressCircleActivity.RESULT_CANCELED);
            } else {
                processedDataIntent.putExtra(IMAGE_ID, imageId);
                activity.setResult(ProgressCircleActivity.RESULT_OK, processedDataIntent);
            }
            activity.finish();
        }
    }
}
