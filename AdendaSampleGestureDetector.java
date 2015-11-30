package com.adenda.knightsanddragons.unlock;

import sdk.adenda.modules.AdendaGlobal;
import sdk.adenda.widget.AdendaUnlockInterface;
import sdk.adenda.widget.AdendaUnlockWidget;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.AlphaAnimation;

public class AdendaSampleGestureDetector extends GestureDetectorCompat 
{
	public static AdendaSampleGestureDetector newInstance(Activity context)
	{
		AdendaSampleGestureListener listener = new AdendaSampleGestureListener(context);
		return new AdendaSampleGestureDetector(context, listener);
	}
	
	AdendaSampleGestureListener mListener;
	
	public AdendaSampleGestureDetector(Context context, AdendaSampleGestureListener listener) 
	{
		super(context, listener);
		mListener = listener;
	}

	public boolean onTouchEvent(MotionEvent ev) {
		if (ev != null && ev.getAction() == MotionEvent.ACTION_UP)
			mListener.onUp(ev);
		return super.onTouchEvent(ev);
	}
	
	public static class AdendaSampleGestureListener extends SimpleOnGestureListener implements AdendaUnlockWidget
	{
		private Activity mActivity;
		private DisplayMetrics mScreenMetrics;
		private int mPendingAction;
		private AdendaUnlockInterface mAdendaUnlockInterface;
		
		public AdendaSampleGestureListener(Activity activity)
		{
			this.mActivity = activity;
			mScreenMetrics = new DisplayMetrics();
			if (activity != null)
				((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mScreenMetrics);
			mPendingAction = -1;
			
			if (AdendaUnlockInterface.class.isInstance(activity))
				setAdendaUnlockInterface((AdendaUnlockInterface)activity);
		}
		
		
		@Override
	    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
	        //Log.d(getClass().getSimpleName(), "onFling: " + event1.toString()+event2.toString());
	        
			if (mAdendaUnlockInterface == null)
				return false;
			
	        float diffY = event1.getRawY() - event2.getRawY();
	        float diffX = event1.getRawX() - event2.getRawX();
	        
	        // Check for a 'minimum' fling
	        if (Math.abs(diffY) < mScreenMetrics.heightPixels/4 && Math.abs(diffX) < mScreenMetrics.widthPixels/4)
	        	return false;
	        
	        // If we already have an action pending, then don't bother triggering another
	        if (mPendingAction >= 0)
	        	return false;
	        
	        // If it was a swipe up
	        if (mActivity != null && diffY > 0 && Math.abs(diffX) < Math.abs(diffY))
	        	// Perform unlock action
	        	simpleUnlock();
	        
	        // If it was a swipe down
	        else if (mActivity != null && diffY < 0 && Math.abs(diffX) < Math.abs(diffY))
	        	// Launch Google Now
	        	launchGoogleNow();
	        
	        // If it was a swipe left
	        else if (mActivity != null && diffX > 0 && Math.abs(diffY) < Math.abs(diffX))
	        	// Launch Camera
	        	launchCamera();
	        
	        // If it was a swipe right
	        else if (mActivity != null && diffX < 0 && Math.abs(diffY) < Math.abs(diffX))
	        	// Launch Action
	        	engageContent();
	        
	        return true;
	    }

	    @Override
	    public void onLongPress(MotionEvent event) {
	        //Log.d(getClass().getSimpleName(), "onLongPress: " + event.toString()); 
	    }

		@Override
	    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) 
	    {
	    	if (mActivity == null || event1 == null || event2 == null)
	    		return false;
	    	
	    	float diffY = event1.getRawY() - event2.getRawY();
	        float diffX = event1.getRawX() - event2.getRawX();
	        
	        // If it was a swipe up
	        if (diffY > 0 && Math.abs(diffX) < Math.abs(diffY))
	        {	
	        	float trigger = mScreenMetrics.heightPixels/3;
	        	
	        	if ( Math.abs(diffY) >= trigger)
	        		// Just unlock
	        		mPendingAction = 0;
	        	else
	        		mPendingAction = -1;
	        }
	        
	        // If it was a swipe down
	        else if (diffY < 0 && Math.abs(diffX) < Math.abs(diffY))
	        {	
	        	float trigger = mScreenMetrics.heightPixels/3;
	        	     	
	        	if ( Math.abs(diffY) >= trigger)
		        	// Launch Google Now
	        		mPendingAction = 3;
	        	else
	        		mPendingAction = -1;
	        }
	        
	        // If it was a swipe left
	        else if (diffX > 0 && Math.abs(diffY) < Math.abs(diffX))
	        {	
	        	float trigger = mScreenMetrics.widthPixels/3;
	        
	        	if ( Math.abs(diffX) >= trigger)
		        	// Launch Camera
	        		mPendingAction = 1;
	        	else
	        		mPendingAction = -1;
	        }
	        
	        // If it was a swipe right
	        else if (diffX < 0 && Math.abs(diffY) < Math.abs(diffX))
	        {
	        	float trigger = mScreenMetrics.widthPixels/3;
	        	
	        	if ( Math.abs(diffX) >= trigger)
	        		// Launch Action
	        		mPendingAction = 2;
	        	else
	        		mPendingAction = -1;
	        }
	        
	        return true;
	    }
	    
	    public void onUp(MotionEvent e)
	    {
	        // If we have a pending action, execute it
	        if (mPendingAction >= 0)
	        	triggerAction( mPendingAction);
	    }
	    
	    @SuppressLint("NewApi")
		private void setAlpha (View view, float alpha)
	    {
	    	if (view == null)
	    		return;
	    	
	    	// Make sure view is visible
	    	view.setVisibility(View.VISIBLE);
	    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) 		
	    		view.setAlpha(alpha);
	    	else
	    	{
	    		view.setVisibility(View.VISIBLE);
	    		AlphaAnimation alphaAnim = new AlphaAnimation(alpha, alpha);
	    		alphaAnim.setDuration(0); // Make animation instant
	    		alphaAnim.setFillAfter(true); // Tell it to persist after the animation ends
	    		view.startAnimation(alphaAnim);
	    	}
	    }


		@Override
		public void setAdendaUnlockInterface( AdendaUnlockInterface adendaUnlockInterface) {
			mAdendaUnlockInterface = adendaUnlockInterface;
		}
		
		private void triggerAction(int pendingAction)
		{
			switch(pendingAction)
			{
			case 0:
				// Perform unlock action
				simpleUnlock();
	        	break;
			case 1:
				launchCamera();
				break;
			case 2:
				engageContent();
				break;
			case 3:
				launchGoogleNow();
				break;
			default:
			}
		}
		
		private void simpleUnlock()
		{
			mAdendaUnlockInterface.simpleUnlock();
		}
		
		private void launchCamera()
		{
			// Launch Camera
	    	mAdendaUnlockInterface.unlockAndPerformIntent(AdendaGlobal.getActionIntentFromString( mActivity, MediaStore.ACTION_IMAGE_CAPTURE));
		}
		
		private void engageContent()
		{
			mAdendaUnlockInterface.unlockAndEngage();
		}
		
		private void launchGoogleNow()
		{
			mAdendaUnlockInterface.unlockAndPerformIntent(AdendaGlobal.getActionIntentFromString( mActivity, "com.google.android.googlequicksearchbox"));
		}
	}
}
