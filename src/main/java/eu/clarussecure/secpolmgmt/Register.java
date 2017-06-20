package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.secpolmgmt.dao.CLARUSPolicyDAO;

import java.util.Set;

public class Register extends Command {
    private int policyID;

    public Register(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException {
        this.verifyRights("admin");
        // At this point, the user SHOULD be identified and authorized to execute this command

        // Find the policy
        Policy policy = null;

        for (Policy p : policies)
            if (p.getPolicyID() == this.policyID)
                policy = p;

        if (policy == null)
            throw new CommandExecutionException("The policy with ID " + this.policyID + " could not be found!");

        // Validate the policy;
        if (!policy.checkPolicyIntegrity()) {
            throw new CommandExecutionException("The policy with ID " + this.policyID + " seems to be incomplete!");
        }

        // FIXME - At the moment this class only stores the policy
        // NOTE: If a path for the identiy file is present, it will be used to identify the user even if a password was provided.
        // Too many doubts about what "registering" a policy means...
        // Do we need to comunicate the configuration? To whom? Afterwards, does the policy need to be deleted from the file?

        CLARUSPolicyDAO dao = CLARUSPolicyDAO.getInstance();
        dao.savePolicy(policy);
        dao.deleteInstance();

        CommandReturn cr = new CommandReturn(0, "The policy ID " + policy.getPolicyID() + " was correctly registered.");
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("register"))
            throw new CommandParserException(
                    "Why a non-'register' command ended up in the 'register' part of the parser?");

        // Second, check that the policyID is present and well-formed
        try {
            this.policyID = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandParserException(
                    "There was an error identifying the policyID. Is the port number well formed?");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'policyID' was not given and it is required.");
        }

        this.parseCredentials(args);

        return true;
    }
}
