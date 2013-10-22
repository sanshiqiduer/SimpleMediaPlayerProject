package com.ivan.simplemediaplayer;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.simplemediaplayer.domain.Media;
import com.ivan.simplemediaplayer.provider.VideoService;
import com.ivan.simplemediaplayer.utils.GridItemAnalyzer;
import com.ivan.simplemediaplayer.utils.ImageCache;
import com.ivan.simplemediaplayer.utils.ImageFetcher;
import com.ivan.simplemediaplayer.utils.ImageResizer;
import com.padplay.android.R;

import java.util.List;

/**
 * @author: Ivan Vigoss
 * Date: 13-10-8
 * Time: PM7:56
 */
@SuppressLint("NewApi")
public class VideoGridFragment extends VideoBaseFragment {

    private static final String IMAGE_CACHE_DIR = "thumbs";

    private GridView gridView;

    private int thumbImageSize;

    private int columns;

    private ImageFetcher mImageFetcher;

    private GridItemAnalyzer gridItemAnalyzer;

    @Override
    public void init() {
        mAdapter = new GridAdapter(getActivity(), playList);

        //根据屏幕尺寸获取图片的缩放尺寸
        gridItemAnalyzer = new GridItemAnalyzer(getActivity());
        Pair<Integer, Integer> iconConfig = gridItemAnalyzer.getIconConfig();
        thumbImageSize = iconConfig.first;
        columns = iconConfig.second;

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.imageSize = thumbImageSize;

        cacheParams.setMemCacheSizePercent(0.5f);

        mImageFetcher = new ImageFetcher(getActivity(), thumbImageSize);

        mImageFetcher.setLoadingImage(mImageFetcher.processBitmap(R.drawable.empty_photo));
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (isEmpty()) {
            return callback.getEmptyView(inflater);
        }
        final View view = inflater.inflate(R.layout.video_grid_view, container, false);
        gridView = (GridView) view.findViewById(R.id.grid_view);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(this);
        gridView.setNumColumns(columns);
        gridView.setColumnWidth(thumbImageSize);
        gridView.setHorizontalSpacing(GridItemAnalyzer.GRID_PADDING);
        return view;
    }


    private class GridAdapter extends VideoBaseAdapter {
        protected GridAdapter(Context context, List<Media> playList) {
            super(context, playList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.grid_item, null);
                viewHolder = new ViewHolder();

                viewHolder.icon = (ImageView) convertView.findViewById(R.id.grid_item_img);
                viewHolder.text = (TextView) convertView.findViewById(R.id.grid_item_text);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Media media = playList.get(position);
            if (media.getMediaType() == Media.MEDIA_TYPE_VIDEO) {
                mImageFetcher.loadImage(media.getPath(), viewHolder.icon);
            } else {
                Bitmap icon = mImageFetcher.processBitmap(R.drawable.files_grid);
                viewHolder.icon.setImageBitmap(icon);
            }

            String displayName = media.getDisplayName();
            viewHolder.text.setText(displayName);
            return convertView;
        }

    }


}
