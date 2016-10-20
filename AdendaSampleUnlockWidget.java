package com.adenda.example;

import sdk.adenda.widget.AdendaUnlockInterface;
import sdk.adenda.widget.AdendaUnlockWidget;
import sdk.adenda.widget.DateTimeFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

public class AdendaSampleUnlockWidget extends RelativeLayout implements AdendaUnlockWidget
{
	private AdendaUnlockInterface mAdendaUnlockInterface;
	private GestureDetectorCompat mDetector;
	private AdendaSampleGestureListener mListener;
	private Context mContext;

	public AdendaSampleUnlockWidget(Context context) {
		super(context);
		mContext = context;
		initialize();
	}
	
	public AdendaSampleUnlockWidget(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		mContext = context;
		initialize();
	}

	public AdendaSampleUnlockWidget(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mContext = context;
		initialize();
	}
	
	private void initialize()
	{
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    inflater.inflate(R.layout.adenda_sample_unlock_layout, this);
	    if (Activity.class.isInstance(mContext))
	    {
	    	mListener = new AdendaSampleGestureListener(mContext);
	    	mDetector = new GestureDetectorCompat(mContext, mListener);
	    }
	}

	@Override
	public void setAdendaUnlockInterface(AdendaUnlockInterface adendaUnlockInterface) {
		mAdendaUnlockInterface = adendaUnlockInterface;
		if (mListener != null)
			mListener.setAdendaUnlockInterface(adendaUnlockInterface);
	}
	
	@Override
	protected void onFinishInflate() {
	    super.onFinishInflate();

	    Button btnUnlock = (Button) findViewById(R.id.btn_learn_more);
	    if (btnUnlock != null)
	    	btnUnlock.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (mAdendaUnlockInterface != null)
						mAdendaUnlockInterface.unlockAndEngage();
				}
			});

		// Get Screen Height
		Float screenHeight = getScreenHeight();
		if (screenHeight == null)
			return;

		Float dateTimeSize = null;
		View customContentHolder = findViewById(R.id.adenda_content_container);
		// And make sure to scale Date/Time accordingly in order to keep at least 75% of the screen real-estate for ads
		if (customContentHolder != null)
		{
			float btnHeight = pixelToDeviceIndependentPixel(getResources().getDimension(R.dimen.button_height));
			float requiredSize = (float)(screenHeight * 0.25);
			ViewGroup.MarginLayoutParams customContentLayoutParams = (ViewGroup.MarginLayoutParams)customContentHolder.getLayoutParams();
			float margins = pixelToDeviceIndependentPixel(customContentLayoutParams.topMargin + customContentLayoutParams.bottomMargin);
			// <-- Factor of DEFAULT_DATE_FACTOR is to account for the additional height of the date, which is typically ~0.27 * height of the clock.
			// 40f to account for default padding in Text Views
			// 25f to account for system bar
			float timeSize = (requiredSize - btnHeight - margins - 40f - 25f) / (1 + DateTimeFragment.DEFAULT_DATE_FACTOR);
			// Convert to Scaled Pixels (to take into account user preferences)
			dateTimeSize =  deviceIndependentPixelToScaledPixel(timeSize);
			// Don't scale up, only scale down
			if (dateTimeSize > DateTimeFragment.DEFAULT_TXT_SIZE)
				dateTimeSize = null;
		}

		if (FragmentActivity.class.isInstance(mContext))
			((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.managed_custom_date_time_fragment_holder, DateTimeFragment.newInstance(null, dateTimeSize, null, true, true)).commitAllowingStateLoss();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override 
    public boolean onTouchEvent(MotionEvent event)
	{
		if (mListener != null && event != null && event.getAction() == MotionEvent.ACTION_UP)
			mListener.onUp(event);
		
		if (mDetector != null)
			mDetector.onTouchEvent(event);		
       
        return true;
	}

	@Override
	public void onNewImpression() {
		if (mAdendaUnlockInterface == null)
			return;
		
		Log.d(getClass().getSimpleName(), "Impression Recorded: " + mAdendaUnlockInterface.getContentParams().getType());
	}

	@Override
	public void onClick() {
	}

	@Override
	public boolean getMaintainAspectRatio() {
		return true;
	}

	@Override
	public boolean getDisableBackgroundAutofill() {
		return true;
	}

	@Override
	public void onCtaEnabledChanged(CTA_STATUS status) {
		Button btnUnlock = (Button) findViewById(R.id.btn_learn_more);
		if (btnUnlock == null)
			return;

		boolean bEnabled = status == CTA_STATUS.ENABLED;
		btnUnlock.setEnabled(bEnabled);

		if (status == CTA_STATUS.INTERACTIVE_OFF)
			btnUnlock.setText("Interact With Ad Directly");

		else if (status == CTA_STATUS.ENABLED)
			btnUnlock.setText("Learn More");
	}

	private Float getScreenHeight()
	{
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		if (metrics == null)
			return null;

		windowManager.getDefaultDisplay().getMetrics(metrics);

		int heightPixels = metrics.heightPixels;
		return (float)heightPixels / metrics.density;
	}

	private float pixelToDeviceIndependentPixel(float px) {
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return (float)px / metrics.density;
	}

	private float deviceIndependentPixelToScaledPixel(float px) {
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return (float)px * metrics.density / metrics.scaledDensity;
	}
}
