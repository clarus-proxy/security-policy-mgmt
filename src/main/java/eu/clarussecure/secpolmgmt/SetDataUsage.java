package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.types.Usage;
import eu.clarussecure.datamodel.Policy;

import java.util.Arrays;

public class SetDataUsage extends Command {
    //TODO
    private int policyID;
    private Usage dataUsage;

    public SetDataUsage(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Policy policy) throws CommandExecutionException {
        // Verify the given policy ID with the one in the file
        if (this.policyID != policy.getPolicyID()) {
            throw new CommandExecutionException("The given policy ID " + this.policyID
                    + " does not correspond with the policy ID in the file (" + policy.getPolicyID() + ").");
        }

        // Second, set the data usage
        policy.setDataUsage(this.dataUsage);

        CommandReturn cr = new CommandReturn(0,
                "The usage for policy ID " + policy.getPolicyID() + " was correctly set.", policy);
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("set_data_usage"))
            throw new CommandParserException(
                    "Why a non-'set_data_usage' command ended up in the 'set_data_usage' part of the parser?");

        // Second, check that the policyID is present and well-formed
        try {
            this.policyID = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandParserException(
                    "There was an error identifying the policyID. Is the port number well formed?");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'policyID' was not given and it is required.");
        }

        // Third, check the attribute type
        try {
            this.dataUsage = Usage.fromString(args[2].toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new CommandParserException(
                    "There was an error idenfitying the Attribute Type. Is the Type '" + args[2]
                            + "' supported?\nThe list of supported protocols is:\n" + Arrays.toString(Usage.values()));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'dataUsage' was not given and it is required.");
        }
        return true;
    }
}
