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

/**
 * Created by nirojans on 1/12/17.
 */
public class DataServiceImpl {

    public static void insertIntoDB(MasterReceipt receipt) throws UnknownHostException {

        MongoClient mongo = new MongoClient("localhost", 27017);

        DB db = mongo.getDB("mitra");
        DBCollection col = db.getCollection("receipts");

        Gson gson = new Gson();
        BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(receipt));
        col.insert(obj);
        mongo.close();
    }

    public static List<Receipt> getReceiptDataFromDB() throws UnknownHostException {
        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("mitra");
        DBCollection col = db.getCollection("receipts");

        DBCursor cursor = col.find(new BasicDBObject("id","second"));
        List<Receipt> receiptList = new ArrayList<>();

        try {
            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                Receipt dbReceipt = (new Gson()).fromJson(dbObject.toString(), Receipt.class);
                receiptList.add(dbReceipt);
            }
        } finally {
            cursor.close();
            mongo.close();
        }
        return receiptList;
    }

}
