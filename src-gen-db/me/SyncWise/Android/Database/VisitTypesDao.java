package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.VisitTypes;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table VisitTypes.
*/
public class VisitTypesDao extends AbstractDao<VisitTypes, Long> {

    public static final String TABLENAME = "VisitTypes";

    /**
     * Properties of entity VisitTypes.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property VisitID = new Property(1, long.class, "VisitID", false, "VisitID");
        public final static Property VisitTypeID = new Property(2, Integer.class, "VisitTypeID", false, "VisitTypeID");
        public final static Property StampDate = new Property(3, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public VisitTypesDao(DaoConfig config) {
        super(config);
    }
    
    public VisitTypesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'VisitTypes' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'VisitID' INTEGER NOT NULL UNIQUE ," + // 1: VisitID
                "'VisitTypeID' INTEGER," + // 2: VisitTypeID
                "'StampDate' INTEGER NOT NULL );"); // 3: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'VisitTypes'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, VisitTypes entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getVisitID());
 
        Integer VisitTypeID = entity.getVisitTypeID();
        if (VisitTypeID != null) {
            stmt.bindLong(3, VisitTypeID);
        }
        stmt.bindLong(4, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public VisitTypes readEntity(Cursor cursor, int offset) {
        VisitTypes entity = new VisitTypes( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // VisitID
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // VisitTypeID
            new java.util.Date(cursor.getLong(offset + 3)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, VisitTypes entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setVisitID(cursor.getLong(offset + 1));
        entity.setVisitTypeID(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 3)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(VisitTypes entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(VisitTypes entity) {
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