package com.ivan.simplemediaplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ivan.simplemediaplayer.domain.Media;
import com.ivan.simplemediaplayer.provider.VideoService;
import com.padplay.android.R;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class VideoPlayerActivity extends Activity {
    private static String TAG = "VideoPlayerTag";


    public static final String BASE_PATH_KEY = "base_path";
    public static final String PATH_KEY = "path";

    public static final String POSITION_KEY = "position";

    private VideoView contentView;
    private ImageView preBtn;
    private ImageView playBtn;
    private ImageView nextBtn;
    private TextView currentTime;
    private TextView totalTime;
    private ImageView fullScreenToggleBtn;
    private SeekBar progressControl;
    private TextView playStatus;
    private TextView titleView;
    private ImageView sideBarBtn;
    private ListView playListView;
    private ImageView gobackBtn;

    private List<Media> playList;
    private List<String> fileNameList = new ArrayList<String>();
    private int currPosition;//当前播放序号
    private String basePath;//当前播放的存储器位置
    private String path;//当前播放的文件夹

    private int duration;

    private final static int SEEKBAR_UPDATE_INTERVAL = 1000;

    private Handler progressUpdateHandler = new Handler();

    private Runnable progressUpdateJob = new Runnable() {
        @Override
        public void run() {
            if (contentView.isPlaying()) {
                updateProgress();
            }
            progressUpdateHandler.postDelayed(this, SEEKBAR_UPDATE_INTERVAL);
        }
    };

    private void updateProgress() {
        int currentPosition = contentView.getCurrentPosition();
        currentTime.setText(convertMillisecond(currentPosition));
        progressControl.setProgress(currentPosition * 100 / duration);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        currPosition = getIntent().getIntExtra(POSITION_KEY, 0);
        basePath = getIntent().getStringExtra(BASE_PATH_KEY);
        path = getIntent().getStringExtra(PATH_KEY);

        playList = VideoService.loadMp4Medias(basePath, path);

        if (playList != null && !playList.isEmpty()) {
            for (Media m : playList) {
                fileNameList.add(m.getDisplayName());
            }
        }

        setContentView(R.layout.video_player_new);

        contentView = (VideoView) findViewById(R.id.content);
        preBtn = (ImageView) findViewById(R.id.els_player_ctrl_pre_btn);
        playBtn = (ImageView) findViewById(R.id.els_player_btn);
        nextBtn = (ImageView) findViewById(R.id.els_player_ctrl_next_btn);
        currentTime = (TextView) findViewById(R.id.els_sco_study_time);
        totalTime = (TextView) findViewById(R.id.els_sco_total_time);
        progressControl = (SeekBar) findViewById(R.id.els_sco_study_progress);
        sideBarBtn = (ImageView) findViewById(R.id.els_player_charpter_btn);
        playListView = (ListView) findViewById(R.id.els_sco_list);
        playStatus = (TextView) findViewById(R.id.playStatus);
        titleView = (TextView) findViewById(R.id.title);
        fullScreenToggleBtn = (ImageView) findViewById(R.id.els_vedio_scale_btn);
        fullScreenToggleBtn.setTag(false);//是否为全屏模式
        gobackBtn = (ImageView) findViewById(R.id.els_player_go_back);

        View controlBar = findViewById(R.id.els_player_ctrl_bottom);

        controlBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });

        initGobackBtn();
        initPreBtn();
        initNextBtn();
        initProgressControl();
        initFullScreenToggleBtn();
        initPlayBtn();
        initVideoPlayer();//初始化VideoView
        initSideBar();
        playMedia(currPosition);
        Log.i(TAG, "onCreate invoked");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (contentView.canPause()) {
            contentView.pause();
        }
        Log.i(TAG, "onPause invoked");
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoStart();
        Log.i(TAG, "onResume invoked");
    }

    private void initGobackBtn() {
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayerActivity.this.finish();
            }
        });
    }

    private void playMedia(int position) {
        if (position < 0 || position > playList.size()) {
            throw new RuntimeException("position is not legal");
        }

        Media target = playList.get(position);
        if (target == null) return;
        contentView.setVideoPath(target.getPath());
        titleView.setText(target.getDisplayName());
        playListView.setItemChecked(position, true);
        currPosition = position;

        contentView.start();
    }

    private void initSideBar() {
        sideBarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playListView.isShown()) {
                    playListView.setVisibility(View.GONE);
                    sideBarBtn.setImageResource(R.drawable.els_player_chapter_off);
                } else {
                    playListView.setVisibility(View.VISIBLE);
                    sideBarBtn.setImageResource(R.drawable.els_player_chapter_on);
                }
                playListView.setSelection(currPosition);
            }
        });

        playListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, fileNameList));
        playListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        playListView.setItemChecked(currPosition, true);
        playListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == currPosition) {
                    if (!contentView.isPlaying()) {
                        contentView.start();
                    }
                } else {
                    playMedia(position);
                }
            }
        });
    }


    private void initPreBtn() {
        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playMedia(currPosition - 1);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(VideoPlayerActivity.this, R.string.can_not_play_pre, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initNextBtn() {
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playMedia(currPosition + 1);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(VideoPlayerActivity.this, R.string.can_not_play_next, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initProgressControl() {
        progressControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int currentPosition = progress * duration / seekBar.getMax();
                    String text = convertMillisecond(currentPosition);
                    currentTime.setText(text);
                    playStatus.setText(text);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoPause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int currentPosition = seekBar.getProgress() * duration / seekBar.getMax();
                contentView.seekTo(currentPosition);
                videoStart();
            }
        });
    }

    private void initFullScreenToggleBtn() {
        fullScreenToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean inFullScreenMode = (Boolean) fullScreenToggleBtn.getTag();
                setFullscreen(!inFullScreenMode);
                fullScreenToggleBtn.setTag(!inFullScreenMode);
                if (inFullScreenMode) {
                    fullScreenToggleBtn.setImageResource(R.drawable.fit_screen);
                } else {
                    fullScreenToggleBtn.setImageResource(R.drawable.full_screen);
                }

            }
        });
    }


    private void initPlayBtn() {
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentView.isPlaying()) {
                    playStatus.setText(R.string.video_play_pause);
                    videoPause();
                } else {
                    videoStart();
                }
            }
        });
    }

    private void setFullscreen(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initVideoPlayer() {
        contentView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setScreenOnWhilePlaying(true);
                duration = contentView.getDuration();
                totalTime.setText(convertMillisecond(duration));

                progressUpdateHandler.postDelayed(progressUpdateJob, SEEKBAR_UPDATE_INTERVAL);
            }
        });

        contentView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
                AlertDialog dialog = builder.setTitle(R.string.dialog_title).setMessage(R.string.video_play_exception).setPositiveButton(R.string.positive_text, null).create();

                dialog.show();

                return true;//return true will not call on completion
            }
        });

        contentView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoPause();
                contentView.seekTo(duration);
            }
        });

        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    updateVideoCtrlBarAutoVisible();
                    return true;
                }
                return false;
            }
        });
    }

    private void updateVideoCtrlBarAutoVisible() {
        View videoCtrlBar = findViewById(R.id.els_vedio_ctrl_wraper);// 视频操作栏
        if (contentView.isPlaying()) {
            if (videoCtrlBar.getVisibility() == View.VISIBLE) {
                videoCtrlBar.setVisibility(View.GONE);
            } else {
                videoCtrlBar.setVisibility(View.VISIBLE);
            }
        } else {
            videoCtrlBar.setVisibility(View.VISIBLE);
        }
    }


    private void videoPause() {
        playBtn.setImageResource(R.drawable.play_btn);
        contentView.pause();
        playStatus.setVisibility(View.VISIBLE);
        progressUpdateHandler.removeCallbacks(progressUpdateJob);
    }

    private void videoStart() {
        playBtn.setImageResource(R.drawable.pause_btn);
        contentView.start();
        playStatus.setVisibility(View.GONE);
        progressUpdateHandler.postDelayed(progressUpdateJob, SEEKBAR_UPDATE_INTERVAL);
    }


    private String convertMillisecond(int millisecond) {
        int totalSeconds = millisecond / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return new Formatter().format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }
}
