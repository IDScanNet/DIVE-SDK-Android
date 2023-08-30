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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.idscan.components.android.dvs.DvsException;

public class DvsErrorFragment extends Fragment {
    private static final String ARG_ERROR = "arg_error";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dvs_error, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            DvsException error = (DvsException) args.getSerializable(ARG_ERROR);
            if (error != null) {
                ((TextView) view.findViewById(R.id.tv_error)).setText(error.getMessage());
                Log.d("DvsErrorFragment", error.getMessage());
            }
        }
    }

    public static DvsErrorFragment newInstance(@NonNull DvsException error) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ERROR, error);
        DvsErrorFragment fragment = new DvsErrorFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
