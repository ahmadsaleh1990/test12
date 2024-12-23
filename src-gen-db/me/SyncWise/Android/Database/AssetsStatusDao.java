package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.AssetsStatus;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table AssetsStatus.
*/
public class AssetsStatusDao extends AbstractDao<AssetsStatus, Long> {

    public static final String TABLENAME = "AssetsStatus";

    /**
     * Properties of entity AssetsStatus.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property StatusID = new Property(1, int.class, "StatusID", false, "StatusID");
        public final static Property StatusType = new Property(2, int.class, "StatusType", false, "StatusType");
        public final static Property SeqNo = new Property(3, Integer.class, "SeqNo", false, "SeqNo");
        public final static Property StatusName = new Property(4, String.class, "StatusName", false, "StatusName");
        public final static Property StatusAltName = new Property(5, String.class, "StatusAltName", false, "StatusAltName");
        public final static Property StampDate = new Property(6, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public AssetsStatusDao(DaoConfig config) {
        super(config);
    }
    
    public AssetsStatusDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'AssetsStatus' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'StatusID' INTEGER NOT NULL ," + // 1: StatusID
                "'StatusType' INTEGER NOT NULL ," + // 2: StatusType
                "'SeqNo' INTEGER," + // 3: SeqNo
                "'StatusName' TEXT," + // 4: StatusName
                "'StatusAltName' TEXT," + // 5: StatusAltName
                "'StampDate' INTEGER NOT NULL );"); // 6: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'AssetsStatus'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AssetsStatus entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getStatusID());
        stmt.bindLong(3, entity.getStatusType());
 
        Integer SeqNo = entity.getSeqNo();
        if (SeqNo != null) {
            stmt.bindLong(4, SeqNo);
        }
 
        String StatusName = entity.getStatusName();
        if (StatusName != null) {
            stmt.bindString(5, StatusName);
        }
 
        String StatusAltName = entity.getStatusAltName();
        if (StatusAltName != null) {
            stmt.bindString(6, StatusAltName);
        }
        stmt.bindLong(7, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public AssetsStatus readEntity(Cursor cursor, int offset) {
        AssetsStatus entity = new AssetsStatus( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // StatusID
            cursor.getInt(offset + 2), // StatusType
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // SeqNo
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // StatusName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // StatusAltName
            new java.util.Date(cursor.getLong(offset + 6)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AssetsStatus entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStatusID(cursor.getInt(offset + 1));
        entity.setStatusType(cursor.getInt(offset + 2));
        entity.setSeqNo(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setStatusName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStatusAltName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(AssetsStatus entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(AssetsStatus entity) {
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
