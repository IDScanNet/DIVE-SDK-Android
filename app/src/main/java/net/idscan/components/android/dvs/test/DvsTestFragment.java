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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import net.idscan.components.android.dvs.DvsConfig;
import net.idscan.components.android.dvs.capture.CaptureConfig;
import net.idscan.components.android.dvs.common.DocumentType;
import net.idscan.components.android.dvs.net.VerificationConfig;

import java.util.ArrayList;

public class DvsTestFragment extends Fragment {
    private static final String REQUEST_KEY = "DvsTestFragment";
    private static final String CONFIG_KEY = "DvsConfig";
    private static final String DEFAULT_AUTHORIZATION_TOKEN = "sk_e821ca3f-a12e-4f51-8e30-ed08795a88eb";
    private static final String DEFAULT_LICENSE_KEY = "eyJjb21tb25MaWNlbnNlS2V5IjoiaFFaWTRSa2MzRERlM1dOQVZMTHN4MjJxRXAwZnJwU3N2dE1YNEEwbDJseXAxZjlSSWZDaFFsSmx4b20wVTBPTm1EbFFSMHRFdDR0cTJDVG5Qd1FwNFljS1FSa3VadThURzhhQUdCd0sxK2tJWjdhblhnMVRNR1VNVDFrMnNEWi94SVpDTE02VW50Q1ozK3o0V3ZjakgydG9NMkt5SldFZjNkU291TXQ4b3NjPSJ9";

    interface Listener {
        void startDvsTest(@NonNull DvsConfig config);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dvs_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = requireActivity().getSharedPreferences("dvs_prefs", Context.MODE_PRIVATE);

        EditText etLicenseKey = view.findViewById(R.id.et_license_key);
        etLicenseKey.setText(prefs.getString("license_key", DEFAULT_LICENSE_KEY));

        EditText etAuthorization = view.findViewById(R.id.et_authorization);
        etAuthorization.setText(prefs.getString("authorization", DEFAULT_AUTHORIZATION_TOKEN));

        view.findViewById(R.id.btn_start).setOnClickListener(v -> {
            String authorizationToken = etAuthorization.getText().toString();
            String licenseKey = etLicenseKey.getText().toString();
            boolean isFront = ((CheckBox)view.findViewById(R.id.cb_front)).isChecked();
            boolean isBack = ((CheckBox)view.findViewById(R.id.cb_back)).isChecked();
            boolean isFace = ((CheckBox)view.findViewById(R.id.cb_face)).isChecked();
            boolean isRealFace = ((CheckBox)view.findViewById(R.id.cb_real_face)).isChecked();
            boolean isManualEnabled = ((CheckBox)view.findViewById(R.id.cb_manual)).isChecked();
            boolean isCameraEnabled = ((CheckBox)view.findViewById(R.id.cb_camera)).isChecked();
            boolean isHintEnabled = ((CheckBox)view.findViewById(R.id.cb_enable_hints)).isChecked();
            boolean isAutoContinueEnabled = ((CheckBox)view.findViewById(R.id.cb_auto_continue)).isChecked();

            ArrayList<DocumentType> documentTypes = new ArrayList<>();
            if(((CheckBox)view.findViewById(R.id.cb_driver_license)).isChecked()) {
                documentTypes.add(DocumentType.DriverLicense);
            }
            if(((CheckBox)view.findViewById(R.id.cb_passport)).isChecked()) {
                documentTypes.add(DocumentType.Passport);
            }
            if(((CheckBox)view.findViewById(R.id.cb_passport_card)).isChecked()) {
                documentTypes.add(DocumentType.PassportCard);
            }
            if(((CheckBox)view.findViewById(R.id.cb_international_id)).isChecked()) {
                documentTypes.add(DocumentType.InternationalId);
            }
            if(((CheckBox)view.findViewById(R.id.cb_green_card)).isChecked()) {
                documentTypes.add(DocumentType.GreenCard);
            }

            CaptureConfig.Builder configBuilder = CaptureConfig.builder(licenseKey);
            for(DocumentType type: documentTypes) {
                CaptureConfig.DocumentFlowConfig flowConfig = configBuilder.withDocumentType(type);
                if(isFront) {
                    flowConfig.withFront(isManualEnabled, isCameraEnabled);
                }
                if(isBack) {
                    flowConfig.withBack(isManualEnabled, isCameraEnabled);
                }
                if(isFace) {
                    flowConfig.withFace(isManualEnabled, isCameraEnabled, isRealFace);
                }
                flowConfig.complete();
            }

            CaptureConfig captureConfig = configBuilder
                    .withHints(isHintEnabled)
                    .withAutoContinue(isAutoContinueEnabled)
                    .build();

            VerificationConfig verificationConfig = new VerificationConfig();
            DvsConfig config = new DvsConfig.Builder(authorizationToken, captureConfig, verificationConfig).build();

            prefs.edit()
                    .putString("license_key", licenseKey)
                    .putString("authorization", authorizationToken)
                    .apply();

            Bundle bundle = new Bundle();
            bundle.putParcelable(CONFIG_KEY, config);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, bundle);
        });
    }

    public static void setListener(
            @NonNull FragmentManager fragmentManager,
            @NonNull LifecycleOwner lifecycleOwner,
            @NonNull Listener listener
    ) {
        fragmentManager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner, (requestKey, bundle) -> {
            if (requestKey.equals(REQUEST_KEY)) {
                DvsConfig config = bundle.getParcelable(CONFIG_KEY);
                if(config != null) {
                    listener.startDvsTest(config);
                }
            }
        });
    }
}
