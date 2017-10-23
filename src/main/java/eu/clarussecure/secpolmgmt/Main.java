package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.clarussecure.datamodel.types.Module;
import eu.clarussecure.datamodel.types.Protocol;
import eu.clarussecure.datamodel.types.utils.ModuleAdapter;
import eu.clarussecure.datamodel.types.utils.ProtocolAdapter;
import java.io.File;

public class Main {

    public static Policy policy = null;
    public static String filename = "/Users/diegorivera/Dropbox/Montimage/CLARUS/policy.json";

    static public void main(String[] args) {
        // Initialization of the Gson library
        // Method setPrettyPrinting allows writing to file in a "human-readable" way
        // Swap the comments of the next lines for the "traditional", single-line printing
        Gson g = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Module.class, new ModuleAdapter())
                .registerTypeAdapter(Protocol.class, new ProtocolAdapter()).create();
        //Gson g = new Gson();

        // Parse the file with the current policies.
        // The Gson library does the magic to map the Json to Java objects
        FileReader f = null;
        try {
            // Try to open the file containing the temporal policy
            f = new FileReader(filename);
            policy = g.fromJson(f, Policy.class);
        } catch (FileNotFoundException e) {
            // File was not found, create a new one.
        }
        // Get the instance of the Command Parser.
        // This object will create the correct instance of the Command and delegate its parsing correctly.
        CommandParser parser = CommandParser.getInstance();
        CommandReturn cr = null;
        try {
            // Parse the command. This method will do all the checkings required to validate the command
            Command com = parser.parse(args);
            // Start the delegation. This command will make the required modifications to the policy.
            cr = com.execute(policy);
            // Print the return on the screen, and alert in case of an error
            if (cr.getReturnValue() == 0) {
                System.out.println(cr.getReturnInfo());
            } else {
                System.out.println("The command '" + args[0] + "' could not be completed:");
                System.out.println(cr.getReturnInfo());
            }

            // Write the policy ONLY if the command did not "delete" it (register command)
            if (cr.getModifiedPolicy() != null) {
                // Reconstruct the JSON representaiton of the policies
                String newFileContent = g.toJson(cr.getModifiedPolicy());

                // Write back the JSON file
                try (FileWriter fout = new FileWriter(filename)) {
                    fout.write(newFileContent);
                }
            } else {
                // Delete the file, since there's nothing to write oi it
                File fout = new File(filename);
                fout.delete();
            }

            // Return the value to the shell as well
            System.exit(cr.getReturnValue());
        } catch (CommandParserException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (CommandExecutionException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        } catch (IOException e) {
            System.err.println("An error arose while opening the output JSON file: " + e.getMessage());
            System.exit(-1);
        }
    }
}
