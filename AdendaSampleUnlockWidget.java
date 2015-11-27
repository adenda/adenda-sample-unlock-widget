package com.adenda.knightsanddragons;

import sdk.adenda.widget.AdendaUnlockInterface;
import sdk.adenda.widget.AdendaUnlockWidget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class AdendaSampleUnlockWidget extends RelativeLayout implements AdendaUnlockWidget
{
	private AdendaUnlockInterface mAdendaUnlockInterface;

	public AdendaSampleUnlockWidget(Context context) {
		super(context);
		initialize();
	}
	
	public AdendaSampleUnlockWidget(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		initialize();
	}

	public AdendaSampleUnlockWidget(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initialize();
	}
	
	private void initialize()
	{
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    inflater.inflate(R.layout.adenda_sample_unlock_layout, this);
	}

	@Override
	public void setAdendaUnlockInterface(AdendaUnlockInterface adendaUnlockInterface) {
		mAdendaUnlockInterface = adendaUnlockInterface;
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
}
