package com.lingavin.tapcounter.activities;

import com.lingavin.tapcounter.R;
import com.lingavin.tapcounter.R.layout;
import com.lingavin.tapcounter.controllers.TapController;
import com.lingavin.tapcounter.vos.CounterVo;
import com.lingavin.tapcounter.vos.OnChangeListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class TapActivity extends Activity implements OnChangeListener, Callback {
	
	public static final String EXTRA_TAP_ID = "tapId";

	private static final String TAG = TapActivity.class.getSimpleName();
	private CounterVo counterVo;
	private TapController controller;
	
	private EditText label;
	private TextView count;
	private Button minusBtn;
	private Button plusBtn;
	private CompoundButton lockedBtn;
	
	private static final int UPDATE_VIEW = 0;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			switch(what){
			case UPDATE_VIEW:
				updateView();
				break;
			default:
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		counterVo = new CounterVo();
		counterVo.addListener(this);
		controller = new TapController(counterVo);
		controller.addOutboxHandler(new Handler(this));
		
		initViews();
	}

	private void initViews() {
		label = (EditText) findViewById(R.id.label);
		count = (TextView) findViewById(R.id.countView);
		minusBtn = (Button) findViewById(R.id.minusBtn);
		plusBtn = (Button) findViewById(R.id.plusBtn);
		lockedBtn = (CompoundButton) findViewById(R.id.lockBtn);
		
		plusBtn.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View arg0) {
				controller.handleMessage(TapController.MESSAGE_INCREMENT_COUNT);
			}
			
		});
		
		minusBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				controller.handleMessage(TapController.MESSAGE_DECREMENT_COUNT);
			}
			
		});
	}

	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean handled = controller.handleMessage(TapController.MESSAGE_KEY_EVENT, event);
		if(!handled)
			return super.dispatchKeyEvent(event);
		return handled;
	}

	@Override
	public void onChange(Object model) {
		mHandler.sendEmptyMessage(UPDATE_VIEW);
	}
	
	private void updateView(){
		if(!label.getText().toString().equals(counterVo.getLabel()))
			label.setText(counterVo.getLabel());
		label.setEnabled(!counterVo.isLocked());
		count.setText(Integer.toString(counterVo.getCount()));
		lockedBtn.setChecked(counterVo.isLocked());
	}

	@Override
	public boolean handleMessage(Message arg0) {
		return true;
	}

}
