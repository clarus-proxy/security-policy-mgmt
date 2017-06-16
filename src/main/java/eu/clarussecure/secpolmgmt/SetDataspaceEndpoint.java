/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.types.Protocol;
import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.Endpoint;

import java.util.Set;
import java.util.Arrays;

/**
 *
 * @author diegorivera
 */
public class SetDataspaceEndpoint implements Command{

	private int policyID = -1;
	private int port = -1;
	private Protocol protocolName = null;
	private String baseURL = "";

	public SetDataspaceEndpoint(String[] args) throws CommandParserException{
		this.parseCommandArgs(args);
	}

	public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException{
		// First, find the policy
		Policy policy = null;

		for (Policy p : policies)
			if (p.getPolicyID() == this.policyID)
				policy = p;

		if (policy == null)
			throw new CommandExecutionException("The policy with ID " + this.policyID + " could not be found!");

		// Second, create the Endpoint object
		Endpoint e = new Endpoint(this.protocolName, this.port, this.baseURL);

		// Third, set the Endopoint to the Policy
		policy.setEndpoint(e);

		// Finally, prepare the return
		CommandReturn cr = new CommandReturn(0, "Endpoint set succesfully. The Endpoint URL is: " + e.getEndpointURL());
		return cr;
	}

	public boolean parseCommandArgs(String[] args) throws CommandParserException{
		// First: Sanity Check
		if (!args[0].toLowerCase().equals("set_dataspace_endpoint"))
			throw new CommandParserException("Why a non-'set_dataspace_endpoint' command ended up in the 'set_dataspace_endpoint' part of the parser?");

		// Second, check that the policyID is present and well-formed
		try{
			this.policyID = Integer.parseInt(args[1]);
		} catch (NumberFormatException e){
			throw new CommandParserException("There was an error identifying the policyID. Is the port number well formed?");
		} catch (IndexOutOfBoundsException e){
			throw new CommandParserException("The field 'policyID' was not given and it is required.");
		}

		// Third, check that the protocol is present and it is a supported protocol
		try{
			this.protocolName = Protocol.fromString(args[2].toLowerCase());
		} catch (IllegalArgumentException e){
			throw new CommandParserException("There was an error idenfitying the protocol. Is the protocol '" + args[2] + "' supported?\nThe list of supported protocols is:\n" + Arrays.toString(Protocol.values()));
		} catch (IndexOutOfBoundsException e){
			throw new CommandParserException("The field 'protocolName' was not given and it is required.");
		}

		// Parse the port number;
		try{
			this.port = Integer.parseInt(args[3]);
		} catch (NumberFormatException e){
			throw new CommandParserException("There was an error identifying the port. Is the port number well formed?");
		} catch (IndexOutOfBoundsException e){
			throw new CommandParserException("The field 'port' was not given and it is required.");
		}

		try{
			this.baseURL = args[4];
		} catch (IndexOutOfBoundsException e){ } // This exception is not fatal. It means the baseURL is not provided.

		return true;
	}
}
