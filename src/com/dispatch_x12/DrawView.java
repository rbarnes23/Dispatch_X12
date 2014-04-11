package com.dispatch_x12;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawView extends SurfaceView implements OnTouchListener {
	Paint paint = new Paint();
	Path mPath = new Path();
	Base64 mBase64 = new Base64();
	private Context mContext;
	private Canvas mCanvas;
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;
	private static final int IMAGE_MAX_SIZE = 450;
	private static final int SUCCESS = 1;
	private Bitmap mBitmap;
	private Paint mBitmapPaint;
	private Paint mPaint;
	private int mBackGroundColor = Color.GRAY;
	private int mPaintColor = Color.WHITE;
	private float mSW = 5;
	private int mBrushOpacity=255;
	private int mBackgroundOpacity = 255;
	File folder = new File(Environment.getExternalStorageDirectory()
			+ "/artytheartist");
	private BitmapDrawable mBackGround;
	Bitmap mColorSampleBitmap;


	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(mSW);
		setBackgroundColor(mBackGroundColor);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// maxScrollX= hz.getWidth();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		this.setBackgroundColor(mBackGroundColor);
		// this.onSetAlpha(mBackgroundOpacity/255);
		if (mBackGround != null) {
			this.setBackgroundDrawable(mBackGround);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mPaint);
	}

	private void touch_start(float x, float y)  {


		mPaint.setXfermode(null);
		mPaint.setColor(mPaintColor);
		mPaint.setStrokeWidth(mSW);
		mPaint.setAlpha(mBrushOpacity);
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;


	//		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


	}

	private void touch_move(float x, float y)  {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up()  {
		mPath.lineTo(mX, mY);

		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);

		// Lets save the path to our array of paths
		// kill this so we don't double draw
		mPath.reset();
	}








	@Override
	public boolean onTouch(View view, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:

					touch_start(x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					touch_move(x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					touch_up();
					invalidate();
					break;

				}
				return true;
	}




	// Notice difference in spelling...not inherited
	public void setBackGroundColor(int aColor) {

		mBackGroundColor = aColor;

		if (Build.VERSION.SDK_INT > 10) {
			if (mBackgroundOpacity > 0) {
				int alpha = mBackgroundOpacity / 255;
				this.onSetAlpha(alpha);
			}
		}

		this.setBackgroundColor(aColor);

		mBackGround = null;
	}

	private void clear() {
		mBitmap = Bitmap.createBitmap(mCanvas.getWidth(), mCanvas.getHeight(),
				Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		invalidate();
	}

	public File save(String aFilename) {
		Bitmap mBitmaptoSave;
		// Save Bitmap to File
		FileOutputStream fos = null;
		File filename = null;
		this.setDrawingCacheEnabled(true);
		try {
			boolean mSuccess = false;
			if (!folder.exists()) {
				mSuccess = folder.mkdir();
			}

			filename = new File(folder + "/" + aFilename + ".png");

			fos = new FileOutputStream(filename);
			mBitmaptoSave = this.getDrawingCache(); // This allows background to
													// be saved also

			mBitmaptoSave.compress(Bitmap.CompressFormat.PNG, 100, fos);

			fos.flush();
			fos.close();
			fos = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setDrawingCacheEnabled(false); // This clears the cache for next
											// save
clear();
		return filename;

	}




	public void freememory() {
		Runtime freememory = Runtime.getRuntime();
		freememory.gc();
	}

	public int displayUserBitmap(String aFileName, int left, int top) {
		// Restore Bitmap to background
		int mReturn = -1;
		freememory();

		File filename = new File(aFileName);
		Bitmap bm = decodeFile(filename, 0);
		// int w = mCanvas.getWidth();
		bm = bm.createScaledBitmap(bm, 50, 50, false);
		if (bm == null) {
			return mReturn;
		}
		return SUCCESS;
	}


	public Bitmap decodeFile(File f, int imagesize) {
		if (imagesize == 0) {
			imagesize = IMAGE_MAX_SIZE;
		}
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			FileInputStream fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;

			if (o.outHeight > imagesize || o.outWidth > imagesize) {
				scale = (int) Math.pow(
						2.0,
						(int) Math.round(Math.log(imagesize
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
			freememory();
		} catch (IOException e) {
		}
		return b;
	}

	public String getStringFromBitmap(Bitmap bitmapPicture) {
		/*
		 * This functions converts Bitmap picture to a string which can be
		 * JSONified.
		 */
		final int COMPRESSION_QUALITY = 100;
		String encodedImage = null;
		ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
		bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
				byteArrayBitmapStream);
		byte[] b = byteArrayBitmapStream.toByteArray();
		encodedImage = Base64.encodeToString(b, false);
		return encodedImage;
	}

	public String getStringFromCurrentBitmap() {
		this.setDrawingCacheEnabled(true);
		Bitmap bm = Bitmap.createBitmap(this.getDrawingCache());
		String encodedBitmap = getStringFromBitmap(bm);
		this.setDrawingCacheEnabled(false); // This clears the cache for next
		clear();
		return encodedBitmap;
	}	

	
	public Bitmap getBitmapFromString(String jsonString) {
		/*
		 * This Function converts the String back to Bitmap
		 */
		byte[] decodedString = Base64.decode(jsonString);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
				decodedString.length);
		return decodedByte;
	}

	public String cleanUp() {
		try {
			this.finalize();
		} catch (Throwable e) {
			return e.getMessage();
		}
		return "Closed";

	}

}