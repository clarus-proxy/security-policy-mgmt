package eu.clarussecure.secpolmgmt;

import eu.clarussecure.secpolmgmt.dao.CLARUSPolicyDAO;

import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.PolicyAttribute;
import eu.clarussecure.datamodel.ProtectionAttributeType;

import java.util.Set;

public class List extends Command {

    public List(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException {
        this.verifyRights("admin");
        // At this point, the user SHOULD be identified and authorized to execute this command

        // FIXME - Review the implementation of this command
        // NOTE: If a path for the identiy file is present, it will be used to identify the user even if a password was provided.
        // Too many doubts about what "registering" a policy means...
        // Do they need to comunicate the configuration? To whom? Afterwards, does the policy need to be deleted from the file?

        CLARUSPolicyDAO dao = CLARUSPolicyDAO.getInstance();
        Set<Policy> pols = dao.getPolicies();
        dao.deleteInstance();

        List.printPolicies(pols);

        CommandReturn cr = new CommandReturn(0, "");
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("list"))
            throw new CommandParserException("Why a non-'list' command ended up in the 'list' part of the parser?");

        this.parseCredentials(args);

        return true;
    }

    static public void printPolicies(Set<Policy> policies) throws UnsupportedOperationException {
        for (Policy p : policies) {
            System.out.println("ID = " + p.getPolicyID() + ", name = " + p.getPolicyName());

            System.out.println("Endpoint = " + p.getEndpoint().getProtocol() + ", port = " + p.getEndpoint().getPort()
                    + ", baseURL = " + p.getEndpoint().getBaseUrl());

            for (PolicyAttribute a : p.getAttributes())
                System.out.println("\tattribute: path = " + a.getPath() + ", type = " + a.getAttributeType()
                        + ", datatype = " + a.getDataType());

            System.out.println("\tModule = " + p.getProtection().getModule());

            for (ProtectionAttributeType pa : p.getProtection().getAttributeTypes()) {
                System.out.println("\t\tProtection = " + pa.getProtection() + ", type = " + pa.getType());

                if (pa.getParameter() != null) {
                    System.out.println("\t\t\tParamName = " + pa.getParameter().getParam() + ", ParamValue = "
                            + pa.getParameter().getValue());
                } else {
                    System.out.println("\t\t\t----");
                }
            }
        }
    }
}
