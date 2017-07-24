package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.secpolmgmt.dao.CLARUSPolicyDAO;

public class Register extends Command {

    private int policyID;

    public Register(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Policy policy) throws CommandExecutionException {
        this.verifyRights("admin");
        // At this point, the user SHOULD be identified and authorized to execute this command
        // Verify the given policy ID with the one in the file
        if (this.policyID != policy.getPolicyId()) {
            throw new CommandExecutionException("The given policy ID " + this.policyID
                    + " does not correspond with the policy ID in the file (" + policy.getPolicyId() + ").");
        }

        if (policy == null) {
            throw new CommandExecutionException(
                    "The policy with ID" + this.policyID + " could not be read from the file!");
        }

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

        CommandReturn cr = new CommandReturn(0, "The policy ID " + policy.getPolicyId() + " was correctly registered.",
                null);
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("register")) {
            throw new CommandParserException(
                    "Why a non-'register' command ended up in the 'register' part of the parser?");
        }

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
