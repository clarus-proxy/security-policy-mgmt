package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.PolicyAttribute;
import eu.clarussecure.datamodel.ProtectionAttributeType;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Set;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import eu.clarussecure.datamodel.types.Module;
import eu.clarussecure.datamodel.types.Protocol;
import eu.clarussecure.datamodel.types.utils.ModuleAdapter;
import eu.clarussecure.datamodel.types.utils.ProtocolAdapter;

public class Main {

    public static Set<Policy> policies = null;
    public static Policy policy = null;
    public static String filename = "sec-pol-examples/new-structure.json";

    static public void main(String[] args) {
        // Initialization of the Gson library
        // Method setPrettyPrinting allows writing to file in a "human-readable" way
        // Swap the comments of the next liens for the "traditional", single-line printing
        Gson g = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Module.class, new ModuleAdapter())
                .registerTypeAdapter(Protocol.class, new ProtocolAdapter()).create();
        //Gson g = new Gson();

        // Parse the file with the current policies.
        // The Gson library does the magic to map the Json to Java objects
        FileReader f = null;
        try {
            // Try to open the file containing the temporal policy
            f = new FileReader(filename);
        } catch (FileNotFoundException e) {
            // File was not found, create a new one.
        }
        //policies = g.fromJson(f, new TypeToken<Set<Policy>>() {
        //}.getType());
        policy = g.fromJson(f, Policy.class);

        // Get the instance of the Command Parser.
        // This object will create the correct instance of the Command and delegate its parsing correctly.
        CommandParser parser = CommandParser.getInstance();

        try {
            // Parse the command. This method will do all the checkings required to validate the command
            Command com = parser.parse(args);
            // Start the delegation. This command will make the required modifications to the set of policies.
            Set<Policy> aux = new HashSet<>();
            aux.add(policy);
            CommandReturn cr = com.execute(aux);
            // Print the return on the screen, and alert in case of an error
            if (cr.getReturnValue() == 0) {
                System.out.println(cr.getReturnInfo());
            } else {
                System.out.println("The command '" + args[0] + "' could not be completed:");
                System.out.println(cr.getReturnInfo());
            }

            // Reconstruct the JSON representaiton of the policies
            String newFileContent = g.toJson(policy);

            // Write back the JSON file
            try (FileWriter fout = new FileWriter(filename)) {
                fout.write(newFileContent);
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

    static public Policy findPolicy(int id) {
        for (Policy p : policies)
            if (p.getPolicyID() == id)
                return p;

        return null;
    }

    // This method should not be called, it is intended to print the content of the
    static public void printPolicies(Set<Policy> policies) throws UnsupportedOperationException {

        //"Test case":  Print the content of the file.

        for (Policy p : policies) {
            System.out.println("ID = " + p.getPolicyID() + ", name = " + p.getPolicyName());

            System.out.println("Endpoint = " + p.getEndpoint().getProtocol() + ", port = " + p.getEndpoint().getPort());

            for (PolicyAttribute a : p.getAttributes())
                System.out.println("\tattribute: path = " + a.getPath() + ", type = " + a.getAttributeType()
                        + ", datatype = " + a.getDataType());

            System.out.println("\tModule = " + p.getProtection().getModule());

            for (ProtectionAttributeType pa : p.getProtection().getAttributeTypes()) {
                System.out.println("\t\tProtection = " + pa.getProtection() + ", type = " + pa.getType());

                if (pa.getParameters() != null) {
                    System.out.println("\t\t\tParamName = " + pa.getParameters().getParam() + ", ParamValue = "
                            + pa.getParameters().getValue());
                } else {
                    System.out.println("\t\t\t----");
                }
            }
        }

        throw new UnsupportedOperationException(
                "This method should not be called, it is intended to print the content of the JSON file!");
    }
}
