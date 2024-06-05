package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.Reasons;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table Reasons.
*/
public class ReasonsDao extends AbstractDao<Reasons, Long> {

    public static final String TABLENAME = "Reasons";

    /**
     * Properties of entity Reasons.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ReasonCode = new Property(1, String.class, "ReasonCode", false, "ReasonCode");
        public final static Property ReasonType = new Property(2, String.class, "ReasonType", false, "ReasonType");
        public final static Property ReasonName = new Property(3, String.class, "ReasonName", false, "ReasonName");
        public final static Property ReasonAltName = new Property(4, String.class, "ReasonAltName", false, "ReasonAltName");
        public final static Property ReasonDisplaySequence = new Property(5, Integer.class, "ReasonDisplaySequence", false, "ReasonDisplaySequence");
        public final static Property ReasonAffectStock = new Property(6, String.class, "ReasonAffectStock", false, "ReasonAffectStock");
        public final static Property WarehouseCode = new Property(7, String.class, "WarehouseCode", false, "WarehouseCode");
        public final static Property AccountNumber = new Property(8, String.class, "AccountNumber", false, "AccountNumber");
        public final static Property StampDate = new Property(9, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ReasonsDao(DaoConfig config) {
        super(config);
    }
    
    public ReasonsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'Reasons' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ReasonCode' TEXT NOT NULL UNIQUE ," + // 1: ReasonCode
                "'ReasonType' TEXT," + // 2: ReasonType
                "'ReasonName' TEXT," + // 3: ReasonName
                "'ReasonAltName' TEXT," + // 4: ReasonAltName
                "'ReasonDisplaySequence' INTEGER," + // 5: ReasonDisplaySequence
                "'ReasonAffectStock' TEXT," + // 6: ReasonAffectStock
                "'WarehouseCode' TEXT," + // 7: WarehouseCode
                "'AccountNumber' TEXT," + // 8: AccountNumber
                "'StampDate' INTEGER NOT NULL );"); // 9: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'Reasons'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Reasons entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getReasonCode());
 
        String ReasonType = entity.getReasonType();
        if (ReasonType != null) {
            stmt.bindString(3, ReasonType);
        }
 
        String ReasonName = entity.getReasonName();
        if (ReasonName != null) {
            stmt.bindString(4, ReasonName);
        }
 
        String ReasonAltName = entity.getReasonAltName();
        if (ReasonAltName != null) {
            stmt.bindString(5, ReasonAltName);
        }
 
        Integer ReasonDisplaySequence = entity.getReasonDisplaySequence();
        if (ReasonDisplaySequence != null) {
            stmt.bindLong(6, ReasonDisplaySequence);
        }
 
        String ReasonAffectStock = entity.getReasonAffectStock();
        if (ReasonAffectStock != null) {
            stmt.bindString(7, ReasonAffectStock);
        }
 
        String WarehouseCode = entity.getWarehouseCode();
        if (WarehouseCode != null) {
            stmt.bindString(8, WarehouseCode);
        }
 
        String AccountNumber = entity.getAccountNumber();
        if (AccountNumber != null) {
            stmt.bindString(9, AccountNumber);
        }
        stmt.bindLong(10, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Reasons readEntity(Cursor cursor, int offset) {
        Reasons entity = new Reasons( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // ReasonCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ReasonType
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ReasonName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // ReasonAltName
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // ReasonDisplaySequence
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // ReasonAffectStock
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // WarehouseCode
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // AccountNumber
            new java.util.Date(cursor.getLong(offset + 9)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Reasons entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setReasonCode(cursor.getString(offset + 1));
        entity.setReasonType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setReasonName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setReasonAltName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setReasonDisplaySequence(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setReasonAffectStock(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setWarehouseCode(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAccountNumber(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 9)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Reasons entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Reasons entity) {
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