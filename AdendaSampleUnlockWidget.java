package com.adenda.knightsanddragons.unlock;

import com.adenda.knightsanddragons.R;

import sdk.adenda.widget.AdendaUnlockInterface;
import sdk.adenda.widget.AdendaUnlockWidget;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class AdendaSampleUnlockWidget extends RelativeLayout implements AdendaUnlockWidget
{
	private AdendaUnlockInterface mAdendaUnlockInterface;
	private AdendaSampleGestureDetector mDetector;
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
	    	mDetector = AdendaSampleGestureDetector.newInstance((Activity)mContext);
	}

	@Override
	public void setAdendaUnlockInterface(AdendaUnlockInterface adendaUnlockInterface) {
		mAdendaUnlockInterface = adendaUnlockInterface;
		if (mDetector != null)
			mDetector.setAdendaUnlockInterface(adendaUnlockInterface);
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
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override 
    public boolean onTouchEvent(MotionEvent event)
	{
		if (mDetector != null)
			mDetector.onTouchEvent(event);		
       
        return true;
	}

	@Override
	public void onNewImpression() {
		Log.d(getClass().getSimpleName(), "Impression Recorded");
	}
}
