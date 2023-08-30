/*
 * Copyright (c) 2021 IDScan.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Support: support@idscan.com
 */
package net.idscan.components.android.dvs.test;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.idscan.components.android.dvs.common.DocumentFiled;
import net.idscan.components.android.dvs.net.DocumentVerification;
import net.idscan.components.android.dvs.net.FaceVerification;
import net.idscan.components.android.dvs.net.FieldVerificationDetails;
import net.idscan.components.android.dvs.net.ServiceDetails;
import net.idscan.components.android.dvs.net.VerificationDetails;
import net.idscan.components.android.dvs.net.VerificationResult;

import java.util.List;
import java.util.Map;

public class DvsResultFragment extends Fragment {
    private static final String ARG_RESULT = "arg_result";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dvs_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            VerificationResult result = args.getParcelable(ARG_RESULT);
            if (result != null) {
                showResult(view, result);
            }
        }
    }

    private void showResult(@NonNull View view, @NonNull VerificationResult result) {
        if (result.facePhoto != null) {
            ((ImageView) view.findViewById(R.id.iv_face)).setImageBitmap(result.facePhoto);
        }

        if (result.documentFacePhoto != null) {
            ((ImageView) view.findViewById(R.id.iv_doc_face)).setImageBitmap(result.documentFacePhoto);
        }

        if (result.signature != null) {
            ((ImageView) view.findViewById(R.id.iv_signature)).setImageBitmap(result.signature);
        }

        SpannableStringBuilder spannable = new SpannableStringBuilder();

        if (result.faceVerification != null) {
            printFaceVerification("", spannable, result.faceVerification);
        }

        if (result.documentVerification != null) {
            printDocumentVerification("", spannable, result.documentVerification);
        }

        ((TextView) view.findViewById(R.id.tv_report)).setText(spannable);
    }


    public static DvsResultFragment newInstance(@NonNull VerificationResult result) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_RESULT, result);
        DvsResultFragment fragment = new DvsResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static void printFaceVerification(
            @NonNull String prefix,
            @NonNull SpannableStringBuilder spannable,
            @NonNull FaceVerification faceVerification
    ) {
        spannable
                .append(prefix + "Face Verification", new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append("\n");

        prefix = prefix + "  ";

        spannable.append(prefix)
                .append("Anti Spoofing score: ", new StyleSpan(Typeface.BOLD_ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append(String.valueOf(faceVerification.antiSpoofing))
                .append("\n");

        spannable
                .append(prefix)
                .append("Face Confidence: ", new StyleSpan(Typeface.BOLD_ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append(String.valueOf(faceVerification.confidence))
                .append("\n");

        spannable.append("\n\n");
    }

    private static void printDocumentVerification(
            @NonNull String prefix,
            @NonNull SpannableStringBuilder spannable,
            @NonNull DocumentVerification documentVerification
    ) {
        spannable
                .append(prefix)
                .append("Document Verification", new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append("\n");

        prefix += "  ";

        spannable
                .append(prefix)
                .append("Document type: ", new StyleSpan(Typeface.BOLD_ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append(documentVerification.type.name())
                .append("\n");

        spannable
                .append(prefix)
                .append("Status: ", new StyleSpan(Typeface.BOLD_ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append(documentVerification.status)
                .append("\n");

        spannable
                .append(prefix)
                .append("Total Confidence: ", new StyleSpan(Typeface.BOLD_ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append(String.valueOf(documentVerification.totalConfidence))
                .append("\n");

        spannable.append("\n");

        Map<DocumentFiled, String> fields = documentVerification.fields;
        Map<DocumentFiled, Integer> fieldsConfidence = documentVerification.fieldsConfidence;
        if (fields != null) {
            printDocumentFields(prefix, spannable, fields, fieldsConfidence);
        }

        VerificationDetails details = documentVerification.verificationDetails;
        if (details != null) {
            printVerificationDetails(prefix, spannable, details);
        }
    }

    private static void printDocumentFields(
            @NonNull String prefix,
            @NonNull SpannableStringBuilder spannable,
            @NonNull Map<DocumentFiled, String> fields,
            @Nullable Map<DocumentFiled, Integer> fieldsConfidence
    ) {
        spannable
                .append(prefix)
                .append("Fields", new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append("\n");

        prefix += "  ";

        for (Map.Entry<DocumentFiled, String> f : fields.entrySet()) {
            String fieldName = getFieldName(f.getKey());
            String fieldValue = f.getValue();
            String fieldScore;
            if (fieldsConfidence != null && fieldsConfidence.containsKey(f.getKey())) {
                fieldScore = String.valueOf(fieldsConfidence.get(f.getKey()));
            } else {
                fieldScore = "?";
            }

            spannable
                    .append(prefix)
                    .append(fieldName, new StyleSpan(Typeface.BOLD_ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append(fieldValue)
                    .append("(")
                    .append(fieldScore, new StyleSpan(Typeface.ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append(")")
                    .append("\n");
        }
        spannable.append("\n");
    }

    private static void printVerificationDetails(
            @NonNull String prefix,
            @NonNull SpannableStringBuilder spannable,
            @NonNull VerificationDetails details
    ) {
        spannable
                .append(prefix)
                .append("Details", new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append("\n");

        prefix += "  ";

        printServicesDetails(prefix, spannable, details.servicesDetails);
    }

    private static void printServicesDetails(
            @NonNull String prefix,
            @NonNull SpannableStringBuilder spannable,
            @NonNull List<ServiceDetails> servicesDetails
    ) {
        for (ServiceDetails d : servicesDetails) {
            spannable
                    .append(prefix)
                    .append(d.displayName, new StyleSpan(Typeface.BOLD_ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append("\n");

            String internalPrefix = prefix + "  ";
            spannable
                    .append(internalPrefix)
                    .append("Status: ")
                    .append(d.status.name())
                    .append("\n");

            spannable
                    .append(internalPrefix)
                    .append("Effect: ")
                    .append(d.serviceEffect.name())
                    .append("\n");

            spannable
                    .append(internalPrefix)
                    .append("Reason: ")
                    .append(d.reason)
                    .append("\n");

            Map<DocumentFiled, FieldVerificationDetails> fieldsDetails = d.fieldVerificationDetails;
            if (fieldsDetails != null) {
                printFieldsDetails(internalPrefix, spannable, fieldsDetails);
            }
        }
    }

    private static void printFieldsDetails(
            @NonNull String prefix,
            @NonNull SpannableStringBuilder spannable,
            @NonNull Map<DocumentFiled, FieldVerificationDetails> fieldsDetails
    ) {
        spannable
                .append(prefix)
                .append("Fields", new StyleSpan(Typeface.BOLD_ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append("\n");

        prefix += "  ";

        for (Map.Entry<DocumentFiled, FieldVerificationDetails> f : fieldsDetails.entrySet()) {
            String fieldName = getFieldName(f.getKey());
            FieldVerificationDetails fieldValue = f.getValue();

            spannable
                    .append(prefix)
                    .append(fieldName, new StyleSpan(Typeface.BOLD_ITALIC), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append(fieldValue.effect.name())
                    .append(" ")
                    .append(String.valueOf(fieldValue.score))
                    .append("\n");
        }
    }

    private static String getFieldName(DocumentFiled key) {
        switch (key) {
            case abbr3Country:
                return "Country Code: ";
            case abbrCountry:
                return "Country Abr: ";
            case address:
                return "Address: ";
            case city:
                return "City: ";
            case driverClass:
                return "Driver Class: ";
            case country:
                return "Country: ";
            case dob:
                return "DOB: ";
            case expires:
                return "Expires: ";
            case eyes:
                return "Eyes: ";
            case familyName:
                return "Family Name: ";
            case firstName:
                return "First Name: ";
            case fullName:
                return "Full Name: ";
            case gender:
                return "Gender: ";
            case hair:
                return "Hair: ";
            case height:
                return "Height: ";
            case id:
                return "Id: ";
            case idType:
                return "Id Type: ";
            case issued:
                return "Issued: ";
            case middleName:
                return "Middle Name: ";
            case postalBox:
                return "Postal Box: ";
            case state:
                return "State: ";
            case template:
                return "Template: ";
            case weight:
                return "Weight: ";
            case zip:
                return "Zip: ";
            default:
                return "Unknown: ";
        }
    }
}
