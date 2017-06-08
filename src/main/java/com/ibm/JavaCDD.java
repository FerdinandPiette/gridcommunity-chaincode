
package com.ibm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.shim.ChaincodeBase;
import org.hyperledger.fabric.sdk.shim.ChaincodeStub;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Thomas 
 *
 */
public class JavaCDD extends ChaincodeBase {

	private static Log log = LogFactory.getLog(JavaCDD.class);

	@Override
	/**
	 * Entry point of invocation interaction
	 * 
	 * @param stub
	 * @param function
	 * @param args
	 */
	public String run(ChaincodeStub stub, String function, String[] args) {

		log.info("Calling invocation chaincode with function :" + function + " and args :"
				+ org.apache.commons.lang3.StringUtils.join(args, ","));
		String re = null;
		switch (function) {
		case "init":
			init(stub, function, args);
			break;
		case "add":
			re = operate(stub, "add", args);
			log.info("Return of add : " + re);
			return re;
		case "redeem":
			re = operate(stub, "redeem", args);
			log.info("Return of redeem : " + re);
			return re;
		case "view":
			return operate(stub, "view", args);
		default:
			String warnMessage = "{\"Error\":\"Error function " + function + " not found\"}";
			log.warn(warnMessage);
			return warnMessage;
		}

		return null;
	}

	public String operate(ChaincodeStub stub, String function, String[] args) {

		if (function.equalsIgnoreCase("add") || function.equalsIgnoreCase("redeem")) {
			if (args.length != 2) {
				String errorMessage = "{\"Error\":\"Incorrect number of arguments. Expecting 2: client name, amount\"}";
				log.error(errorMessage);
				return errorMessage;
			}
		} else {
			// view
			if (args.length != 1) {
				String errorMessage = "{\"Error\":\"Incorrect number of arguments. Expecting 1: client name\"}";
				log.error(errorMessage);
				return errorMessage;
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		ContractRecord contractRecord;
		try {
			contractRecord = mapper.readValue(stub.getState(args[0]), ContractRecord.class);
		} catch (Exception e1) {

			String errorMessage = "{\"Error\":\" Problem retrieving state of client contract : " + e1.getMessage()
					+ "  \"}";
			log.error(errorMessage);
			return errorMessage;
		}
		switch (function) {
		case "add":
			contractRecord.totalAmount += Integer.parseInt(args[1]);
			break;
		case "redeem":
			contractRecord.totalAmount -= Integer.parseInt(args[1]);
			break;
		case "view":
			return contractRecord.toString();
		}

		stub.putState(contractRecord.clientName, contractRecord.toString());

		return contractRecord.toString();
	}

	/**
	 * This function initializes the contract
	 * 
	 * @param stub
	 * @param function
	 * @param args
	 *            client name, temperature threshold, amount received when
	 *            contract is activated
	 * @return
	 */
	public String init(ChaincodeStub stub, String function, String[] args) {

		if (args.length != 3) {
			return "{\"Error\":\"Incorrect number of arguments. Expecting 3 : client name, temperature threshold, amount received when contract is activated \"}";
		}
		try {
			ContractRecord contractRecord = new ContractRecord(args[0],
					args[1] == null ? 0 : Integer.parseInt(args[1]));
			stub.putState(args[0], contractRecord.toString());
		} catch (NumberFormatException e) {
			return "{\"Error\":\"Expecting integer value for temperature threshold and amount received\"}";
		}
		return null;
	}

	/**
	 * This function can query the current State of the contract
	 * 
	 * @param stub
	 * @param function
	 * @param args
	 *            client name
	 * @return total amount received for this client
	 */
	@Override
	public String query(ChaincodeStub stub, String function, String[] args) {

		log.info("Calling query chaincode with function :" + function + " and args :"
				+ org.apache.commons.lang3.StringUtils.join(args, ","));

		if (args.length != 1) {
			return "{\"Error\":\"Incorrect number of arguments. Expecting name of the client to query\"}";
		}
		String clientName = stub.getState(args[0]);

		if (clientName != null && !clientName.isEmpty()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				ContractRecord contractRecord = mapper.readValue(clientName, ContractRecord.class);
				return "" + contractRecord.totalAmount;
			} catch (Exception e) {
				return "{\"Error\":\"Failed to parse state for client " + args[0] + " : " + e.getMessage() + "\"}";
			}
		} else {
			return "{\"Error\":\"Failed to get state for client " + args[0] + "\"}";
		}

	}

	@Override
	/**
	 * Just a easiest way to retrieve a contract by its name
	 */
	public String getChaincodeID() {
		return "JavaCDD";
	}

	public static void main(String[] args) throws Exception {
		new JavaCDD().start(args);
	}

}
