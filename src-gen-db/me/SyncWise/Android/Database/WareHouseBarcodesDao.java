package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.WareHouseBarcodes;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table WareHouseBarcodes.
*/
public class WareHouseBarcodesDao extends AbstractDao<WareHouseBarcodes, Long> {

    public static final String TABLENAME = "WareHouseBarcodes";

    /**
     * Properties of entity WareHouseBarcodes.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property WareHouseCode = new Property(1, String.class, "WareHouseCode", false, "WareHouseCode");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property Barcode = new Property(4, String.class, "Barcode", false, "Barcode");
        public final static Property StampDate = new Property(5, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public WareHouseBarcodesDao(DaoConfig config) {
        super(config);
    }
    
    public WareHouseBarcodesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'WareHouseBarcodes' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'WareHouseCode' TEXT NOT NULL ," + // 1: WareHouseCode
                "'CompanyCode' TEXT NOT NULL ," + // 2: CompanyCode
                "'DivisionCode' TEXT NOT NULL ," + // 3: DivisionCode
                "'Barcode' TEXT NOT NULL ," + // 4: Barcode
                "'StampDate' INTEGER NOT NULL );"); // 5: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'WareHouseBarcodes'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, WareHouseBarcodes entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getWareHouseCode());
        stmt.bindString(3, entity.getCompanyCode());
        stmt.bindString(4, entity.getDivisionCode());
        stmt.bindString(5, entity.getBarcode());
        stmt.bindLong(6, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public WareHouseBarcodes readEntity(Cursor cursor, int offset) {
        WareHouseBarcodes entity = new WareHouseBarcodes( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // WareHouseCode
            cursor.getString(offset + 2), // CompanyCode
            cursor.getString(offset + 3), // DivisionCode
            cursor.getString(offset + 4), // Barcode
            new java.util.Date(cursor.getLong(offset + 5)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, WareHouseBarcodes entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setWareHouseCode(cursor.getString(offset + 1));
        entity.setCompanyCode(cursor.getString(offset + 2));
        entity.setDivisionCode(cursor.getString(offset + 3));
        entity.setBarcode(cursor.getString(offset + 4));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(WareHouseBarcodes entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(WareHouseBarcodes entity) {
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
