package com.ivan.simplemediaplayer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.simplemediaplayer.domain.Media;
import com.ivan.simplemediaplayer.provider.VideoService;

import java.util.List;

/**
 * @author: Ivan Vigoss
 * Date: 13-10-21
 * Time: PM7:56
 */
public abstract class VideoBaseFragment extends Fragment implements AdapterView.OnItemClickListener {

    protected VideoBaseAdapter mAdapter;

    protected OnMediaItemClickListener callback;

    protected List<Media> playList;

    protected String bathPath;

    protected String path = "";

    public VideoBaseFragment(String bathPath) {
        this.bathPath = bathPath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            path = args.getString(VideoPlayerActivity.PATH_KEY);
        }
        playList = VideoService.loadMedias(bathPath, path);

        init();
    }

    public abstract void init();

    protected boolean isEmpty() {
        return playList == null || playList.size() == 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Media media = playList.get(position);
        callback.onMediaItemClick(media, position, bathPath, path);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (OnMediaItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMediaItemClickListener");
        }
    }


    public abstract class VideoBaseAdapter extends BaseAdapter {
        protected LayoutInflater mInflater;
        protected Context context;
        protected List<Media> playList;

        protected VideoBaseAdapter(Context context, List<Media> playList) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.playList = playList;
        }

        @Override
        public int getCount() {
            return playList != null ? playList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return playList != null ? playList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        protected class ViewHolder {
            TextView text;
            ImageView icon;
        }
    }
}
