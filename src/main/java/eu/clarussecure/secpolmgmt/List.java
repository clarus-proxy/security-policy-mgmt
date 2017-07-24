package eu.clarussecure.secpolmgmt;

import eu.clarussecure.secpolmgmt.dao.CLARUSPolicyDAO;

import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.ProtectionAttributeType;

import java.util.Set;

public class List extends Command {

    public List(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Policy policy) throws CommandExecutionException {
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

        CommandReturn cr = new CommandReturn(0, "", policy);
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // First, sanity check
        if (!args[0].toLowerCase().equals("list")) {
            throw new CommandParserException("Why a non-'list' command ended up in the 'list' part of the parser?");
        }

        this.parseCredentials(args);

        return true;
    }

    static public void printPolicies(Set<Policy> policies) throws UnsupportedOperationException {
        for (Policy pol : policies) {
            System.out.println("ID = " + pol.getPolicyId() + ", name = " + pol.getPolicyName() + ", usage = "
                    + pol.getDataUsage());

            System.out.println("\tEndpoint: Protocol Name = " + pol.getEndpoint().getProtocol().getProtocolName()
                    + ", port = " + pol.getEndpoint().getPort());

            pol.getEndpoint().getParameters().forEach((param) -> {
                System.out.println("\t\tParameter: name = " + param.getParam() + ", value = " + param.getValue());
            });

            System.out.println("\tAttributes:");

            pol.getAttributes().forEach((attrib) -> {
                System.out.println("\t\tattribute: path = " + attrib.getPath() + ", type = " + attrib.getAttributeType()
                        + ", datatype = " + attrib.getDataType());
            });

            System.out.println("\tModule = " + pol.getProtection().getModule().getClarusModuleName());

            for (ProtectionAttributeType pa : pol.getProtection().getAttributeTypes()) {
                System.out.println("\t\tProtection = " + pa.getProtection() + ", type = " + pa.getType());

                if (pa.getParameters() != null) {
                    pa.getParameters().forEach((protParam) -> {
                        System.out.println("\t\t\tParameter: name = " + protParam.getParam() + ", value = "
                                + protParam.getValue());
                    });
                } else {
                    System.out.println("\t\t\t----");
                }
            }
        }
    }
}
