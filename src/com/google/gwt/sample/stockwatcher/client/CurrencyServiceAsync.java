package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CurrencyServiceAsync {

	void getData(String url, AsyncCallback<String> callback);

}
