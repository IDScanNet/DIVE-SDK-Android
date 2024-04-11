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

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.idscan.components.android.dvs.DvsConfig;
import net.idscan.components.android.dvs.DvsException;
import net.idscan.components.android.dvs.DvsFragment;
import net.idscan.components.android.dvs.net.VerificationResult;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        int systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

        getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        DvsTestFragment.setListener(
                getSupportFragmentManager(),
                this,
                this::showDvsFragment
        );

        DvsFragment.setFragmentResultListener(
                getSupportFragmentManager(),
                this,
                this::showDvsResultFragment,
                this::showDvsErrorFragment
        );

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("DvsTestFragment") == null) {
            Fragment fragment = new DvsTestFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setPrimaryNavigationFragment(fragment)
                    .add(R.id.fragment_container_view, fragment, "DvsTestFragment")
                    .commit();
        }
    }

    private void showDvsFragment(@NonNull DvsConfig config) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("DvsFragment") == null) {
            Fragment fragment = DvsFragment.newInstance(config);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setPrimaryNavigationFragment(fragment)
                    .replace(R.id.fragment_container_view, fragment, "DvsFragment")
                    .addToBackStack("DvsFragment")
                    .commit();
        }
    }

    private void showDvsResultFragment(@NonNull VerificationResult result) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("DvsResultFragment") == null) {
            fm.popBackStackImmediate("DvsTestFragment", 0);

            Fragment fragment = DvsResultFragment.newInstance(result);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, fragment, "DvsResultFragment")
                    .addToBackStack("DvsResultFragment")
                    .commit();
        }
    }

    private void showDvsErrorFragment(@NonNull DvsException error) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("DvsErrorFragment") == null) {
            Fragment fragment = DvsErrorFragment.newInstance(error);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, fragment, "DvsErrorFragment")
                    .addToBackStack("DvsErrorFragment")
                    .commit();
        }
    }

}