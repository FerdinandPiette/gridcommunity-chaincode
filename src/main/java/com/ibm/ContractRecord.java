package com.ibm;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ContractRecord {

	public String clientName;
	// public int temperatureThreshold;
	// public int amountReceivedWhenContractIsActivated;
	public int totalAmount;

	public ContractRecord() {

	}

	public ContractRecord(String clientName) {
		this.clientName = clientName;
		// this.temperatureThreshold = temperatureThreshold;
		// this.amountReceivedWhenContractIsActivated =
		// amountReceivedWhenContractIsActivated;
		this.totalAmount = 0;
	}

	public ContractRecord(String clientName, int totalAmountReceived) {
		this.clientName = clientName;
		// this.temperatureThreshold = temperatureThreshold;
		// this.amountReceivedWhenContractIsActivated =
		// amountReceivedWhenContractIsActivated;
		this.totalAmount = totalAmountReceived;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
