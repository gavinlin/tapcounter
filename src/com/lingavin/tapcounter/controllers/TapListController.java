package com.lingavin.tapcounter.controllers;

import java.util.ArrayList;

import android.os.Handler;
import android.os.HandlerThread;

import com.lingavin.tapcounter.vos.CounterVo;

public class TapListController extends Controller{
	
	private HandlerThread workThread;
	private Handler workerHandler;
	
	public static final int MESSAGE_GET_COUNTERS = 1;
	public static final int MESSAGE_MODEL_UPDATED = 2;
	public static final int MESSAGE_DELETE_COUNTER = 3;
	public static final int MESSAGE_INCREMENT_COUNTER = 4;
	public static final int MESSAGE_DECREMENT_COUNTER = 5;
	
	private ArrayList<CounterVo> model;
	public ArrayList<CounterVo> getModel(){
		return model;
	}
	
	public TapListController(ArrayList<CounterVo> model){
		this.model = model;
		this.workThread = new HandlerThread("Work Thread");
		workThread.start();
		workerHandler = new Handler(workThread.getLooper());
	}

	
	
	@Override
	public void dispose() {
		super.dispose();
		workThread.getLooper().quit();
	}

	@Override
	boolean handleMessage(int what, Object data) {
		switch(what){
		case MESSAGE_GET_COUNTERS:
			getCounters();
			return true;
        case MESSAGE_DELETE_COUNTER:
            deleteCounter((Integer)data);
            getCounters();
            return true;
        case MESSAGE_INCREMENT_COUNTER:
            changeCount(1, (CounterVo)data);
            getCounters();
            return true;
        case MESSAGE_DECREMENT_COUNTER:
            changeCount(-1, (CounterVo)data);
            getCounters();
            return true;
			
		}
		return false;
	}
	
	private void changeCount(final int amount, final CounterVo counter){
		workerHandler.post(new Runnable(){

			@Override
			public void run() {
				synchronized(counter){
					counter.setCount(counter.getCount() + amount);
					//do Persistence
				}
			}
			
		});
	}
	
	private void getCounters(){
		workerHandler.post(new Runnable(){

			@Override
			public void run() {
				synchronized(model){
					
					notifyOtuboxHandlers(MESSAGE_MODEL_UPDATED, 0, 0, null);
				}
			}
			
		});
	}
	
	private void deleteCounter(final int itemId) {
		workerHandler.post(new Runnable() {
			@Override
			public void run() {

			}
		});
	}
}
