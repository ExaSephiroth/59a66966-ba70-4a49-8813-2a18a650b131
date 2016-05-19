package com.google.gwt.sample.stockwather.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
// not used as ENUMS seem flawed in GWT
public enum Currency implements IsSerializable{
	
	HISTORICAL_CURRENCY,
	PRESENT_CURRENCY;
	
	Currency() {}

}
