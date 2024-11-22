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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.idscan.components.android.dvs.net.VerificationData;

public class DvsVerificationDataFragment extends Fragment {
    private static final String ARG_DATA = "arg_data";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dvs_verification_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            VerificationData data = args.getParcelable(ARG_DATA);
            if (data != null) {
                showResult(view, data);
            }
        }
    }

    private void showResult(@NonNull View view, @NonNull VerificationData data) {
        if (data.faceImage != null) {
            ((ImageView) view.findViewById(R.id.iv_face)).setImageBitmap(data.faceImage);
        }

        if (data.frontImage != null) {
            ((ImageView) view.findViewById(R.id.iv_front)).setImageBitmap(data.frontImage);
        }

        if (data.backImage != null) {
            ((ImageView) view.findViewById(R.id.iv_back)).setImageBitmap(data.backImage);
        }
    }

    public static DvsVerificationDataFragment newInstance(@NonNull VerificationData data) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA, data);
        DvsVerificationDataFragment fragment = new DvsVerificationDataFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
