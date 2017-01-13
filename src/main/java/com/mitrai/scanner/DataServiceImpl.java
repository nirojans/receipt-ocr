package com.mitrai.scanner;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import java.net.UnknownHostException;

/**
 * Created by nirojans on 1/12/17.
 */
public class DataServiceImpl {

    public static void createDB(Receipt receipt) throws UnknownHostException {

        MongoClient mongo = new MongoClient("localhost", 27017);

        DB db = mongo.getDB("mitra");
        DBCollection col = db.getCollection("receipts");

        Gson gson = new Gson();
        BasicDBObject obj = (BasicDBObject) JSON.parse(gson.toJson(receipt));
        col.insert(obj);



        DBCursor cursor = col.find(new BasicDBObject("id","second"));

        try {
            while(cursor.hasNext()) {
                DBObject dbobj = cursor.next();
                //Converting BasicDBObject to a custom Class(Employee)
                Receipt dbReceipt = (new Gson()).fromJson(dbobj.toString(), Receipt.class);
                System.out.println(dbReceipt);
            }
        } finally {
            cursor.close();
        }

        mongo.close();
    }
}
