package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.types.Protocol;
import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.Endpoint;

public class SetDataspaceEndpoint extends Command {

    private int policyID = -1;
    private int port = -1;
    private Protocol protocolName = null;

    public SetDataspaceEndpoint(String[] args) throws CommandParserException {
        this.parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Policy policy) throws CommandExecutionException {
        // Verify the given policy ID with the one in the file
        if (this.policyID != policy.getPolicyId()) {
            throw new CommandExecutionException("The given policy ID " + this.policyID
                    + " does not correspond with the policy ID in the file (" + policy.getPolicyId() + ").");
        }

        // Second, create the Endpoint object
        Endpoint e = new Endpoint(this.protocolName, this.port);

        // Third, set the Endopoint to the Policy
        policy.setEndpoint(e);

        // Finally, prepare the return
        CommandReturn cr = new CommandReturn(0, "Endpoint set succesfully. The Endpoint URL is: " + e.getEndpointURL(),
                policy);
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First: Sanity Check
        if (!args[0].toLowerCase().equals("set_dataspace_endpoint"))
            throw new CommandParserException(
                    "Why a non-'set_dataspace_endpoint' command ended up in the 'set_dataspace_endpoint' part of the parser?");

        // Second, check that the policyID is present and well-formed
        try {
            this.policyID = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandParserException(
                    "There was an error identifying the policyID. Is the port number well formed?");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'policyID' was not given and it is required.");
        }

        // Third, check that the protocol is present and it is a supported protocol
        try {
            if (!Protocol.isValidProtocol(args[2].toLowerCase())) {
                throw new IllegalArgumentException();
            }
            this.protocolName = new Protocol(args[2].toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new CommandParserException("There was an error idenfitying the protocol. Is the protocol '" + args[2]
                    + "' supported?\nThe list of supported protocols is:\n" + Protocol.getProtocolNamesAsList());
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'protocolName' was not given and it is required.");
        }

        // Parse the port number;
        try {
            this.port = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new CommandParserException(
                    "There was an error identifying the port. Is the port number well formed?");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'port' was not given and it is required.");
        }

        return true;
    }
}
