/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.clarussecure.secpolmgmt;

import eu.clarussecure.secpolmgmt.dao.CLARUSPolicyDAO;

import eu.clarussecure.datamodel.Policy;

import java.util.Set;

/**
 *
 * @author diegorivera
 */
public class Delete implements Command{
	private int policyID;
	private String loginID = "";
	private String password = "";
	private String identityFilePath = "";
	
	public Delete(String[] args) throws CommandParserException{
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

		// TODO - Check if the policy has been registered or not. If not, simply delete the policy from the file (?)

		// Check if there's any missing information
		while(this.loginID.equals("")){
			// Ask the user for the loginID
			System.out.print("loginID?");
			this.loginID = System.console().readLine();
		}

		// The next conditions implies that blank passwords are not allowed!!!
		if(this.password.equals("") && this.identityFilePath.equals("")){
			// Ask the user for the password
			System.out.print("Password?");
			this.password = new String(System.console().readPassword());
		}

		// Pathological case: no password provided. Ask for the path of the identity file
		while(this.password.equals("") && this.identityFilePath.equals("")){
			System.out.print("Identity File path:");
			this.identityFilePath = System.console().readLine();
		}

		// System.out.println("LoginID = '" + this.loginID + "'");
		// System.out.println("Password = '" + this.password + "'");
		// System.out.println("IdentityFilePath = '" + this.identityFilePath + "'");

		// FIXME - Validate the behavior of this command
		// NOTE: If a path for the identiy file is present, it will be used to identify the user even if a password was provided.
		// Too many doubts about what "registering" a policy means...
		// Do they need to comunicate the configuration? To whom? Afterwards, does the policy need to be deleted from the file?

		CLARUSPolicyDAO dao = CLARUSPolicyDAO.getInstance();
		dao.removePolicy(policy);
		dao.deleteInstance();

		CommandReturn cr = new CommandReturn(0, "The policy ID " + policy.getPolicyID() + " was successfully deleted.");
		return cr;
	}

	public boolean parseCommandArgs(String[] args) throws CommandParserException{
		// First, sanity check
		if (!args[0].toLowerCase().equals("delete"))
			throw new CommandParserException("Why a non-'delete' command ended up in the 'delete' part of the parser?");

		// Second, check that the policyID is present and well-formed
		try{
			this.policyID = Integer.parseInt(args[1]);
		} catch (NumberFormatException e){
			throw new CommandParserException("There was an error identifying the policyID. Is the port number well formed?");
		} catch (IndexOutOfBoundsException e){
			throw new CommandParserException("The field 'policyID' was not given and it is required.");
		}

		// Parse other params of the command
		try{
			for (int i=2; i<args.length; i++){
				// Parse the options:
				switch(args[i]){
					case "-l":
					case "--login":
						// The next argument will be interpreted as the login name
						this.loginID = args[++i];
						break;
					case "-p":
					case "--password":
						// The next argument will be interpreted as the password
						this.password = args[++i];
						break;
					case "-i":
						// The next argument will be interpreted as the login name
						this.identityFilePath = args[++i];
						break;
					default:
						break;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e){
			// This Exception is not fatal. Missing information will be asked directly to the user while executing the command
		}

		return true;
	}
}
