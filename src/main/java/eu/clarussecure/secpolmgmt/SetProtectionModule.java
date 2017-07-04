package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.types.Module;
import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.Protection;

import java.util.Set;
import java.util.Arrays;

public class SetProtectionModule extends Command {
    private int policyID;
    private Module protectionModule;

    public SetProtectionModule(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException {
        // First, find the policy
        Policy policy = null;

        for (Policy p : policies)
            if (p.getPolicyID() == this.policyID)
                policy = p;

        if (policy == null)
            throw new CommandExecutionException("The policy with ID " + this.policyID + " could not be found!");

        // Second, Assign the CLARUS protection Module
        policy.setProtection(new Protection(this.protectionModule));

        // Finally, prepare the return info
        CommandReturn cr = new CommandReturn(0,
                "The Protection module for Policy ID " + policy.getPolicyID() + " was correctly set.");
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("set_protection_module"))
            throw new CommandParserException(
                    "Why a non-'set_protection_module' command ended up in the 'set_protection_module' part of the parser?");

        // Second, check that the policyID is present and well-formed
        try {
            this.policyID = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandParserException(
                    "There was an error identifying the policyID. Is the port number well formed?");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'policyID' was not given and it is required.");
        }

        // Third, identify the CLARUS Protection Module
        try {
            // Load the installed modules.
            Module.initialize();
            // Validate the name of the module
            if (!Module.isValidModule(args[2].toLowerCase())) {
                throw new IllegalArgumentException();
            }

            this.protectionModule = new Module(args[2].toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new CommandParserException(
                    "There was an error idenfitying the CLARUS Protection Module. Is the Module '" + args[2]
                            + "' supported?\nThe list of supported modules is:\n" + Module.getModulesAsString());
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'protectionModule' was not given and it is required.");
        }
        return true;
    }
}
