package com.google.gwt.sample.stockwatcher.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;


public class Stockwatcher implements EntryPoint {

	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newCurrencyTextBox = new TextBox();
	private TextBox newPriceTextBox = new TextBox();
	private TextBox newChangeTextBox = new TextBox();
	private Button addStockButton = new Button("Add");
	private HorizontalPanel refreshPanel = new HorizontalPanel();
	private Button refreshButton = new Button("Refresh");
	private Label lastUpdatedLabel = new Label();
	private ArrayList<String> stocks = new ArrayList<>();
	private Label errorMsgLabel = new Label();
	
	private final CurrencyServiceAsync currencyService = (CurrencyServiceAsync) GWT.create(CurrencyService.class);
	  
	/**
	 * Entry point method.
	*/
  	public void onModuleLoad() {
  		initTable();
	    	initTableData();
  	}

	/**
	 * init static html table
	*/
  	private void initTable() {
  		// Create table for stock data.
		stocksFlexTable.setText(0, 0, "Currency");
		stocksFlexTable.setText(0, 1, "Price in HKD");
		stocksFlexTable.setText(0, 2, "Change in % over the past month");
		stocksFlexTable.setText(0, 3, "Remove");
	    
		stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		stocksFlexTable.addStyleName("watchList");
		stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");
		stocksFlexTable.setCellPadding(6);
		// Assemble Add Stock panel.
		newCurrencyTextBox.getElement().setPropertyString("placeholder", "Currency");
		newPriceTextBox.getElement().setPropertyString("placeholder", "Price in HKD");
		newChangeTextBox.getElement().setPropertyString("placeholder", "Change over the last month");
	    
		addPanel.add(newCurrencyTextBox);
		addPanel.add(newPriceTextBox);
		addPanel.add(newChangeTextBox);
		addPanel.add(addStockButton);
		addPanel.addStyleName("addPanel");
	    
		refreshPanel.add(refreshButton);
	    
		errorMsgLabel.setStyleName("errorMessage");
		errorMsgLabel.setVisible(false);
	
		// Assemble Main panel.
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(refreshPanel);
		mainPanel.add(lastUpdatedLabel);
		mainPanel.add(errorMsgLabel);
	
		// Associate the Main panel with the HTML host page.
		RootPanel.get("stockList").add(mainPanel);
	
		// Move cursor focus to the input box.
		newCurrencyTextBox.setFocus(true);
		
		// Listen for mouse events on the Add button.
		addStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addCurrency();
			}
		});
	    
		refreshButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				updateTableData();
			}
		});
  	}

	/**
	 * Add currency to main table. Executed when the user clicks the addStockButton
	*/
  	private void addCurrency() {
  		final String currency = newCurrencyTextBox.getText().toUpperCase().trim();
  		// currency must be between 1 and 10 chars that are letters.
  		if (!currency.matches("^[A-Z]{1,10}$")) {
  			Window.alert("'" + currency + "' is not a valid symbol. Currency must be between 1 and 10 chars that are letters.");
  			newCurrencyTextBox.selectAll();
  			return;
  		}
  		final String price = newPriceTextBox.getText().toUpperCase().trim();
  		// Price must contain dot and only numbers.
  		if (!price.matches("\\d*\\.\\d*")) {
  			Window.alert("'" + price + "' is not a valid price. Price must contain dot and only numbers.");
  			newCurrencyTextBox.selectAll();
  			return;
  		}
  		final String change = newChangeTextBox.getText().toUpperCase().trim();
  		// change must contain dot and only numbers.
		if (!change.matches("\\d*\\.\\d*")) {
			Window.alert("'" + change + "' is not a valid percentage change. Change must contain dot and only numbers.");
		    	newCurrencyTextBox.selectAll();
		    	return;
		}
	
		if (stocks.contains(currency)) {
		   	Window.alert("'" + currency + "' is already in the list.");
		    	newCurrencyTextBox.selectAll();
		    	return;
		}
		int row = stocksFlexTable.getRowCount();
		stocks.add(currency);
		stocksFlexTable.setText(row, 0, currency);
		stocksFlexTable.setText(row, 1, price);
		stocksFlexTable.setText(row, 2, change);
		stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
		stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");
	  
		Button removeStockButton = new Button("x");
		removeStockButton.addStyleDependentName("remove");
		removeStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
		    		int removedIndex = stocks.indexOf(currency);
		    		stocks.remove(removedIndex);
		    		stocksFlexTable.removeRow(removedIndex + 1);
		    	}
		});
		stocksFlexTable.setWidget(row, 3, removeStockButton);
	  
		updateTableData();
    	}
  
  	/**
  	 * Call historic data to prefill the table
  	 */
  	private void initTableData() {
  		
    		AsyncCallback<String> callback = new AsyncCallback<String>() {
	    	  	public void onFailure(Throwable caught) {
	    	    		displayError("Couldn't retrieve JSON");
	    	    	}
	
		  	@Override
		  	public void onSuccess(String result) {
		  		if (!result.isEmpty()) {
		  			for (Entry<String, Double> entry : getObjectData(result).entrySet()) {
						addRow(entry.getKey(),entry.getValue());
		  			}
					updateTableData();
				} else {
					displayError("Couldn't retrieve JSON");
			  	}
					
				DateTimeFormat dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
		  	        lastUpdatedLabel.setText("Last update : " + dateFormat.format(new Date()));
			}
    		};
    		currencyService.getData("HISTORICAL_CURRENCY", callback);
  	}
  	
  	private void updateTableData() {
  		
  		AsyncCallback<String> callback = new AsyncCallback<String>() {
  	    		public void onFailure(Throwable caught) {
  	    		displayError("Couldn't retrieve JSON while updating");
  	    	}

		@Override
		public void onSuccess(String result) {
			if (!result.isEmpty()) {
				for (Entry<String, Double> entry : getObjectData(result).entrySet()) {
	  				updateRow(entry.getKey(),entry.getValue());
	  			}
				errorMsgLabel.setVisible(false);
			} else {
	  	        	displayError("Couldn't retrieve JSON while updating");
	  	      	}
			
			DateTimeFormat dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
  	        	lastUpdatedLabel.setText("Last update : " + dateFormat.format(new Date()));
		}
  	  };
  	  currencyService.getData("PRESENT_CURRENCY", callback);
  	}

  	
    	private void updateRow(String currency, Double newRate) {
		int rowNbr = stocks.indexOf(currency) + 1;
		if (rowNbr > 0) {
			double oldRate = Double.parseDouble(stocksFlexTable.getText(rowNbr, 1));
			double change = 100.0 * ( (1/newRate - oldRate) / oldRate);
			
			if (Double.compare(oldRate, 1/newRate) > 0 || Double.compare(change, 0.0f) > 0) {
				stocksFlexTable.setText(rowNbr, 1, String.valueOf(1/newRate));
				stocksFlexTable.setText(rowNbr, 2, String.valueOf(change));
			}
		}
	}
    
    	private void displayError(String error) {
    		errorMsgLabel.setText("Error: " + error);
        	errorMsgLabel.setVisible(true);
    	}
    
	private void addRow(final String currency, double rate) {
		int row = stocksFlexTable.getRowCount();
	    	stocks.add(currency);
	    	stocksFlexTable.setText(row, 0, currency);
	    	stocksFlexTable.setText(row, 1, String.valueOf(1/rate));
	    	stocksFlexTable.setWidget(row, 2, new Label());
	    	stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
	    	stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
	    	stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");
	      
	    	Button removeStockButton = new Button("x");
	    	removeStockButton.addStyleDependentName("remove");
	    	removeStockButton.addClickHandler(new ClickHandler() {
	    		public void onClick(ClickEvent event) {
	          		int removedIndex = stocks.indexOf(currency);
	          		stocks.remove(removedIndex);
	          		stocksFlexTable.removeRow(removedIndex + 1);
	        	}
	      	});
	    	stocksFlexTable.setWidget(row, 3, removeStockButton);
	}
	
	
	private Map<String, Double> getObjectData(String response) {
		DataFactory dataFactory = GWT.create(DataFactory.class);
    		Data data = AutoBeanCodex.decode(dataFactory, Data.class, response).as();
    		return data.getRates();
	}

	public interface Data extends Serializable {
    		public String getBase();
    		public String getDate();
	    	public Map<String,Double> getRates();
	}
	
	public interface DataFactory extends AutoBeanFactory, Serializable {
		AutoBean<Data> getData();
	}
}
