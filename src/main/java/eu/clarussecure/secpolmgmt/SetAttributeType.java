package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.types.AttrType;
import eu.clarussecure.datamodel.types.DataType;
import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.PolicyAttribute;

import java.util.Arrays;

public class SetAttributeType extends Command {
    private int policyID;
    private String attributePath;
    private AttrType attributeType;
    private DataType dataType;

    public SetAttributeType(String[] args) throws CommandParserException {
        this.parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Policy policy) throws CommandExecutionException {
        // Verify the given policy ID with the one in the file
        if (this.policyID != policy.getPolicyId()) {
            throw new CommandExecutionException("The given policy ID " + this.policyID
                    + " does not correspond with the policy ID in the file (" + policy.getPolicyId() + ").");
        }
        String message = "";

        // Second, check if there's an attribute for the path
        boolean updated = false;
        for (PolicyAttribute pa : policy.getAttributes()) {
            if (pa.getPath().equals(this.attributePath)) {
                pa.setAttributeType(this.attributeType);
                pa.setDataType(this.dataType);
                message = "Attribute in policy ID " + policy.getPolicyId() + " for path " + pa.getPath()
                        + " was updated succesfully";
                updated = true;
            }
        }

        // Third, create the new Attribute and attach it to the Policy.
        if (!updated) {
            PolicyAttribute newPA = new PolicyAttribute(this.attributePath, this.attributeType, this.dataType);
            policy.addAttribute(newPA);
            message = "An attribute for path " + this.attributePath + " in policy " + policy.getPolicyId()
                    + " was added";
        }

        // Finally, prepare the return
        CommandReturn cr = new CommandReturn(0, message, policy);
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("set_attribute_type"))
            throw new CommandParserException(
                    "Why a non-'set_attribute_type' command ended up in the 'set_attribute_type' part of the parser?");

        // Second, check that the policyID is present and well-formed
        try {
            this.policyID = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandParserException(
                    "There was an error identifying the policyID. Is the ID number well formed?");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'policyID' was not given and it is required.");
        }

        // Third, check the attribute path
        try {
            this.attributePath = args[2];
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'attributePath' was not given and it is required.");
        }

        // Fourth, check the attribute type
        try {
            this.attributeType = AttrType.fromString(args[3].toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new CommandParserException("There was an error idenfitying the Attribute Type. Is the Type '"
                    + args[3] + "' supported?\nThe list of supported protocols is:\n"
                    + Arrays.toString(AttrType.values()));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'attributeType' was not given and it is required.");
        }

        // Fifth, check the data type
        try {
            this.dataType = DataType.fromString(args[4].toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new CommandParserException("There was an error idenfitying the Data Type. Is the Type '" + args[4]
                    + "' supported?\nThe list of supported protocols is:\n" + Arrays.toString(DataType.values()));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'dataType' was not given and it is required.");
        }

        return true;
    }
}
