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
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import net.idscan.components.android.dvs.DvsException;
import net.idscan.components.android.dvs.common.network.CallResult;
import net.idscan.components.android.dvs.net.DvsClient;
import net.idscan.components.android.dvs.net.VerificationConfig;
import net.idscan.components.android.dvs.net.VerificationError;
import net.idscan.components.android.dvs.net.VerificationRequest;
import net.idscan.components.android.dvs.net.VerificationResult;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DvsRequestFragment extends Fragment {
    private static final String DEFAULT_REQUEST_AUTHORIZATION_TOKEN = "sk_e821ca3f-a12e-4f51-8e30-ed08795a88eb";
    private static final String REQUEST_KEY = "DvsRequestFragment";
    private static final String ARG_REQUEST = "arg_request";
    private static final String RESULT_ERROR = "error";
    private static final String RESULT_DATA = "data";

    interface ResultListener {
        void onResult(@NonNull VerificationResult result);
    }

    interface ErrorListener {
        void onError(@NonNull DvsException error);
    }

    @NonNull
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    @NonNull
    private final Handler handler = new Handler();
    private boolean isProcessing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dvs_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("dvs_prefs", Context.MODE_PRIVATE);

        Bundle args = requireArguments();
        VerificationRequest request = args.getParcelable(ARG_REQUEST);
        if (request == null) {
            throw new NullPointerException("Request argument is omitted.");
        }

        EditText etAuthorization = view.findViewById(R.id.et_authorization);
        etAuthorization.setText(prefs.getString("request_authorization", DEFAULT_REQUEST_AUTHORIZATION_TOKEN));

        EditText etRequest = view.findViewById(R.id.et_request_id);
        etRequest.setText(request.requestId);

        ProgressBar progress = (ProgressBar) view.findViewById(R.id.pb_processing);
        Button btnRequest = (Button) view.findViewById(R.id.btn_request);
        btnRequest.setOnClickListener(v -> {
            String authorizationToken = etAuthorization.getText().toString();
            String requestId = etRequest.getText().toString();

            if (!isProcessing) {
                isProcessing = true;
                btnRequest.setEnabled(false);
                progress.setVisibility(View.VISIBLE);
                executor.submit(() -> makeCall(authorizationToken, new VerificationRequest(requestId)));

                prefs.edit()
                        .putString("request_authorization", authorizationToken)
                        .apply();
            }
        });
    }

    @Override
    public void onDestroy() {
        executor.shutdownNow();
        isProcessing = false;
        super.onDestroy();
    }

    @WorkerThread
    private void makeCall(@NonNull String authorizationToken, @NonNull VerificationRequest request) {
        try {
            DvsClient client = new DvsClient(authorizationToken);
            VerificationConfig config = new VerificationConfig();
            CallResult<VerificationResult, VerificationError> result =
                    client.getVerificationResult(config, request).execute();

            if (result.isSuccess) {
                handler.post(() -> onSuccess(result.result));
            } else {
                handler.post(() -> onError(new DvsException(result.error.description)));
            }
        } catch (IOException e) {
            handler.post(() -> onError(new DvsException(e)));
        }
    }

    @MainThread
    private void onSuccess(@NonNull VerificationResult result) {
        if (isProcessing) {
            isProcessing = false;
            ((Button) requireView().findViewById(R.id.btn_request)).setEnabled(true);
            ((ProgressBar) requireView().findViewById(R.id.pb_processing)).setVisibility(View.GONE);

            Bundle bundle = new Bundle();
            bundle.putParcelable(RESULT_DATA, result);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, bundle);
        }
    }

    @MainThread
    private void onError(@NonNull DvsException error) {
        if (isProcessing) {
            isProcessing = false;
            ((Button) requireView().findViewById(R.id.btn_request)).setEnabled(true);
            ((ProgressBar) requireView().findViewById(R.id.pb_processing)).setVisibility(View.GONE);

            Bundle bundle = new Bundle();
            bundle.putSerializable(RESULT_ERROR, error);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, bundle);
        }
    }

    public static void setListener(
            @NonNull FragmentManager fragmentManager,
            @NonNull LifecycleOwner lifecycleOwner,
            @NonNull ResultListener resultListener,
            @NonNull ErrorListener errorListener
    ) {
        fragmentManager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner, (requestKey, bundle) -> {
            if (requestKey.equals(REQUEST_KEY)) {
                DvsException error = (DvsException) bundle.getSerializable(RESULT_ERROR);
                if (error != null) {
                    errorListener.onError(error);
                } else {
                    VerificationResult data = bundle.getParcelable(RESULT_DATA);
                    if (data != null) {
                        resultListener.onResult(data);
                    }
                }
            }
        });
    }

    public static DvsRequestFragment newInstance(@NonNull VerificationRequest request) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_REQUEST, request);
        DvsRequestFragment fragment = new DvsRequestFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
