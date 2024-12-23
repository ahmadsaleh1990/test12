package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.Objectives;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table Objectives.
*/
public class ObjectivesDao extends AbstractDao<Objectives, Long> {

    public static final String TABLENAME = "Objectives";

    /**
     * Properties of entity Objectives.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectiveID = new Property(1, Long.class, "ObjectiveID", false, "ObjectiveID");
        public final static Property ObjectiveType = new Property(2, Integer.class, "ObjectiveType", false, "ObjectiveType");
        public final static Property ObjectiveSource = new Property(3, Integer.class, "ObjectiveSource", false, "ObjectiveSource");
        public final static Property VisitID = new Property(4, Long.class, "VisitID", false, "VisitID");
        public final static Property ObjectivePriorityID = new Property(5, Integer.class, "ObjectivePriorityID", false, "ObjectivePriorityID");
        public final static Property ObjectiveDescription = new Property(6, String.class, "ObjectiveDescription", false, "ObjectiveDescription");
        public final static Property ObjectiveAltDescription = new Property(7, String.class, "ObjectiveAltDescription", false, "ObjectiveAltDescription");
        public final static Property StartDate = new Property(8, java.util.Date.class, "StartDate", false, "StartDate");
        public final static Property EndDate = new Property(9, java.util.Date.class, "EndDate", false, "EndDate");
        public final static Property IsProcessed = new Property(10, int.class, "IsProcessed", false, "IsProcessed");
        public final static Property StampDate = new Property(11, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ObjectivesDao(DaoConfig config) {
        super(config);
    }
    
    public ObjectivesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'Objectives' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ObjectiveID' INTEGER," + // 1: ObjectiveID
                "'ObjectiveType' INTEGER," + // 2: ObjectiveType
                "'ObjectiveSource' INTEGER," + // 3: ObjectiveSource
                "'VisitID' INTEGER," + // 4: VisitID
                "'ObjectivePriorityID' INTEGER," + // 5: ObjectivePriorityID
                "'ObjectiveDescription' TEXT," + // 6: ObjectiveDescription
                "'ObjectiveAltDescription' TEXT," + // 7: ObjectiveAltDescription
                "'StartDate' INTEGER," + // 8: StartDate
                "'EndDate' INTEGER," + // 9: EndDate
                "'IsProcessed' INTEGER NOT NULL ," + // 10: IsProcessed
                "'StampDate' INTEGER NOT NULL );"); // 11: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'Objectives'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Objectives entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long ObjectiveID = entity.getObjectiveID();
        if (ObjectiveID != null) {
            stmt.bindLong(2, ObjectiveID);
        }
 
        Integer ObjectiveType = entity.getObjectiveType();
        if (ObjectiveType != null) {
            stmt.bindLong(3, ObjectiveType);
        }
 
        Integer ObjectiveSource = entity.getObjectiveSource();
        if (ObjectiveSource != null) {
            stmt.bindLong(4, ObjectiveSource);
        }
 
        Long VisitID = entity.getVisitID();
        if (VisitID != null) {
            stmt.bindLong(5, VisitID);
        }
 
        Integer ObjectivePriorityID = entity.getObjectivePriorityID();
        if (ObjectivePriorityID != null) {
            stmt.bindLong(6, ObjectivePriorityID);
        }
 
        String ObjectiveDescription = entity.getObjectiveDescription();
        if (ObjectiveDescription != null) {
            stmt.bindString(7, ObjectiveDescription);
        }
 
        String ObjectiveAltDescription = entity.getObjectiveAltDescription();
        if (ObjectiveAltDescription != null) {
            stmt.bindString(8, ObjectiveAltDescription);
        }
 
        java.util.Date StartDate = entity.getStartDate();
        if (StartDate != null) {
            stmt.bindLong(9, StartDate.getTime());
        }
 
        java.util.Date EndDate = entity.getEndDate();
        if (EndDate != null) {
            stmt.bindLong(10, EndDate.getTime());
        }
        stmt.bindLong(11, entity.getIsProcessed());
        stmt.bindLong(12, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Objectives readEntity(Cursor cursor, int offset) {
        Objectives entity = new Objectives( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // ObjectiveID
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // ObjectiveType
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // ObjectiveSource
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // VisitID
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // ObjectivePriorityID
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // ObjectiveDescription
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // ObjectiveAltDescription
            cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // StartDate
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // EndDate
            cursor.getInt(offset + 10), // IsProcessed
            new java.util.Date(cursor.getLong(offset + 11)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Objectives entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectiveID(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setObjectiveType(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setObjectiveSource(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setVisitID(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setObjectivePriorityID(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setObjectiveDescription(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setObjectiveAltDescription(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setStartDate(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setEndDate(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setIsProcessed(cursor.getInt(offset + 10));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 11)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Objectives entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Objectives entity) {
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
