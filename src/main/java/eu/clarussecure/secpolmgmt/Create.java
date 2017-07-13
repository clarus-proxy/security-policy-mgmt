package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.secpolmgmt.dao.CLARUSPolicyDAO;

public class Create extends Command {

    private String newPolicyname;

    public Create(String[] args) throws CommandParserException {
        this.parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Policy policy) throws CommandExecutionException {
        // Compute the new Policy ID
        // FIXME !!!
        // At the moment this is the LARGEST id of the set (i.e., the ones in the file) + 1.

        CLARUSPolicyDAO dao = CLARUSPolicyDAO.getInstance();
        int id = dao.getLastPolicyID() + 1;
        dao.deleteInstance();

        // Create new Policy object and add it to the policies set
        Policy pol = new Policy(id, this.newPolicyname);

        // Prepare the return value
        return new CommandReturn(0, "The ID of the policy created is " + id, pol);
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First: Sanity Check
        if (!args[0].toLowerCase().equals("create"))
            throw new CommandParserException("Why a non-'create' command ended up in the 'create' part of the parser?");

        // Second, check that the name of the policy has been given
        if (args.length < 2)
            throw new CommandParserException(
                    "Create command requires a Policy Name\nUsage clarus-spm create <policy_name>\nPlease note that names containing spaces MUST be quoted");

        // Extract the new policy name from the command line
        this.newPolicyname = args[1];

        return true;
    }
}
