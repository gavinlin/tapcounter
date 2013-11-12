package com.lingavin.tapcounter.vos;

public interface EasyObservable<T> {
	void addListener(OnChangeListener<T> listener);
	void removeListener(OnChangeListener<T> listener);
}
