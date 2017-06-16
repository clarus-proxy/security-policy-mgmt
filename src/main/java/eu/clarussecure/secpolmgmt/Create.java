/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;

import java.util.Set;

/**
 *
 * @author diegorivera
 */
public class Create implements Command{

	private String newPolicyname;

	public Create(String[] args) throws CommandParserException{
		this.parseCommandArgs(args);
	}

	public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException{
		// Compute the new Policy ID
		// FIXME !!!
		// At the moment this is the LARGEST id of the set + 1.

		int id = -1;

		for(Policy p : policies)
			id = p.getPolicyID()>id ? p.getPolicyID() : id;
		id++;

		// Create new Policy object and add it to the policies set
		Policy pol = new Policy(id, this.newPolicyname);

		policies.add(pol);

		// Prepare the return value
		return new CommandReturn(0, "The ID of the policy created is " + id);
	}


	public boolean parseCommandArgs(String[] args) throws CommandParserException{
		// First: Sanity Check
		if (!args[0].toLowerCase().equals("create"))
			throw new CommandParserException("Why a non-'create' command ended up in the 'create' part of the parser?");

		// Second, check that the name of the policy has been given
		if (args.length < 2)
			throw new CommandParserException("Create command requires a Policy Name\nUsage clarus-spm create <policy_name>\nPlease note that names containing spaces MUST be quoted");

		// Extract the new policy name from the command line
		this.newPolicyname = args[1];

		return true;
	}
}
