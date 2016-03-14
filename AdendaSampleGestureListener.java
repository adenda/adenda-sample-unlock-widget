package com.adenda.knightsanddragons.unlock;

import sdk.adenda.modules.AdendaGlobal;
import sdk.adenda.widget.AdendaUnlockInterface;
import android.content.Context;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;

public class AdendaSampleGestureListener extends SimpleOnGestureListener
{
	private Context mContext;
	private DisplayMetrics mScreenMetrics;
	private int mPendingAction;
	private AdendaUnlockInterface mAdendaUnlockInterface;
	
	public AdendaSampleGestureListener(Context context)
	{
		mContext = context;
		mScreenMetrics = new DisplayMetrics();
		if (context != null)
			((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mScreenMetrics);
		mPendingAction = -1;
	}
	
	public void setAdendaUnlockInterface( AdendaUnlockInterface adendaUnlockInterface) {
		mAdendaUnlockInterface = adendaUnlockInterface;
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
        if (mContext != null && diffY > 0 && Math.abs(diffX) < Math.abs(diffY))
        	// Perform unlock action
        	simpleUnlock();
        
        // If it was a swipe down
        else if (mContext != null && diffY < 0 && Math.abs(diffX) < Math.abs(diffY))
        	// Launch Google Now
        	launchGoogleNow();
        
        // If it was a swipe left
        else if (mContext != null && diffX > 0 && Math.abs(diffY) < Math.abs(diffX))
        	// Launch Camera
        	launchCamera();
        
        // If it was a swipe right
        else if (mContext != null && diffX < 0 && Math.abs(diffY) < Math.abs(diffX))
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
    	if (mContext == null || event1 == null || event2 == null)
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
		if (mAdendaUnlockInterface != null)
			mAdendaUnlockInterface.simpleUnlock();
	}
	
	private void launchCamera()
	{
		// Launch Camera
		if (mAdendaUnlockInterface != null)
			mAdendaUnlockInterface.unlockAndPerformIntent(AdendaGlobal.getActionIntentFromString( mContext, MediaStore.ACTION_IMAGE_CAPTURE));
	}
	
	private void engageContent()
	{
		if (mAdendaUnlockInterface != null)
			mAdendaUnlockInterface.unlockAndEngage();
	}
	
	private void launchGoogleNow()
	{
		if (mAdendaUnlockInterface != null)
			mAdendaUnlockInterface.unlockAndPerformIntent(AdendaGlobal.getActionIntentFromString( mContext, "com.google.android.googlequicksearchbox"));
	}
}
