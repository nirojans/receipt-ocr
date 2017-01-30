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

    public static String dataLakeDB;
    public static String manualDataDB;
    public static String manualDataTescoCollection;
    public static String manualDataSaintsCollection;

    public static String ocrDB;
    public static String ocrReceiptCollection;

    public static String mitraDB;
    public static String configsCollection;

    public static String localhost = "localhost";
    public static int port = 27017;

    static {
        Properties properties = Configs.getConfigs(Configs.DB_CONFIG_FILE_NAME);
        dataLakeDB = properties.getProperty("dataLakeDbName");
        manualDataDB = properties.getProperty("manualDataDbName");
        manualDataTescoCollection = properties.getProperty("manualTescoReceiptCollection");
        manualDataSaintsCollection = properties.getProperty("manualSaintsReceiptCollection");

        ocrDB = properties.getProperty("ocrDBName");
        ocrReceiptCollection = properties.getProperty("ocrReceiptCollection");

        mitraDB = properties.getProperty("mitraDB");
        configsCollection = properties.getProperty("configsCollection");
    }

    public static void insertIntoDB(MasterReceipt masterReceipt) throws UnknownHostException {

        MongoClient mongo = new MongoClient(localhost, port);
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

    public static void insertRawDataIntoDB(Receipt receipt) throws UnknownHostException {
        MongoClient mongo = new MongoClient(localhost, port);
        DB db = mongo.getDB(ocrDB);
        DBCollection col = db.getCollection("rawData");

        String[] rawData = receipt.getRawData();
        BasicDBObject document = new BasicDBObject();
        // Delete All documents from collection Using blank BasicDBObject
        col.remove(document);

        // Delete All documents from collection using DBCursor
        DBCursor cursor = col.find();
        while (cursor.hasNext()) {
            col.remove(cursor.next());
        }

        for (int i=0;i<rawData.length;i++) {
            String desc = rawData[i];
            BasicDBObject itemDocument = new BasicDBObject();
            itemDocument.put("id", receipt.getId());
            itemDocument.put("description", desc);
            itemDocument.put("lineNumber", i);
            col.insert(itemDocument);
        }
        mongo.close();

    }

    public static void insertLineItemsIntoDB(Receipt receipt) throws UnknownHostException {

        MongoClient mongo = new MongoClient(localhost, port);
        DB db = mongo.getDB(ocrDB);
        DBCollection col = db.getCollection("lineItems");

        List<LineItem> lineItemList = receipt.getLineItems();
        BasicDBObject document = new BasicDBObject();
        // Delete All documents from collection Using blank BasicDBObject
        col.remove(document);

        // Delete All documents from collection using DBCursor
        DBCursor cursor = col.find();
        while (cursor.hasNext()) {
            col.remove(cursor.next());
        }

        for (LineItem item : lineItemList) {
            BasicDBObject itemDocument = new BasicDBObject();
            itemDocument.put("id", receipt.getId());
            itemDocument.put("description", item.getDescription());
            itemDocument.put("lineNumber", item.getLineNumber());
            col.insert(itemDocument);
        }
        mongo.close();
    }

    public static LineItem doFullTextSearchForLineItem(String searchTerm, String collectionName) throws UnknownHostException {

        MongoClient mongo = new MongoClient(localhost, port);
        DB db = mongo.getDB(ocrDB);
        DBCollection col = db.getCollection(collectionName);

        DBObject search = new BasicDBObject("$text", new BasicDBObject("$search", searchTerm));
        DBObject project = new BasicDBObject("score", new BasicDBObject("$meta", "textScore"));
        DBObject sorting = new BasicDBObject("score", new BasicDBObject("$meta", "textScore"));

        DBCursor cursor = col.find(search, project).sort(sorting).limit(2);;

        try {
            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                return  (new Gson()).fromJson(dbObject.toString(), LineItem.class);
            }
        } finally {
            cursor.close();
            mongo.close();
        }
        return null;
    }

    public static List<ManualReceiptLineItem> doFullTextSearchFromManualData(String searchTerm, String collectionName) throws UnknownHostException {
        MongoClient mongo = new MongoClient(localhost, port);
        DB db = mongo.getDB(dataLakeDB);
        DBCollection col = db.getCollection(collectionName);

        DBObject search = new BasicDBObject("$text", new BasicDBObject("$search", searchTerm));
        DBObject project = new BasicDBObject("score", new BasicDBObject("$meta", "textScore"));
        DBObject sorting = new BasicDBObject("score", new BasicDBObject("$meta", "textScore"));

        DBCursor cursor = col.find(search, project).sort(sorting).limit(10);;
        List<ManualReceiptLineItem> manualReceiptLineItemList = new ArrayList<>();

        try {
            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                ManualReceiptLineItem manualReceiptLineItem = (new Gson()).fromJson(dbObject.toString(), ManualReceiptLineItem.class);
                manualReceiptLineItemList.add(manualReceiptLineItem);
            }
        } finally {
            cursor.close();
            mongo.close();
        }
        return manualReceiptLineItemList;
    }

    public static List<ManualReceiptLineItem> getReceiptFromManualData(String searchID, String collectionName) throws UnknownHostException {

        MongoClient mongo = new MongoClient(localhost, 27017);
        DB db = mongo.getDB(manualDataDB);
        DBCollection col = db.getCollection(collectionName);

        DBCursor cursor = col.find(new BasicDBObject("TILLROLL_DOC_ID", searchID));
        List<ManualReceiptLineItem> manualReceiptLineItemList = new ArrayList<>();

        try {
            int i = 0;
            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                ManualReceiptLineItem manualReceiptLineItem = (new Gson()).fromJson(dbObject.toString(), ManualReceiptLineItem.class);
                i = i + 1;
                manualReceiptLineItem.setRecordID(i);
                manualReceiptLineItemList.add(manualReceiptLineItem);
            }
        } finally {
            cursor.close();
            mongo.close();
        }
        return manualReceiptLineItemList;
    }


    /*
    Method saves the final results to the mongo DB
     */
    public static void insertBatchProcessDetails(Result result) throws UnknownHostException {

        MongoClient mongo = new MongoClient(localhost, port);

        DB db = mongo.getDB(ocrDB);
        DBCollection col = db.getCollection("result");

        //check if a document exists, If exists remove id=filename
        if (isRecordExists(col, "id", result.getId())) {
            BasicDBObject document = new BasicDBObject();
            document.put("id", result.getId());
            col.remove(document);
        }

        // Insert new document to mongo DB
        Gson gson = new Gson();
        BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(result));
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

    public static boolean isRecordExists(DBCollection col, String searchAttribute, int searchValue) {
        DBCursor cursor = col.find(new BasicDBObject(searchAttribute, searchValue));
        while(cursor.hasNext()) {
            return true;
        }
        return false;
    }


    /*
    This method is used to get the status variable to process the files randomly from the receipts folder
     */
    public static boolean getRandomProcessStatus() throws UnknownHostException {
        MongoClient mongo = new MongoClient(localhost, 27017);
        DB configsDB = mongo.getDB(mitraDB);
        DBCollection col = configsDB.getCollection(configsCollection);

        DBCursor cursor = col.find(new BasicDBObject("id", "random"));

        List<SystemParameters> systemParametersList = new ArrayList<>();
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();
            SystemParameters systemParameters = (new Gson()).fromJson(dbObject.toString(), SystemParameters.class);
            systemParametersList.add(systemParameters);
        }

        for (SystemParameters params : systemParametersList) {
            if (params.isStatus()) {
                BasicDBObject searchQuery = new BasicDBObject().append("id", "random");
                BasicDBObject updateQuery = new BasicDBObject();
                updateQuery.append("$set", new BasicDBObject().append("status", false));
                col.update(searchQuery, updateQuery, false, true);

                return true;
            }
        }
        return false;
    }

    /*
        This method will be used as a counter implementation for mongo db
     */
    public static int getNextSequence() throws UnknownHostException {

        MongoClient mongoClient = new MongoClient( localhost , 27017 );
        // Now connect to your databases
        DB db = mongoClient.getDB(mitraDB);
        DBCollection collection = db.getCollection(configsCollection);

        BasicDBObject find = new BasicDBObject();
        find.put("id", "counter");
        BasicDBObject update = new BasicDBObject();
        update.put("$inc", new BasicDBObject("seq", 1));
        DBObject obj =  collection.findAndModify(find, update);

        double d = Double.parseDouble(obj.get("seq").toString());
        return  (int) d;
    }


    /*
    TODO check if not it proper use remove
     */
    public static List<MasterReceipt> getOCRMasterReceipt(String searchID, String collectionName) throws UnknownHostException {

        MongoClient mongo = new MongoClient(localhost, 27017);
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

}
