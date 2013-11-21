package com.lingavin.tapcounter.controllers;

import android.os.Handler;
import android.os.HandlerThread;

import com.lingavin.tapcounter.daos.CounterDao;
import com.lingavin.tapcounter.vos.CounterVo;

public class TapState implements ControllerState{

	private HandlerThread workerThread;
	
	protected TapController controller;
	protected CounterVo model;
	
	private Handler workerHandler;
	protected Handler getWorkerHandler(){
		return workerHandler;
	}
	
	public TapState(TapController controller){
		this.controller = controller;
		this.model = controller.getModel();
		workerThread = new HandlerThread("Unlocked Save Thread");
		workerThread.start();
		workerHandler = new Handler(workerThread.getLooper());
	}
	
	@Override
	public boolean handleMessage(int what) {
		return handleMessage(what, null);
	}

	@Override
	public boolean handleMessage(int what, Object data) {
		switch(what){
		case TapController.MESSAGE_SAVE_MODEL:
			saveModel();
			return true;
		case TapController.MESSAGE_POPULATE_MODEL_BY_ID:
			populateModel((Integer)data);
			return true;
		case TapController.MESSAGE_CREATE_NEW_MODEL:
			createNewModel();
			return true;
		default:
				return false;
		}
	}

	@Override
	public void disopse() {
		workerThread.getLooper().quit();
	}

	private void saveModel(){
		workerHandler.post(new Runnable() {
			
			@Override
			public void run() {
				synchronized(model){
					CounterDao dao = new CounterDao();
					if(model.getId() > 0){
						int effected = dao.update(model);
						
						if(effected < 1){
							long id = dao.insert(model);
							model.setId((int)id);
						}
					}else {
						long id = dao.insert(model);
						model.setId((int)id);
					}
					controller.notifyOutboxHandlers(TapController.MESSAGE_SAVE_COMPLETE, 0, 0 , null);
				}
			}
		});
	}
	
	private void populateModel(final int id){
		if(id < 0) return;
		workerHandler.post(new Runnable() {
			
			@Override
			public void run() {
				synchronized (model) {
					CounterDao dao = new CounterDao();
					CounterVo vo = dao.get(id);
					if(vo == null) vo = new CounterVo();
					model.consume(vo);
				}
			}
		});
	}
	
	private void createNewModel(){
		CounterVo vo = new CounterVo();
		model.consume(vo);
	}
	
}
