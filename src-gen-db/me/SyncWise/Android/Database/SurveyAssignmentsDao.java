package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.SurveyAssignments;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SurveyAssignments.
*/
public class SurveyAssignmentsDao extends AbstractDao<SurveyAssignments, Long> {

    public static final String TABLENAME = "SurveyAssignments";

    /**
     * Properties of entity SurveyAssignments.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property SurveyID = new Property(1, long.class, "SurveyID", false, "SurveyID");
        public final static Property AssignmentCode = new Property(2, String.class, "AssignmentCode", false, "AssignmentCode");
        public final static Property AssignmentType = new Property(3, int.class, "AssignmentType", false, "AssignmentType");
        public final static Property StampDate = new Property(4, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public SurveyAssignmentsDao(DaoConfig config) {
        super(config);
    }
    
    public SurveyAssignmentsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SurveyAssignments' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'SurveyID' INTEGER NOT NULL ," + // 1: SurveyID
                "'AssignmentCode' TEXT NOT NULL ," + // 2: AssignmentCode
                "'AssignmentType' INTEGER NOT NULL ," + // 3: AssignmentType
                "'StampDate' INTEGER NOT NULL );"); // 4: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SurveyAssignments'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SurveyAssignments entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSurveyID());
        stmt.bindString(3, entity.getAssignmentCode());
        stmt.bindLong(4, entity.getAssignmentType());
        stmt.bindLong(5, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SurveyAssignments readEntity(Cursor cursor, int offset) {
        SurveyAssignments entity = new SurveyAssignments( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // SurveyID
            cursor.getString(offset + 2), // AssignmentCode
            cursor.getInt(offset + 3), // AssignmentType
            new java.util.Date(cursor.getLong(offset + 4)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SurveyAssignments entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSurveyID(cursor.getLong(offset + 1));
        entity.setAssignmentCode(cursor.getString(offset + 2));
        entity.setAssignmentType(cursor.getInt(offset + 3));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SurveyAssignments entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SurveyAssignments entity) {
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
