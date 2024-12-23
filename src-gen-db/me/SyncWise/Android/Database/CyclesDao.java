package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.Cycles;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table Cycles.
*/
public class CyclesDao extends AbstractDao<Cycles, Long> {

    public static final String TABLENAME = "Cycles";

    /**
     * Properties of entity Cycles.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CycleID = new Property(1, int.class, "CycleID", false, "CycleID");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property CycleName = new Property(3, String.class, "CycleName", false, "CycleName");
        public final static Property CycleAltName = new Property(4, String.class, "CycleAltName", false, "CycleAltName");
        public final static Property CoefficientNumber = new Property(5, Integer.class, "CoefficientNumber", false, "CoefficientNumber");
        public final static Property CycleStatus = new Property(6, Integer.class, "CycleStatus", false, "CycleStatus");
        public final static Property IsProcessed = new Property(7, int.class, "IsProcessed", false, "IsProcessed");
        public final static Property StampDate = new Property(8, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public CyclesDao(DaoConfig config) {
        super(config);
    }
    
    public CyclesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'Cycles' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'CycleID' INTEGER NOT NULL UNIQUE ," + // 1: CycleID
                "'CompanyCode' TEXT," + // 2: CompanyCode
                "'CycleName' TEXT," + // 3: CycleName
                "'CycleAltName' TEXT," + // 4: CycleAltName
                "'CoefficientNumber' INTEGER," + // 5: CoefficientNumber
                "'CycleStatus' INTEGER," + // 6: CycleStatus
                "'IsProcessed' INTEGER NOT NULL ," + // 7: IsProcessed
                "'StampDate' INTEGER NOT NULL );"); // 8: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'Cycles'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Cycles entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getCycleID());
 
        String CompanyCode = entity.getCompanyCode();
        if (CompanyCode != null) {
            stmt.bindString(3, CompanyCode);
        }
 
        String CycleName = entity.getCycleName();
        if (CycleName != null) {
            stmt.bindString(4, CycleName);
        }
 
        String CycleAltName = entity.getCycleAltName();
        if (CycleAltName != null) {
            stmt.bindString(5, CycleAltName);
        }
 
        Integer CoefficientNumber = entity.getCoefficientNumber();
        if (CoefficientNumber != null) {
            stmt.bindLong(6, CoefficientNumber);
        }
 
        Integer CycleStatus = entity.getCycleStatus();
        if (CycleStatus != null) {
            stmt.bindLong(7, CycleStatus);
        }
        stmt.bindLong(8, entity.getIsProcessed());
        stmt.bindLong(9, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Cycles readEntity(Cursor cursor, int offset) {
        Cycles entity = new Cycles( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // CycleID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // CompanyCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // CycleName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // CycleAltName
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // CoefficientNumber
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // CycleStatus
            cursor.getInt(offset + 7), // IsProcessed
            new java.util.Date(cursor.getLong(offset + 8)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Cycles entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCycleID(cursor.getInt(offset + 1));
        entity.setCompanyCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCycleName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCycleAltName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCoefficientNumber(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setCycleStatus(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setIsProcessed(cursor.getInt(offset + 7));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 8)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Cycles entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Cycles entity) {
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
