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
public interface Command{
	public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException;

	public boolean parseCommandArgs(String[] args) throws CommandParserException;
}
