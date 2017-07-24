package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.types.AttrType;
import eu.clarussecure.datamodel.types.Module;
import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.ProtectionAttributeType;
import eu.clarussecure.datamodel.ProtectionAttributeParameter;

import java.util.Arrays;

public class SetProtectionParam extends Command {

    private int policyID;
    private AttrType attributeType;
    private String protectionName;
    private String protectionParam = null;
    private double protectionValue;

    public SetProtectionParam(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Policy policy) throws CommandExecutionException {
        // FIXME - This commands adds a new entry to the attribute types when trying to assign a new param to an existent attribute Type
        // Implement either a "search" before attaching OR a new command to add a parameter to the ProtectionAttributeType
        // Verify the given policy ID with the one in the file
        if (this.policyID != policy.getPolicyId()) {
            throw new CommandExecutionException("The given policy ID " + this.policyID
                    + " does not correspond with the policy ID in the file (" + policy.getPolicyId() + ").");
        }
        // Second, create the Required objects to be set on the policy's Protection
        ProtectionAttributeType pa;
        if (this.protectionParam != null) {
            // If the Attribute takes a parameter, create the object ProtectionParam and ProtectionAtributeParameter objects
            ProtectionAttributeParameter protParam = new ProtectionAttributeParameter(this.protectionParam,
                    this.protectionValue);
            // Create the ProtectionAttributeType object
            pa = new ProtectionAttributeType(this.protectionName, this.attributeType, protParam);
        } else {
            // Create the ProtectionAttributeType object
            pa = new ProtectionAttributeType(this.protectionName, this.attributeType);
        }

        // Attach the ProtectionAtttribute to the Protection
        policy.getProtection().addProtectionAttribute(pa);

        CommandReturn cr = new CommandReturn(0, "The Policy ID " + policy.getPolicyId() + " was updated sucessfully",
                policy);
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("set_protection_param")) {
            throw new CommandParserException(
                    "Why a non-'set_protection_param' command ended up in the 'set_protection_param' part of the parser?");
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

        // Third, that the attribute type is valid
        try {
            this.attributeType = AttrType.fromString(args[2].toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new CommandParserException(
                    "There was an error idenfitying the Attribute Type. Is the Type '" + args[2]
                            + "' supported?\nThe list of supported modules is:\n" + Arrays.toString(AttrType.values()));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'attributeType' was not given and it is required.");
        }

        // Fourth, identify the Protection Name
        Module mod = null;
        try {
            this.protectionName = args[3];
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'protectionName' was not given and it is required.");
        }

        // Optional part of the command
        // Fifth, get the parameter of the Protection, if any
        try {
            this.protectionParam = args[4];
        } catch (IndexOutOfBoundsException e) {
            //throw new CommandParserException("The field 'protectionParam' was not given and it is required.");
        }

        // Lastly, Store the value of the the parameter
        try {
            this.protectionValue = Double.parseDouble(args[5]);
        } catch (NumberFormatException e) {
            throw new CommandParserException(
                    "There was an error identifying the Parameter Value. Is the Value well formed?");
        } catch (IndexOutOfBoundsException e) {
            //throw new CommandParserException("The field 'protectionValue' was not given and it is required.");
        }

        return true;
    }
}
