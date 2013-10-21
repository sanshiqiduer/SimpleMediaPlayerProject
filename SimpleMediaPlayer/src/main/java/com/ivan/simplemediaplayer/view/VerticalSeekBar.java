package com.ivan.simplemediaplayer.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.padplay.android.R;


public class VerticalSeekBar extends TextView {

	private int max = 100;
	private int min = 0;
	private int progress = 0;

	private ProgressListener listener;

	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VerticalSeekBar(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			trackTouchEvent(event);

			break;

		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();

		Rect rect = new Rect();
		getDrawingRect(rect);

		float x = rect.left + rect.width() * 1.0f / 2;
		float y = rect.bottom - paddingBottom
				- (rect.height() - paddingTop - paddingBottom)
				* (progress - min) * 1.0f / (max - min);

		Resources resources = getResources();
		TextPaint textPaint = getPaint();
		int white = resources.getColor(R.color.white);
		int black = resources.getColor(R.color.black);
		textPaint.setColor(black);
		textPaint.setStrokeCap(Cap.ROUND);
		textPaint.setStrokeWidth(6);
		canvas.drawLine(x, rect.top + paddingTop, x, y, textPaint);
		textPaint.setColor(white);
		canvas.drawLine(x, y, x, rect.bottom - paddingBottom, textPaint);

		BitmapDrawable drawable = (BitmapDrawable) resources
				.getDrawable(R.drawable.els_player_volume_seek);
		Bitmap bitmap = drawable.getBitmap();
		int width = bitmap.getWidth();
		int hight = bitmap.getHeight();
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(bitmap, (float) (x - width * 1.0 / 2),
				(float) (y - hight * 1.0 / 2), paint);
	}

	private void trackTouchEvent(MotionEvent event) {
		final int Height = getHeight();
		final int available = Height - getPaddingBottom() - getPaddingTop();
		int Y = (int) event.getY();
		float scale;
		float progress = 0;
		if (Y > Height - getPaddingBottom()) {
			scale = 0.0f;
		} else if (Y < getPaddingTop()) {
			scale = 1.0f;
		} else {
			scale = (float) (Height - getPaddingBottom() - Y)
					/ (float) available;
		}

		final int max = getMax();
		progress = scale * max;

		setProgress((int) progress, true);
	}

	public static interface ProgressListener {
		void change(int progress);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress, boolean notify) {
		if (this.progress == progress) {
			return;
		}

		if (progress < min) {
			progress = min;
		} else if (progress > max) {
			progress = max;
		}

		this.progress = progress;

		if (listener != null && notify) {
			listener.change(progress);
		}

		invalidate();
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public ProgressListener getListener() {
		return listener;
	}

	public void setListener(ProgressListener listener) {
		this.listener = listener;
	}
}
