package com.plumya.pricefy.data.local;

import android.text.TextUtils;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by miltomasz on 20/07/18.
 */

public class VisionModelMapper {

    private VisionModelMapper() {}

    public static String mapLabels(AnnotateImageResponse response) {
        List<EntityAnnotation> labelAnnotations = response.getLabelAnnotations();
        Collections.sort(labelAnnotations, new Comparator<EntityAnnotation>() {
            @Override
            public int compare(EntityAnnotation ea1, EntityAnnotation ea2) {
                return ea1.getScore().compareTo(ea2.getScore());
            }
        });
        int toIndex = labelAnnotations.size() > 3 ? 3 : labelAnnotations.size();
        List<EntityAnnotation> bestEntities = labelAnnotations.subList(0, toIndex);
        List<String> labelsToConcat = new ArrayList<>();
        for (EntityAnnotation ea : bestEntities) {
            labelsToConcat.add(ea.getDescription());
        }
        return TextUtils.join("+", labelsToConcat);
    }
}
