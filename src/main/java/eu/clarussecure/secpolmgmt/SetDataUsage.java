/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.types.Usage;
import eu.clarussecure.datamodel.Policy;

import java.util.Set;
import java.util.Arrays;

/**
 *
 * @author diegorivera
 */
public class SetDataUsage implements Command{
	//TODO
	private int policyID;
	private Usage dataUsage;

	public SetDataUsage(String[] args) throws CommandParserException{
		parseCommandArgs(args);
	}

	public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException{
		// First, find the policy
		Policy policy = null;

		for (Policy p : policies)
			if (p.getPolicyID() == this.policyID)
				policy = p;

		if (policy == null)
			throw new CommandExecutionException("The policy with ID " + this.policyID + " could not be found!");

		// Second, set the data usage
		policy.setDataUsage(this.dataUsage);

		CommandReturn cr = new CommandReturn(0, "The usage for policy ID " + policy.getPolicyID() + " was correctly set.");
		return cr;
	}

	public boolean parseCommandArgs(String[] args) throws CommandParserException{
		// First, sanity check
		if (!args[0].toLowerCase().equals("set_data_usage"))
			throw new CommandParserException("Why a non-'set_data_usage' command ended up in the 'set_data_usage' part of the parser?");

		// Second, check that the policyID is present and well-formed
		try{
			this.policyID = Integer.parseInt(args[1]);
		} catch (NumberFormatException e){
			throw new CommandParserException("There was an error identifying the policyID. Is the port number well formed?");
		} catch (IndexOutOfBoundsException e){
			throw new CommandParserException("The field 'policyID' was not given and it is required.");
		}

		// Third, check the attribute type
		try{
			this.dataUsage = Usage.fromString(args[2].toLowerCase());
		} catch (IllegalArgumentException e){
			throw new CommandParserException("There was an error idenfitying the Attribute Type. Is the Type '" + args[2] + "' supported?\nThe list of supported protocols is:\n" + Arrays.toString(Usage.values()));
		} catch (IndexOutOfBoundsException e){
			throw new CommandParserException("The field 'dataUsage' was not given and it is required.");
		}
		return true;
	}
}
