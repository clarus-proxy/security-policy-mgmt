package eu.clarussecure.secpolmgmt;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import eu.clarussecure.datamodel.Policy;
import java.io.FileWriter;
import java.io.IOException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class ConvertPolicy extends Command {

    private int policyID;
    private String filename;

    public ConvertPolicy(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Policy policy) throws CommandExecutionException {
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
        CommandReturn cr;
        try {
            XMLOutputter out = new XMLOutputter();
            out.setFormat(Format.getPrettyFormat().setLineSeparator(LineSeparator.Web).setOmitDeclaration(false)
                    .setOmitEncoding(false));
            out.output(policy.getXMLElement(), new FileWriter(filename));
            cr = new CommandReturn(0, "The policy ID " + policy.getPolicyId() + " was correctly converted to XML.",
                    policy);
        } catch (IOException e) {
            cr = new CommandReturn(1, "The file " + this.filename + " could not be writen.", policy);
        }
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("convert_policy")) {
            throw new CommandParserException(
                    "Why a non-'convert_policy' command ended up in the 'register' part of the parser?");
        }

        // Second, check that the policyID is present and well-formed
        try {
            this.policyID = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandParserException("There was an error identifying the policyID. Is the number well formed?");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandParserException("The field 'policyID' was not given and it is required.");
        }

        // Retrieve the filename to store the policy
        this.filename = args[2];

        return true;
    }
}
