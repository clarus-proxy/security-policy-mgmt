/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.types.AttrType;
import eu.clarussecure.datamodel.types.ProtectionName;
import eu.clarussecure.datamodel.types.ProtectionParam;
import eu.clarussecure.datamodel.types.Module;
import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.ProtectionAttributeType;
import eu.clarussecure.datamodel.ProtectionAttributeParameter;

import java.util.Set;
import java.util.Arrays;

/**
 *
 * @author diegorivera
 */
public class SetProtectionParam implements Command{
	private int policyID;
	private AttrType attributeType;
	private ProtectionName protectionName;
	private ProtectionParam protectionParam;
	private double protectionValue;
	
	public SetProtectionParam(String[] args) throws CommandParserException{
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

		// Second, create the ProtectionAttribute object
		// If the Attribute takes a parameter, create the object, otherwise the AttrParam object is not necesary
		ProtectionAttributeParameter ap = this.protectionName.getsParameter() ? new ProtectionAttributeParameter(this.protectionParam, this.protectionValue) : null;
		ProtectionAttributeType pa = new ProtectionAttributeType(this.protectionName, this.attributeType, ap);

		// Attach the ProtectionAtttribute to the Protection
		policy.getProtection().addProtectionAttribute(pa);

		CommandReturn cr = new CommandReturn(0, "The Policy ID " + policy.getPolicyID() + " was updated sucessfully");
		return cr;
	}

	public boolean parseCommandArgs(String[] args) throws CommandParserException{
		// First, sanity check
		if (!args[0].toLowerCase().equals("set_protection_param"))
			throw new CommandParserException("Why a non-'set_protection_param' command ended up in the 'set_protection_param' part of the parser?");

		// Second, check that the policyID is present and well-formed
		try{
			this.policyID = Integer.parseInt(args[1]);
		} catch (NumberFormatException e){
			throw new CommandParserException("There was an error identifying the policyID. Is the port number well formed?");
		} catch (IndexOutOfBoundsException e){
			throw new CommandParserException("The field 'policyID' was not given and it is required.");
		}

		// Third, that the attribute type is valid
		try{
			this.attributeType = AttrType.fromString(args[2].toLowerCase());
		} catch (IllegalArgumentException e){
			throw new CommandParserException("There was an error idenfitying the Attribute Type. Is the Type '" + args[2] + "' supported?\nThe list of supported modules is:\n" + Arrays.toString(AttrType.values()));
		} catch (IndexOutOfBoundsException e){
			throw new CommandParserException("The field 'attributeType' was not given and it is required.");
		}

		// Fourth, identify the Protection Name and wheter if it is valid or not for the CLARUS Module of the Policy
		Module mod = null;
		try{
			mod = Main.findPolicy(this.policyID).getProtection().getModule();
			this.protectionName = ProtectionName.fromString(args[3], mod);
		} catch (IllegalArgumentException e){
			throw new CommandParserException("There was an error idenfitying the Protection Name. Is the Name '" + args[3] + "' supported for the Protection Module " + mod + "?\nThe list of supported modules is:\n" + Arrays.toString(ProtectionName.values(mod)));
		} catch (IndexOutOfBoundsException e){
			throw new CommandParserException("The field 'protectionName' was not given and it is required.");
		}

		// There are some Protection names that DO NOT take parameters
		if(this.protectionName.getsParameter()){

			// Fifth, check if the given parameter is valid for the Protection name
			try{
				this.protectionParam = ProtectionParam.fromString(args[4], this.protectionName);
			} catch (IllegalArgumentException e){
				throw new CommandParserException("There was an error idenfitying the Protection Parameter. Is the Parameter '" + args[4] + "' supported for the Protection Name " + this.protectionName + "?\nThe list of supported modules is:\n" + Arrays.toString(ProtectionParam.values(this.protectionName)));
			} catch (IndexOutOfBoundsException e){
				throw new CommandParserException("The field 'protectionParam' was not given and it is required.");
			}

			// Lastly, Store the value of the the Param
			try{
				this.protectionValue = Double.parseDouble(args[5]);
			} catch (NumberFormatException e){
				throw new CommandParserException("There was an error identifying the Parameter Value. Is the Value well formed?");
			} catch (IndexOutOfBoundsException e){
				throw new CommandParserException("The field 'protectionValue' was not given and it is required.");
			}
		}

		return true;
	}
}
