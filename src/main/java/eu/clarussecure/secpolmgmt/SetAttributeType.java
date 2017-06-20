package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.types.AttrType;
import eu.clarussecure.datamodel.types.DataType;
import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.PolicyAttribute;

import java.util.Set;
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
    public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException {
        String message = "";

        // First, find the policy
        Policy policy = null;

        for (Policy p : policies)
            if (p.getPolicyID() == this.policyID)
                policy = p;

        if (policy == null)
            throw new CommandExecutionException("The policy with ID " + this.policyID + " could not be found!");

        // Second, check if there's an attribute for the path
        boolean updated = false;
        for (PolicyAttribute pa : policy.getAttributes()) {
            if (pa.getPath().equals(this.attributePath)) {
                pa.setAttributeType(this.attributeType);
                pa.setDataType(this.dataType);
                message = "Attribute in policy ID " + policy.getPolicyID() + " for path " + pa.getPath()
                        + " was updated succesfully";
                updated = true;
            }
        }

        // Third, create the new Attribute and attach it to the Policy.
        if (!updated) {
            PolicyAttribute newPA = new PolicyAttribute(this.attributePath, this.attributeType, this.dataType);
            policy.addAttribute(newPA);
            message = "An attribute for path " + this.attributePath + " in policy " + policy.getPolicyID()
                    + " was added";
        }

        // Finally, prepare the return
        CommandReturn cr = new CommandReturn(0, message);
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
