package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ObjectivePriorities;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ObjectivePriorities.
*/
public class ObjectivePrioritiesDao extends AbstractDao<ObjectivePriorities, Long> {

    public static final String TABLENAME = "ObjectivePriorities";

    /**
     * Properties of entity ObjectivePriorities.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectivePriorityID = new Property(1, Integer.class, "ObjectivePriorityID", false, "ObjectivePriorityID");
        public final static Property ObjectivePriorityDescription = new Property(2, String.class, "ObjectivePriorityDescription", false, "ObjectivePriorityDescription");
        public final static Property ObjectivePriorityAltDescription = new Property(3, String.class, "ObjectivePriorityAltDescription", false, "ObjectivePriorityAltDescription");
        public final static Property StampDate = new Property(4, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ObjectivePrioritiesDao(DaoConfig config) {
        super(config);
    }
    
    public ObjectivePrioritiesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ObjectivePriorities' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ObjectivePriorityID' INTEGER," + // 1: ObjectivePriorityID
                "'ObjectivePriorityDescription' TEXT," + // 2: ObjectivePriorityDescription
                "'ObjectivePriorityAltDescription' TEXT," + // 3: ObjectivePriorityAltDescription
                "'StampDate' INTEGER NOT NULL );"); // 4: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ObjectivePriorities'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ObjectivePriorities entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer ObjectivePriorityID = entity.getObjectivePriorityID();
        if (ObjectivePriorityID != null) {
            stmt.bindLong(2, ObjectivePriorityID);
        }
 
        String ObjectivePriorityDescription = entity.getObjectivePriorityDescription();
        if (ObjectivePriorityDescription != null) {
            stmt.bindString(3, ObjectivePriorityDescription);
        }
 
        String ObjectivePriorityAltDescription = entity.getObjectivePriorityAltDescription();
        if (ObjectivePriorityAltDescription != null) {
            stmt.bindString(4, ObjectivePriorityAltDescription);
        }
        stmt.bindLong(5, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ObjectivePriorities readEntity(Cursor cursor, int offset) {
        ObjectivePriorities entity = new ObjectivePriorities( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // ObjectivePriorityID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ObjectivePriorityDescription
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ObjectivePriorityAltDescription
            new java.util.Date(cursor.getLong(offset + 4)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ObjectivePriorities entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectivePriorityID(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setObjectivePriorityDescription(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setObjectivePriorityAltDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ObjectivePriorities entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ObjectivePriorities entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}