/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.clarussecure.secpolmgmt.dao;

/**
 *
 * @author diegorivera
 */

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

public class CLARUSPolicyDAO{
	// Singleton implementation
	private static CLARUSPolicyDAO instance = null;
	private Gson g;
	private MongoDatabase db;
	private MongoClient mongoClient;
	private int instancesNumber;

	private CLARUSPolicyDAO(){
		// Create the GsonBuilder Object
		this.g = new GsonBuilder().setPrettyPrinting().create();
		// Create a new client connecting to "localhost" on port 
		this.mongoClient = new MongoClient("localhost", 27017);

		// Get the database (will be created if not present)
		this.db = mongoClient.getDatabase("CLARUS");

		this.instancesNumber++;
	}

	public static CLARUSPolicyDAO getInstance(){
		if (CLARUSPolicyDAO.instance == null)
			CLARUSPolicyDAO.instance = new CLARUSPolicyDAO();
		return CLARUSPolicyDAO.instance;
	}

	public void deleteInstance(){
		this.instancesNumber--;

		if(this.instancesNumber <= 0){
			this.mongoClient.close();
			CLARUSPolicyDAO.instance = null;
		}
	}

	public boolean savePolicy(Policy pol){
		// Get the collection of BSON documents that contain the policies
		MongoCollection<Document> collection = this.db.getCollection("policies");;

		// Serialize the policy
		String jsonPolicy = g.toJson(pol);

		// Insert the policy into the datbase
		collection.insertOne(Document.parse(jsonPolicy));
		return true;
	}

	public Set<Policy> getPolicies(){
		// Get the collection of BSON documents that contain the policies
		MongoCollection<Document> collection = this.db.getCollection("policies");;

		// Create the Set to contain the results
		Set<Policy> result = new HashSet<Policy>();

		// Find all policies
		MongoCursor<Document> cursor = collection.find().iterator();
		try{
			while(cursor.hasNext()){
				// Get the JSON representation of the Policy
				String jsonPolicy = cursor.next().toJson();
				// Deserialize the policy
				Policy pol = g.fromJson(jsonPolicy, Policy.class);
				// Add the policy to the result;
				result.add(pol);
			}
		} finally{
			cursor.close();
		}

		return result;
	}

	public boolean removePolicy(Policy pol){
		// Get the collection of BSON documents that contain the policies
		MongoCollection<Document> collection = this.db.getCollection("policies");;

		// Delete the policy from the database;
		long deleted = collection.deleteOne(eq("policyId", pol.getPolicyID())).getDeletedCount();

		return deleted > 0 ? true : false;
	}
}
