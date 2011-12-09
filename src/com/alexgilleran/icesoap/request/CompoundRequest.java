package com.alexgilleran.icesoap.request;

import java.util.ArrayList;
import java.util.List;

import com.alexgilleran.icesoap.observer.ObserverRegistry;
import com.alexgilleran.icesoap.observer.SOAPObserver;


public abstract class CompoundRequest<T> implements Request<T>, SOAPObserver {
	private ObserverRegistry<T> registry = new ObserverRegistry<T>();
	private List<Request<?>> requests = new ArrayList<Request<?>>();
	private int requestCount;

	public CompoundRequest() {

	}

	public void addRequest(Request<?> request) {
		request.addListener(this);
		requests.add(request);
	}

	@Override
	public void execute() {
		requestCount = requests.size();

		if (requestCount <= 0) {
			throw new IllegalStateException(this.getClass().getSimpleName()
					+ " requires at least one request");
		}

		for (Request<?> request : requests) {
			request.execute();
		}
	}

	@Override
	public void addListener(SOAPObserver<T> listener) {
		registry.addListener(listener);
	}

	@Override
	public void removeListener(SOAPObserver<T> listener) {
		registry.removeListener(listener);
	}

	@Override
	public void onNewDaoItem(Object item) {
		requestCount--;

		if (requestCount <= 0) {
			registry.notifyListeners(getParsedObject());
		}
	}

	protected abstract T getParsedObject();

	@Override
	public void cancel() {
		for (Request<?> request : requests) {
			request.cancel();
		}
	}
}
