package org.appsroid.panda;

import org.appsroid.panda.gpuimage.GPUImageFilter;
import org.appsroid.panda.gpuimage.GPUImageRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Created by imochalov on 12.09.2015.
 */
// Extend GLSurfaceView to receive touch events.
public class TouchGLView
		extends GLSurfaceView
		implements GestureDetector.OnGestureListener,
		ScaleGestureDetector.OnScaleGestureListener {

	private GPUImageRenderer     mRenderer;
	private GestureDetector      mTapDetector;
	private ScaleGestureDetector mScaleDetector;
	private float mLastSpan                   = 0;
	private long  mLastNonTapTouchEventTimeNS = 0;

	public TouchGLView(Context c) {
		super(c);
		GPUImageRenderer renderer = new GPUImageRenderer(new GPUImageFilter());
		init(c, renderer);
	}

	public void init(Context c, GPUImageRenderer renderer) {
		mRenderer = renderer;
		mTapDetector = new GestureDetector(c, this);
		mTapDetector.setIsLongpressEnabled(false);
		mScaleDetector = new ScaleGestureDetector(c, this);
	}

	public TouchGLView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent e) {
		// Forward touch events to the gesture detectors.
		mScaleDetector.onTouchEvent(e);
		mTapDetector.onTouchEvent(e);
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
			final float dx, final float dy) {
		// Forward the drag event to the renderer.
		/*queueEvent(new Runnable() {

			public void run() {
				// This Runnable will be executed on the render
				// thread.
				// In a real app, you'd want to divide these by
				// the display resolution first.

				mRenderer.drag(dx, dy);
				requestRender();
			}
		});*/
		mLastNonTapTouchEventTimeNS = System.nanoTime();
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		// Forward the scale event to the renderer.
		final float amount = detector.getCurrentSpan() - mLastSpan;
		queueEvent(new Runnable() {
			public void run() {
				// This Runnable will be executed on the render
				// thread.
				// In a real app, you'd want to divide this by
				// the display resolution first.
				mRenderer.zoom(amount);
				requestRender();
			}});
		mLastSpan = detector.getCurrentSpan();
		mLastNonTapTouchEventTimeNS = System.nanoTime();
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		mLastSpan = detector.getCurrentSpan();
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}

	@Override
	public boolean onDown(MotionEvent e) {
		final int x = (int)e.getX();
		final int y = (int)e.getY();
		queueEvent(new Runnable() {

			public void run() {
				// This Runnable will be executed on the render
				// thread.
				// In a real app, you'd want to divide these by
				// the display resolution first.

				mRenderer.replace(x,y);
				requestRender();
			}
		});
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}