package com.vivavu.dream.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.vivavu.dream.model.bucket.Bucket;

import java.sql.SQLException;

/**
 * Created by yuja on 14. 3. 5.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "dream.db";
    private static final int DATABASE_VERSION = 3;

    // the DAO object we use to access the SimpleData table
    private Dao<Bucket, Integer> bucketDao = null;
    private RuntimeExceptionDao<Bucket, Integer> bucketRuntimeDao = null;


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Bucket.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        /*
        // here we try inserting data in the on-create as a test
        RuntimeExceptionDao<Comment, Integer> dao = getCommentsDataDao();
        // create some entries in the onCreate
        Comment comment = new Comment("First Test Comment");
        dao.create(comment);
        Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Bucket.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Bucket, Integer> getBucketDao() throws SQLException {
        if (bucketDao == null) {
            bucketDao = getDao(Bucket.class);
        }
        return bucketDao;
    }

    public RuntimeExceptionDao<Bucket, Integer> getBucketRuntimeDao() {
        if (bucketRuntimeDao == null) {
            bucketRuntimeDao = getRuntimeExceptionDao(Bucket.class);
        }
        return bucketRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        bucketDao = null;
        bucketRuntimeDao = null;
    }
}
