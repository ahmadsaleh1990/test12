package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.MsgHeader;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MsgHeader.
*/
public class MsgHeaderDao extends AbstractDao<MsgHeader, Long> {

    public static final String TABLENAME = "MsgHeader";

    /**
     * Properties of entity MsgHeader.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MsgCode = new Property(1, String.class, "MsgCode", false, "MsgCode");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property SubjectCode = new Property(3, String.class, "SubjectCode", false, "SubjectCode");
        public final static Property MsgTitle = new Property(4, String.class, "MsgTitle", false, "MsgTitle");
        public final static Property MsgBody = new Property(5, String.class, "MsgBody", false, "MsgBody");
        public final static Property MsgDate = new Property(6, java.util.Date.class, "MsgDate", false, "MsgDate");
        public final static Property StartDate = new Property(7, java.util.Date.class, "StartDate", false, "StartDate");
        public final static Property EndDate = new Property(8, java.util.Date.class, "EndDate", false, "EndDate");
        public final static Property IsActive = new Property(9, String.class, "IsActive", false, "IsActive");
        public final static Property MsgType = new Property(10, String.class, "MsgType", false, "MsgType");
    };


    public MsgHeaderDao(DaoConfig config) {
        super(config);
    }
    
    public MsgHeaderDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MsgHeader' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'MsgCode' TEXT NOT NULL UNIQUE ," + // 1: MsgCode
                "'CompanyCode' TEXT," + // 2: CompanyCode
                "'SubjectCode' TEXT," + // 3: SubjectCode
                "'MsgTitle' TEXT," + // 4: MsgTitle
                "'MsgBody' TEXT," + // 5: MsgBody
                "'MsgDate' INTEGER," + // 6: MsgDate
                "'StartDate' INTEGER," + // 7: StartDate
                "'EndDate' INTEGER," + // 8: EndDate
                "'IsActive' TEXT," + // 9: IsActive
                "'MsgType' TEXT);"); // 10: MsgType
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MsgHeader'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MsgHeader entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getMsgCode());
 
        String CompanyCode = entity.getCompanyCode();
        if (CompanyCode != null) {
            stmt.bindString(3, CompanyCode);
        }
 
        String SubjectCode = entity.getSubjectCode();
        if (SubjectCode != null) {
            stmt.bindString(4, SubjectCode);
        }
 
        String MsgTitle = entity.getMsgTitle();
        if (MsgTitle != null) {
            stmt.bindString(5, MsgTitle);
        }
 
        String MsgBody = entity.getMsgBody();
        if (MsgBody != null) {
            stmt.bindString(6, MsgBody);
        }
 
        java.util.Date MsgDate = entity.getMsgDate();
        if (MsgDate != null) {
            stmt.bindLong(7, MsgDate.getTime());
        }
 
        java.util.Date StartDate = entity.getStartDate();
        if (StartDate != null) {
            stmt.bindLong(8, StartDate.getTime());
        }
 
        java.util.Date EndDate = entity.getEndDate();
        if (EndDate != null) {
            stmt.bindLong(9, EndDate.getTime());
        }
 
        String IsActive = entity.getIsActive();
        if (IsActive != null) {
            stmt.bindString(10, IsActive);
        }
 
        String MsgType = entity.getMsgType();
        if (MsgType != null) {
            stmt.bindString(11, MsgType);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public MsgHeader readEntity(Cursor cursor, int offset) {
        MsgHeader entity = new MsgHeader( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // MsgCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // CompanyCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // SubjectCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // MsgTitle
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // MsgBody
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // MsgDate
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)), // StartDate
            cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // EndDate
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // IsActive
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // MsgType
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MsgHeader entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMsgCode(cursor.getString(offset + 1));
        entity.setCompanyCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSubjectCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMsgTitle(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMsgBody(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setMsgDate(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setStartDate(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
        entity.setEndDate(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setIsActive(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setMsgType(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MsgHeader entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MsgHeader entity) {
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