package com.mitrai.scanner;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by nirojans on 1/12/17.
 */
public class DataServiceImpl {

    public static String manualDataDB;
    public static String manualDataTescoCollection;
    public static String manualDataSaintsCollection;

    public static String ocrDB;
    public static String ocrReceiptCollection;

    static {
        Properties properties = Configs.getConfigs(Configs.DB_CONFIG_FILE_NAME);
        manualDataDB = properties.getProperty("manualDataDbName");
        manualDataTescoCollection = properties.getProperty("manualTescoReceiptCollection");
        manualDataSaintsCollection = properties.getProperty("manualSaintsReceiptCollection");

        ocrDB = properties.getProperty("ocrDBName");
        ocrReceiptCollection = properties.getProperty("ocrReceiptCollection");
    }


    public static void insertIntoDB(MasterReceipt masterReceipt) throws UnknownHostException {

        MongoClient mongo = new MongoClient("localhost", 27017);

        DB db = mongo.getDB(ocrDB);
        DBCollection col = db.getCollection(ocrReceiptCollection);

        // check if a document exists, If exists remove
        if (isRecordExists(col, "id", masterReceipt.getId())) {
            BasicDBObject document = new BasicDBObject();
            document.put("id", masterReceipt.getId());
            col.remove(document);
        }

        // Insert new document to mongo DB
        Gson gson = new Gson();
        BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(masterReceipt));
        col.insert(obj);
        mongo.close();
    }

    public static boolean isRecordExists(DBCollection col, String searchAttribute, String searchValue) {
        DBCursor cursor = col.find(new BasicDBObject(searchAttribute, searchValue));

        while(cursor.hasNext()) {
            return true;
        }
        return false;
    }

    public static List<ManualReceipt> doFullTextSearchFromManualData(String searchTerm, String collectionName) throws UnknownHostException {
        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB(manualDataDB);
        DBCollection col = db.getCollection(collectionName);

        DBObject search = new BasicDBObject("$text", new BasicDBObject("$search", searchTerm));
        DBObject project = new BasicDBObject("score", new BasicDBObject("$meta", "textScore"));
        DBObject sorting = new BasicDBObject("score", new BasicDBObject("$meta", "textScore"));

        DBCursor cursor = col.find(search, project).sort(sorting);


        List<ManualReceipt> manualReceiptList = new ArrayList<>();

        try {
            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                ManualReceipt manualReceipt = (new Gson()).fromJson(dbObject.toString(), ManualReceipt.class);
                manualReceiptList.add(manualReceipt);
            }
        } finally {
            cursor.close();
            mongo.close();
        }
        return manualReceiptList;
    }

    public static List<ManualReceipt> getReceiptFromManualData(String searchID, String collectionName) throws UnknownHostException {

        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB(manualDataDB);
        DBCollection col = db.getCollection(collectionName);

        DBCursor cursor = col.find(new BasicDBObject("TILLROLL_DOC_ID", searchID));
        List<ManualReceipt> manualReceiptList = new ArrayList<>();

        try {
            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                ManualReceipt manualReceipt = (new Gson()).fromJson(dbObject.toString(), ManualReceipt.class);
                manualReceiptList.add(manualReceipt);
            }
        } finally {
            cursor.close();
            mongo.close();
        }
        return manualReceiptList;
    }

    public static List<MasterReceipt> getOCRMasterReceipt(String searchID, String collectionName) throws UnknownHostException {

        MongoClient mongo = new MongoClient("localhost", 27017);
        DB mongoOCRDB = mongo.getDB(ocrDB);
        DBCollection col = mongoOCRDB.getCollection(ocrReceiptCollection);

        DBCursor cursor = col.find(new BasicDBObject("id", searchID));
        List<MasterReceipt> masterReceiptList = new ArrayList<>();

        try {
            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                MasterReceipt masterReceipt = (new Gson()).fromJson(dbObject.toString(), MasterReceipt.class);
                masterReceiptList.add(masterReceipt);
            }
        } finally {
            cursor.close();
            mongo.close();
        }
        return masterReceiptList;
    }

    public static boolean getRandomProcessingStatus() throws UnknownHostException {

        boolean randomProcessStatus = false;

        MongoClient mongo = new MongoClient("localhost", 27017);
        DB configsDB = mongo.getDB("mitra");
        DBCollection col = configsDB.getCollection("configs");

        List<SystemParameters> systemParametersList = new ArrayList<>();

        DBCursor cursor = col.find(new BasicDBObject("random", true));

        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();
            SystemParameters systemParameters = (new Gson()).fromJson(dbObject.toString(), SystemParameters.class);
            systemParametersList.add(systemParameters);
        }

        for (SystemParameters params : systemParametersList) {

            if (params.isRandom()) {

                randomProcessStatus = true;

                BasicDBObject newDocument = new BasicDBObject();
                newDocument.put("random", false);

                BasicDBObject searchQuery = new BasicDBObject().append("random", true);
                col.update(searchQuery, newDocument);

            }
        }
        return randomProcessStatus;
    }

}
