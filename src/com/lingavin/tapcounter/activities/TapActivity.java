package com.lingavin.tapcounter.activities;

import com.lingavin.tapcounter.R;
import com.lingavin.tapcounter.controllers.TapController;
import com.lingavin.tapcounter.vos.CounterVo;
import com.lingavin.tapcounter.vos.OnChangeListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private Dialog saveDialog;
	
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
		label.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				controller.handleMessage(TapController.MESSAGE_UPDATE_LABEL, s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		lockedBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				controller.handleMessage(TapController.MESSAGE_UPDATE_LOCK, isChecked);
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (counterVo.getId() > 0) {
			controller.handleMessage(TapController.MESSAGE_POPULATE_MODEL_BY_ID, counterVo.getId());
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		controller.dispose();
		if (saveDialog != null && saveDialog.isShowing()) saveDialog.dismiss();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.reset:
				return controller.handleMessage(TapController.MESSAGE_RESET_COUNT);
			case R.id.save:
				return controller.handleMessage(TapController.MESSAGE_SAVE_MODEL);
			case R.id.prefs:
				startActivity(new Intent(this, PrefsActivity.class));
				return true;
			case R.id.taps:
				startActivity(new Intent(this, TapListActivity.class));
				return true;
			case R.id.new_:
				createSaveDialog();
				return true;
			case R.id.source:
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://www.therealjoshua.com/blog/"));
				startActivity(intent);
				return true;
			default: 
				return false;
			
		}
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

	private void createSaveDialog() {
		if (saveDialog != null && saveDialog.isShowing()) saveDialog.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Save current counter?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				controller.handleMessage(TapController.MESSAGE_SAVE_MODEL);
				controller.handleMessage(TapController.MESSAGE_CREATE_NEW_MODEL);
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				controller.handleMessage(TapController.MESSAGE_CREATE_NEW_MODEL);
			}
		});
		saveDialog = builder.show();
	}	
	
	@Override
	public boolean handleMessage(Message arg0) {
		return true;
	}

}
