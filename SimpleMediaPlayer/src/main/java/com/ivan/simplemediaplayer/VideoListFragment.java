package com.ivan.simplemediaplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ivan.simplemediaplayer.domain.Media;
import com.ivan.simplemediaplayer.provider.VideoService;
import com.padplay.android.R;

import java.util.List;

/**
 * @author: Ivan Vigoss
 * Date: 13-10-8
 * Time: PM7:19
 */
@SuppressLint("NewApi")
public class VideoListFragment extends VideoBaseFragment {

    private ListView mListView;

    @Override
    public void init() {
        mAdapter =  new IconTextAdapter(getActivity(), playList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (isEmpty()) {
            return callback.getEmptyView(inflater);
        }
        final View view = inflater.inflate(R.layout.video_list_view, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mListView.setEmptyView(callback.getEmptyView(inflater));
        return view;
    }

    private class IconTextAdapter extends VideoBaseAdapter {

        protected IconTextAdapter(Context context, List<Media> playList) {
            super(context, playList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_icon_text, null);
                viewHolder = new ViewHolder();

                viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
                viewHolder.text = (TextView) convertView.findViewById(R.id.text);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Media media = playList.get(position);
            Bitmap icon;
            if (media.getMediaType() != Media.MEDIA_TYPE_DIR) {
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.mp4);
            } else {
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.files);
            }

            viewHolder.icon.setImageBitmap(icon);
            String displayName = playList.get(position).getDisplayName();
            viewHolder.text.setText(displayName);

            return convertView;
        }

    }
}
