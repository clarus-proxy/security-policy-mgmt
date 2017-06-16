package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;
import eu.clarussecure.datamodel.PolicyAttribute;
import eu.clarussecure.datamodel.ProtectionAttributeType;

import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Main{

	public static Set<Policy> policies = null;

	static public void main(String[] args) throws IOException, UnsupportedOperationException{
		// Initialization of the Gson library
		// Method setPrettyPrinting allows writing to file in a "human-readable" way
		// Swap the comments of the next liens for the "traditional", single-line printing
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		//Gson g = new Gson();

		// Parse the file with the current policies.
		// The Gson library does the magic to map the Json to Java objects
		policies = g.fromJson(new FileReader("sec-pol-examples/securitypolicy-example.json"), new TypeToken<Set<Policy>>(){}.getType());

		// printPolicies(policies);
		// System.exit(0);

		// Get the instance of the Command Parser.
		// This object will create the correct instance of the Command and delegate its parsing correctly.
		CommandParser parser = CommandParser.getInstance();

		// Parse the command. This method will do all the checkings required to validate the command
		Command com = null;
		try{
			com = parser.parse(args);
		} catch (CommandParserException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}

		// Start the delegation. This command will make the required modifications to the set of policies.
		CommandReturn cr = null;
		try{
			cr = com.execute(policies);
		} catch (CommandExecutionException e){
			System.err.println(e.getMessage());
			System.exit(2);
		}

		// Print the return on the screen, and alert in case of an error
		if (cr.getReturnValue() == 0) {
			System.out.println(cr.getReturnInfo());
		} else {
			System.out.println("The command '" + args[0] + "' could not be completed:");
			System.out.println(cr.getReturnInfo());
		}

		// Reconstruct the JSON representaiton of the policies
		String newFileContent = g.toJson(policies);

		// Write back the JSON file
		FileWriter f = new FileWriter("sec-pol-examples/securitypolicy-example.json");
		f.write(newFileContent);
		f.close();

		//printPolicies(policies);

		// Return the value to the shell as well
		System.exit(cr.getReturnValue());
	}

	static public Policy findPolicy(int id){
		for (Policy p : policies)
			if(p.getPolicyID() == id)
				return p;

		return null;
	}

	// This method should not be called, it is intended to print the content of the
	static public void printPolicies(Set<Policy> policies) throws UnsupportedOperationException{

		//"Test case":  Print the content of the file.

		for(Policy p : policies){
			System.out.println("ID = " + p.getPolicyID() + ", name = " + p.getPolicyName());

			System.out.println("Endpoint = " + p.getEndpoint().getProtocol() + ", port = " + p.getEndpoint().getPort() + ", baseURL = " + p.getEndpoint().getBaseUrl());

			for(PolicyAttribute a : p.getAttributes())
				System.out.println("\tattribute: path = " + a.getPath() + ", type = " + a.getAttributeType() + ", datatype = " + a.getDataType());

			System.out.println("\tModule = " + p.getProtection().getModule());

			for (ProtectionAttributeType pa : p.getProtection().getAttributeTypes()){
				System.out.println("\t\tProtection = " + pa.getProtection() + ", type = " + pa.getType());

				if(pa.getParameter() != null){
					System.out.println("\t\t\tParamName = " + pa.getParameter().getParam() + ", ParamValue = " + pa.getParameter().getValue());
				} else {
					System.out.println("\t\t\t----");
				}
			}
		}

		throw new UnsupportedOperationException("This method should not be called, it is intended to print the content of the JSON file!");
	}
}
