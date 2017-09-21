package eu.clarussecure.secpolmgmt.dao;

import eu.clarussecure.datamodel.Policy;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;

import org.bson.Document;

import java.util.Set;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.model.Sorts;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLARUSPolicyDAO {
    // Singleton implementation
    private static CLARUSPolicyDAO instance = null;
    private final Gson g;
    private final MongoDatabase db;
    private final MongoClient mongoClient;
    private int instancesNumber;

    private String confFile = "/etc/clarus/clarus-mgmt-tools.conf";
    private String mongoDBHostname = "localhost"; // Default server
    private int mongoDBPort = 27017; // Default port
    private String clarusDBName = "CLARUS"; // Default DB name

    private CLARUSPolicyDAO() {
        // Correctly configure the log level
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        // Create the GsonBuilder Object
        this.g = new GsonBuilder().setPrettyPrinting().create();
        // Open the configuraiton file to extract the information from it.
        this.processConfigurationFile();
        // Create a new client connecting to "localhost" on port 
        this.mongoClient = new MongoClient(this.mongoDBHostname, this.mongoDBPort);

        // Get the database (will be created if not present)
        this.db = mongoClient.getDatabase(this.clarusDBName);

        this.instancesNumber++;
    }

    public synchronized static CLARUSPolicyDAO getInstance() {
        if (CLARUSPolicyDAO.instance == null)
            CLARUSPolicyDAO.instance = new CLARUSPolicyDAO();
        return CLARUSPolicyDAO.instance;
    }

    public synchronized void deleteInstance() {
        this.instancesNumber--;

        if (this.instancesNumber <= 0) {
            this.mongoClient.close();
            CLARUSPolicyDAO.instance = null;
        }
    }

    public boolean savePolicy(Policy pol) {
        // Get the collection of BSON documents that contain the policies
        MongoCollection<Document> collection = this.db.getCollection("policies");

        // Serialize the policy
        String jsonPolicy = g.toJson(pol);

        // Insert the policy into the datbase
        collection.insertOne(Document.parse(jsonPolicy));
        return true;
    }

    public Set<Policy> getPolicies() {
        // Get the collection of BSON documents that contain the policies
        MongoCollection<Document> collection = this.db.getCollection("policies");

        // Create the Set to contain the results
        Set<Policy> result = new HashSet<>();

        // Find all policies
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                // Get the JSON representation of the Policy
                String jsonPolicy = cursor.next().toJson();
                // Deserialize the policy
                Policy pol = g.fromJson(jsonPolicy, Policy.class);
                // Add the policy to the result;
                result.add(pol);
            }
        }

        return result;
    }

    public int getLastPolicyID() {
        int policyID = 0;
        // Get the collection of BSON documents that contain the policies
        MongoCollection<Document> collection = this.db.getCollection("policies");
        MongoCursor<Document> iterator = collection.find().sort(Sorts.descending("policyID")).limit(1).iterator();

        while (iterator.hasNext()) {
            Document doc = iterator.next();

            if (doc == null)
                break;
            policyID = doc.getInteger("policyId");
        }

        return policyID;
    }

    public boolean removePolicy(int polID) {
        // Get the collection of BSON documents that contain the policies
        MongoCollection<Document> collection = this.db.getCollection("policies");

        // Delete the policy from the database;
        long deleted = collection.deleteOne(eq("policyId", polID)).getDeletedCount();

        return deleted > 0;
    }
    
    private void processConfigurationFile() throws RuntimeException {
        // Open the file in read-only mode. This will avoid any permission problem
        try {
            // Read all the lines and join them in a single string
            List<String> lines = Files.readAllLines(Paths.get(this.confFile));
            String content = lines.stream().reduce("", (a, b) -> a + b);

            // Use the bson document parser to extract the info
            Document doc = Document.parse(content);
            this.mongoDBHostname = doc.getString("CLARUS_policies_db_hostname");
            this.mongoDBPort = doc.getInteger("CLARUS_policies_db_port");
            this.clarusDBName = doc.getString("CLARUS_policies_db_name");
        } catch (IOException e) {
            throw new RuntimeException("CLARUS configuration file could not be processed", e);
        }
    }
}
