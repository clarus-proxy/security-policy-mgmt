/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.clarussecure.secpolmgmt;

import eu.clarussecure.secpolmgmt.dao.CLARUSPolicyDAO;

import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.PolicyAttribute;
import eu.clarussecure.datamodel.ProtectionAttributeType;

import java.util.Set;

/**
 *
 * @author diegorivera
 */
public class List implements Command{
	private String loginID = "";
	private String password = "";
	private String identityFilePath = "";
	
	public List(String[] args) throws CommandParserException{
		parseCommandArgs(args);
	}

	public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException{
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

		// FIXME - Review the implementation of this command
		// NOTE: If a path for the identiy file is present, it will be used to identify the user even if a password was provided.
		// Too many doubts about what "registering" a policy means...
		// Do they need to comunicate the configuration? To whom? Afterwards, does the policy need to be deleted from the file?

		CLARUSPolicyDAO dao = CLARUSPolicyDAO.getInstance();
		Set<Policy> pols = dao.getPolicies();
		dao.deleteInstance();

		List.printPolicies(pols);

		CommandReturn cr = new CommandReturn(0, "");
		return cr;
	}

	public boolean parseCommandArgs(String[] args) throws CommandParserException{
		// First, sanity check
		if (!args[0].toLowerCase().equals("list"))
			throw new CommandParserException("Why a non-'list' command ended up in the 'list' part of the parser?");

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

	static public void printPolicies(Set<Policy> policies) throws UnsupportedOperationException{
		for(Policy p : policies){
			System.out.println("ID = " + p.getPolicyID() + ", name = " + p.getPolicyName());

			System.out.println("Endpoint = " + p.getEndpoint().getProtocol() + ", port = " + p.getEndpoint().getPort() + ", baseURL = " + p.getEndpoint().getBaseUrl());

			for(PolicyAttribute a : p.getAttributes())
				System.out.println("\tattribute: path = " + a.getPath() + ", type = " + a.getAttributeType() + ", datatype = " + a.getDataType());

			System.out.println("\tModule = " + p.getProtection().getModule());

			for (ProtectionAttributeType pa : p.getProtection().getAttributeTypes()){
				System.out.println("\t\tProtection = " + pa.getProtection() + ", type = " + pa.getType());

				if(pa.getParameter() != null){
					System.out.println("\t\t\tParamName = " + pa.getParameter().getParam() + ", ParamValue = " + pa.getParameter().getValue());
				} else {
					System.out.println("\t\t\t----");
				}
			}
		}
	}
}
