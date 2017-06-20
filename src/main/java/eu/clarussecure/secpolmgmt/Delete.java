package eu.clarussecure.secpolmgmt;

import eu.clarussecure.secpolmgmt.dao.CLARUSPolicyDAO;

import eu.clarussecure.datamodel.Policy;

import java.util.Set;

public class Delete extends Command {
    private int policyID;

    public Delete(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException {
        this.verifyRights("admin");

        // Find the policy
        Policy policy = null;

        for (Policy p : policies)
            if (p.getPolicyID() == this.policyID)
                policy = p;

        if (policy == null)
            throw new CommandExecutionException("The policy with ID " + this.policyID + " could not be found!");

        // TODO - Check if the policy has been registered or not. If not, simply delete the policy from the file (?)

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

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("delete"))
            throw new CommandParserException("Why a non-'delete' command ended up in the 'delete' part of the parser?");

        // Second, check that the policyID is present and well-formed
        try {
            this.policyID = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandParserException(
                    "There was an error identifying the policyID. Is the port number well formed?");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'policyID' was not given and it is required.");
        }

        // Parse other params of the command
        this.parseCredentials(args);

        return true;
    }
}
