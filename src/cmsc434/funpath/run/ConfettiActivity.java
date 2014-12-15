package cmsc434.funpath.run;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import cmsc434.funpath.R;

public class ConfettiActivity extends Activity{

	// These variables are for testing purposes, do not modify
//	private final static int RANDOM = 0;
//	private final static int SINGLE = 1;
//	private final static int STILL = 2;
//	private static int speedMode = RANDOM;
//
//	private static final String TAG = "Lab-Graphics";

	// The Main view
	private RelativeLayout mFrame;

	// Bubble image's bitmap
	private Bitmap mBitmap;

	// Display dimensions
	private int mDisplayWidth, mDisplayHeight;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_confetti);

		// Set up user interface
		mFrame = (RelativeLayout) findViewById(R.id.confetti_frame);
		//mDisplayWidth = mFrame.getWidth();
		//mDisplayHeight = mFrame.getHeight();
		
		//Log.i("CONFETTI", "width:  "+mDisplayWidth);
		//Log.i("CONFETTI", "height: " +mDisplayHeight);
		
		// Load basic bubble Bitmap
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.confetti_1_64);

		int widthMax = 950;
		
		Log.i("CONFETTI", "starting bubble");
		
		Random r = new Random();
		BubbleView newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
		mFrame.addView(newBubble);
		newBubble.start();
		
		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
		mFrame.addView(newBubble);
		newBubble.start();
		
		Log.i("CONFETTI", "started bubbles");
		
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
//		newBubble = new BubbleView(this, r.nextInt(widthMax), r.nextInt(50));
//		mFrame.addView(newBubble);
//		newBubble.start();
		
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

			// Get the size of the display so this View knows where borders are
			mDisplayWidth = mFrame.getWidth();
			mDisplayHeight = mFrame.getHeight();
			Log.i("CONFETTI", "width:  "+mDisplayWidth);
			Log.i("CONFETTI", "height: " +mDisplayHeight);
		}
	}


	// BubbleView is a View that displays a bubble.
	// This class handles animating, drawing, and popping amongst other actions.
	// A new BubbleView is created for each bubble on the display

	public class BubbleView extends View {

		private static final int BITMAP_SIZE = 64;
		private static final int REFRESH_RATE = 40;
		private final Paint mPainter = new Paint();
		private ScheduledFuture<?> mMoverFuture;
		//private Bitmap mScaledBitmap;

		// location, speed and direction of the bubble
		private float mXPos, mYPos, mDx, mDy, mRadius;
		private long mRotate, mDRotate;

		BubbleView(Context context, float x, float y) {
			super(context);

			// Create a new random number generator to
			// randomize size, rotation, speed and direction
			Random r = new Random();

			// Creates the bubble bitmap for this BubbleView
			//createScaledBitmap(r);

			// Radius of the Bitmap
			mRadius = BITMAP_SIZE / 2;
			
			// Adjust position to center the bubble under user's finger
			mXPos = x - mRadius;
			mYPos = y - mRadius;
			
			
			// Set the BubbleView's speed and direction
			setSpeedAndDirection(r);

			// Set the BubbleView's rotation
			setRotation(r);

			mPainter.setAntiAlias(true);

		}

		private void setRotation(Random r) {

			// set rotation in range [1, 2]
			mDRotate = r.nextInt(2)+1;

		}

		private void setSpeedAndDirection(Random r) {


			// Set movement direction and speed
			// Limit movement speed in the x 
			// direction to [-2..2] pixels per movement.
			mDx = r.nextInt(5) - 3;
			mDy = 20;

		}
//
//		private void createScaledBitmap(Random r) {
//
//			if (speedMode != RANDOM) {
//				mScaledBitmapWidth = BITMAP_SIZE * 3;
//			} else {
//
//				// TODO - set scaled bitmap size in range [1..3] * BITMAP_SIZE
//				mScaledBitmapWidth = BITMAP_SIZE * (r.nextInt(3)+1);
//
//			}
//
//			// TODO - create the scaled bitmap using size set above
//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b64);
//			mScaledBitmap = Bitmap.createScaledBitmap(bitmap, mScaledBitmapWidth, mScaledBitmapWidth, false);
//		}

		// Start moving the BubbleView & updating the display
		private void start() {

			// Creates a WorkerThread
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

			// Execute the run() in Worker Thread every REFRESH_RATE
			// milliseconds
			// Save reference to this job in mMoverFuture
			mMoverFuture = executor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {

					// TODO - implement movement logic.
					// Each time this method is run the BubbleView should
					// move one step. If the BubbleView exits the display,
					// stop the BubbleView's Worker Thread.
					// Otherwise, request that the BubbleView be redrawn.

					
					if (moveWhileOnScreen()) {
						BubbleView.this.postInvalidate();
						Log.i("ON SCREEN!", "on screen");
					} else {
						BubbleView.this.stop(false); //TODO check if popped
					}
					
				}
			}, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);
		}

		// Cancel the Bubble's movement
		// Remove Bubble from mFrame
		// Play pop sound if the BubbleView was popped

		private void stop(final boolean wasPopped) {

			if (null != mMoverFuture) {

				if (!mMoverFuture.isDone()) {
					mMoverFuture.cancel(true);
				}

				// This work will be performed on the UI Thread
				mFrame.post(new Runnable() {
					@Override
					public void run() {

						// TODO - Remove the BubbleView from mFrame
						
						mFrame.removeView(BubbleView.this);

						
					}
				});
			}
		}

		// Draw the Bubble at its current location
		@Override
		protected synchronized void onDraw(Canvas canvas) {

			// TODO - save the canvas
			canvas.save();

			// TODO - increase the rotation of the original image by mDRotate
			mRotate += mDRotate;

			// TODO Rotate the canvas by current rotation
			// Hint - Rotate around the bubble's center, not its position
			canvas.rotate(mRotate, mXPos + mRadius, mYPos + mRadius);
			//TODO is it mRotate?

			// TODO - draw the bitmap at it's new location
			canvas.drawBitmap(mBitmap, mXPos, mYPos, mPainter);
			
			// TODO - restore the canvas
			canvas.restore();

		}

		// Returns true if the BubbleView is still on the screen after the move
		// operation
		private synchronized boolean moveWhileOnScreen() {

			// TODO - Move the BubbleView
			mXPos += mDx;
			mYPos += mDy; 
			
			return isOutOfView();

		}

		// Return true if the BubbleView is still on the screen after the move
		// operation
		private boolean isOutOfView() {

			// TODO - Return true if the BubbleView is still on the screen after
			// the move operation
			float leftx = mXPos, rightx = mXPos + 2*mRadius;
			float topy = mYPos, boty = mYPos + 2*mRadius;
			return (rightx > 0 && leftx < mDisplayWidth &&
					boty > 0 && topy < mDisplayHeight);
//				float leftx = mXPos - mRadius, rightx = mXPos + mRadius;
//				float boty = mYPos + mRadius, topy = mYPos - mRadius;
//				return !(rightx < mDisplayWidth || leftx > mDisplayWidth ||
//						boty > mDisplayHeight || topy < mDisplayHeight);
		}
		
	}

//	@Override
//	public void onBackPressed() {
//		openOptionsMenu();
//	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//
//		getMenuInflater().inflate(R.menu.menu, menu);
//
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO- Add cases to handle addition and deletion of bubbles 
//		// from options menu added bubbles should be given random locations.
//		// The bubble to delete is the most recently added bubble that
//		// is still in the frame.
//		
//		// Hint: You can get all Views in mFrame using the 
//		// ViewGroup.getChildCount() method
//		switch (item.getItemId()) {
//		case R.id.menu_add_bubble:
//			Random r = new Random();
//			BubbleView newBubble = new BubbleView(this, r.nextInt(mDisplayWidth), r.nextInt(mDisplayHeight));
//			mFrame.addView(newBubble); //Add at the beginning
//			newBubble.start();
//			return true;
//		case R.id.menu_delete_bubble:
//			if (mFrame.getChildCount() > 0){
//				mFrame.removeViewAt(mFrame.getChildCount() - 1);
//			}
//			return true;
//		case R.id.menu_still_mode:
//			speedMode = STILL;
//			return true;
//		case R.id.menu_single_speed:
//			speedMode = SINGLE;
//			return true;
//		case R.id.menu_random_mode:
//			speedMode = RANDOM;
//			return true;
//		case R.id.quit:
//			exitRequested();
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

	private void exitRequested() {
		super.onBackPressed();
	}
}
