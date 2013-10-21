package com.ivan.simplemediaplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

    private static final String GRID_FRAGMENT_TAG = "gridFragmentTag";
    private static final String LIST_FRAGMENT_TAG = "listFragmentTag";

    private View gridView;
    private View listView;
    private boolean showGridView;
    //保存了用户使用的试图模式
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        gridView = findViewById(R.id.grid_container);
        listView = findViewById(R.id.list_container);

        preferences = getPreferences(MODE_PRIVATE);
        showGridView = preferences.getBoolean(SHOW_GRID, true);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.home_title_text);

        //add tabs
        List<String> storagePathList = StorageUtils.getSystemFileDiskPath();
        if (storagePathList != null && storagePathList.size() > 0) {
            String tabNamePrefix = getResources().getString(R.string.ext_storage);
            for (int i = 0; i < storagePathList.size(); i++) {
                System.out.println(storagePathList.get(i));
            }
        }


        if (getSupportFragmentManager().findFragmentByTag(GRID_FRAGMENT_TAG) == null) {
            toHome();
        }

        toggleGridView(showGridView);//初始化的界面

    }

    private void toHome() {
        //clear back stack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        String basePath = Environment.getExternalStorageDirectory().getPath();
        changePage(basePath, "", false);//to home
    }

    private void saveShowMode(SharedPreferences preferences, boolean showGridView) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHOW_GRID, showGridView);
        editor.commit();
    }

    private void toggleGridView(Boolean showGridView) {
        if (showGridView) {
            gridView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            gridView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMediaItemClick(Media media, int position, String basePath, String path) {
        if (media.getMediaType() == Media.MEDIA_TYPE_VIDEO) {
            Intent playIntent = new Intent(this, VideoPlayerActivity.class);
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

        Fragment gridFragment = new VideoGridFragment(basePath);
        Fragment listFragment = new VideoListFragment(basePath);

        Bundle args = new Bundle();
        args.putString(VideoPlayerActivity.PATH_KEY, newPath);

        gridFragment.setArguments(args);
        listFragment.setArguments(args);

        transaction.replace(R.id.grid_container, gridFragment, GRID_FRAGMENT_TAG);
        transaction.replace(R.id.list_container, listFragment, LIST_FRAGMENT_TAG);

        if (addToBackStack) {
            transaction.addToBackStack(null);
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

                toggleGridView(nextStatus);
                saveShowMode(preferences, nextStatus);
                return true;
            case R.id.to_home:
                toHome();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static class TabListener<T extends VideoBaseFragment> implements ActionBar.TabListener{


        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }
    }
}
