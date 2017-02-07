package com.google.gwt.sample.stockwatcher.server;

import java.io.IOException;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.sample.stockwatcher.client.CurrencyService;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CurrencyServiceImpl extends RemoteServiceServlet implements CurrencyService {

	private static final long serialVersionUID = 1L;
	private static final String JSON_BASE_URL = "http://api.fixer.io/latest?base=HKD";
	// using String as ENUM seems flawed in GWT
	// test
	// branch test

	private static final String PRESENT_CURRENCY = "PRESENT_CURRENCY";
	private static final String HISTORICAL_CURRENCY = "HISTORICAL_CURRENCY";

	@Override
	public String getData(String input) {
		switch(input) {
			case PRESENT_CURRENCY:
				return getJSONDataAsString(JSON_BASE_URL);
			case HISTORICAL_CURRENCY:
				String url = calculateRatesUrl();
				return getJSONDataAsString(url);
		}
		return "";
	}
	
	private String getJSONDataAsString(String url) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		request.addHeader("accept", "application/json");
	    	try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, "UTF-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";       
	}
	
	private String calculateRatesUrl() {
		String pattern = "yyyy-MM-dd";
		DefaultDateTimeFormatInfo info = new DefaultDateTimeFormatInfo();
		DateTimeFormat dtf = new DateTimeFormat(pattern, info) {};
		Date d = new Date();
		CalendarUtil.addMonthsToDate(d, -1);
		return "http://api.fixer.io/" + dtf.format(d) + "?base=HKD";
	}
}
