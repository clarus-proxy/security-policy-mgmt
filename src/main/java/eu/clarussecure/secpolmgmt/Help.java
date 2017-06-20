package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;

import java.io.InputStream;
import java.io.IOException;

import java.util.Set;

public class Help extends Command {
    //TODO - Put other data from the command as fields of the object
    private int policyID;

    public Help(String[] args) throws CommandParserException {
        parseCommandArgs(args);
    }

    @Override
    public CommandReturn execute(Set<Policy> policies) throws CommandExecutionException {
        // Prepare the output
        String data = "";

        try {
            // Get the embedded resource from the jar file
            InputStream f = Help.class.getClassLoader().getResource("help.txt").openStream();
            byte[] buf = new byte[(int) f.available()];

            // Read the content
            f.read(buf);
            data = new String(buf, "UTF-8");
        } catch (IOException e) {
            data = "help not found";
        }

        CommandReturn cr = new CommandReturn(0, data);
        return cr;
    }

    @Override
    public boolean parseCommandArgs(String[] args) throws CommandParserException {
        // Default case, nothing to do here

        return true;
    }
}
