package com.ivan.simplemediaplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ivan.simplemediaplayer.domain.Media;
import com.ivan.simplemediaplayer.utils.StorageUtils;
import com.padplay.android.R;

import java.io.File;
import java.util.List;

public class MainActivity extends ActionBarActivity implements OnMediaItemClickListener {

    public static final String SHOW_GRID = "showGrid";

    private static final String FRAGMENT_TAG = "fragmentTag";

    private boolean showGridView;
    //保存了用户使用的试图模式
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getPreferences(MODE_PRIVATE);
        showGridView = preferences.getBoolean(SHOW_GRID, true);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle(R.string.home_title_text);
        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_USE_LOGO);

        //add tabs
        List<String> storagePathList = StorageUtils.getSystemFileDiskPath();
        if (storagePathList != null && storagePathList.size() > 0) {
            String tabNamePrefix = getResources().getString(R.string.ext_storage);
            for (int i = 0; i < storagePathList.size(); i++) {
                ActionBar.TabListener listener = new TabListener(this, storagePathList.get(i), getIntent().getExtras());
                ActionBar.Tab tab = actionBar.newTab()
                        .setText(storagePathList.get(i))
                        .setTabListener(listener)
                        .setTag(storagePathList.get(i));
                actionBar.addTab(tab);
            }
        }

    }

    private void toHome() {
        final ActionBar actionBar = getSupportActionBar();
        ActionBar.Tab currentTab = actionBar.getSelectedTab();
        String tabTag = (String) currentTab.getTag();
        //clear back stack
        getSupportFragmentManager().popBackStack(tabTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        changePage(tabTag, "", false);//to home
    }


    @Override
    public void onMediaItemClick(Media media, int position, String basePath, String path) {
        if (media.getMediaType() == Media.MEDIA_TYPE_VIDEO) {
            Intent playIntent = new Intent(this, VideoPlayerActivity.class);
            playIntent.putExtra(VideoPlayerActivity.BASE_PATH_KEY, basePath);
            playIntent.putExtra(VideoPlayerActivity.PATH_KEY, path);
            playIntent.putExtra(VideoPlayerActivity.POSITION_KEY, position);
            startActivity(playIntent);
        } else if (media.getMediaType() == Media.MEDIA_TYPE_DIR) {
            String newPath = path + File.separator + media.getDisplayName();
            changePage(basePath, newPath, true);
        }
    }

    @Override
    public View getEmptyView(LayoutInflater inflater) {
        View emptyView = inflater.inflate(R.layout.empty_view, null);
        Button launchDownload = (Button) emptyView.findViewById(R.id.launch_download);
        launchDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("zz.list.demo");
                if (intent == null) {
                    Toast.makeText(MainActivity.this, R.string.app_download_not_find, Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(intent);
                }
            }
        });

        return emptyView;
    }


    private void changePage(String basePath, String newPath, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment fragment;
        if (showGridView()) {
            fragment = new VideoGridFragment(basePath);
        } else {
            fragment = new VideoListFragment(basePath);
        }

        Bundle args = new Bundle();
        args.putString(VideoPlayerActivity.PATH_KEY, newPath);

        fragment.setArguments(args);
        transaction.replace(android.R.id.content, fragment, basePath);

        if (addToBackStack) {
            transaction.addToBackStack(basePath);
        }

        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_mode:
                boolean showGridView = preferences.getBoolean(SHOW_GRID, true);
                //change menuItem resource
                boolean nextStatus = !showGridView;
                int iconRes = nextStatus ? R.drawable.list_view : R.drawable.grid_view;
                int titleRes = nextStatus ? R.string.to_list_view : R.string.to_grid_view;
                item.setIcon(iconRes);
                item.setTitle(titleRes);
                setShowGridView(nextStatus);

                //reselect it!
                getSupportActionBar().getSelectedTab().select();
                return true;
            case R.id.to_home:
                toHome();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private class TabListener implements ActionBar.TabListener {
        private final FragmentActivity mActivity;
        private final String mBasePath;
        private final Bundle mArgs;
        private Fragment mFragment;
        //tab 内的showGrid标记
        private boolean showGridFlag;

        public TabListener(FragmentActivity mActivity, String bathPath) {
            this(mActivity, bathPath, null);
        }

        public TabListener(FragmentActivity activity, String bathPath, Bundle mArgs) {
            this.mActivity = activity;
            this.mBasePath = bathPath;
            this.mArgs = mArgs;

//            mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mBasePath);
//            if (mFragment != null && !mFragment.isDetached()) {
//                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
//                ft.detach(mFragment);
//                ft.commit();
//            }

            showGridFlag = showGridView();

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if (mFragment == null) {
                mFragment = showGridFlag ? new VideoGridFragment(mBasePath) : new VideoListFragment(mBasePath);
                mFragment.setArguments(mArgs);
                fragmentTransaction.add(android.R.id.content, mFragment, mBasePath);
            } else {
                fragmentTransaction.attach(mFragment);
            }

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if (mFragment != null) {
                fragmentTransaction.detach(mFragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
            final boolean realShowGridFlag = showGridView();
            if (showGridFlag != realShowGridFlag && mFragment != null) {//
                mFragment = showGridFlag ? new VideoGridFragment(mBasePath) : new VideoListFragment(mBasePath);
                mFragment.setArguments(mArgs);
                fragmentTransaction.replace(android.R.id.content, mFragment, mBasePath);

                showGridFlag = realShowGridFlag;
            }

        }

    }

    private boolean showGridView() {
        return preferences.getBoolean(SHOW_GRID, true);
    }

    private void setShowGridView(boolean newValue) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHOW_GRID, newValue);
        editor.commit();
    }
}
