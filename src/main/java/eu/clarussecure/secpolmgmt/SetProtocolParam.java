package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.ProtocolParam;

public class SetProtocolParam extends Command {
    private int policyID;
    private String paramName;
    private String paramValue;

    public SetProtocolParam(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Policy policy) throws CommandExecutionException {
        // Verify the given policy ID with the one in the file
        if (this.policyID != policy.getPolicyID()) {
            throw new CommandExecutionException("The given policy ID " + this.policyID
                    + " does not correspond with the policy ID in the file (" + policy.getPolicyID() + ").");
        }

        // Second, create the ProtocolParam object
        ProtocolParam param = new ProtocolParam(this.paramName, this.paramValue);

        // Attach the ProtocolParam to the Endpoint
        policy.getEndpoint().addParameter(param);

        CommandReturn cr = new CommandReturn(0, "The Policy ID " + policy.getPolicyID() + " was updated sucessfully",
                policy);
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("set_protocol_param"))
            throw new CommandParserException(
                    "Why a non-'set_protocol_param' command ended up in the 'set_protocol_param' part of the parser?");

        // Second, check that the policyID is present and well-formed
        try {
            this.policyID = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandParserException(
                    "There was an error identifying the policyID. Is the port number well formed?");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'policyID' was not given and it is required.");
        }

        // Third, extract the name of the parameter
        try {
            this.paramName = args[2];
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'paramName' was not given and it is required.");
        }

        // Fourth, extract the name of the parameter
        try {
            this.paramValue = args[3];
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'paramValue' was not given and it is required.");
        }

        return true;
    }
}
