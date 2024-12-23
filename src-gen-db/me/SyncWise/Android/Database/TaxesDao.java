package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.Taxes;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table Taxes.
*/
public class TaxesDao extends AbstractDao<Taxes, Long> {

    public static final String TABLENAME = "Taxes";

    /**
     * Properties of entity Taxes.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TaxCode = new Property(1, String.class, "TaxCode", false, "TaxCode");
        public final static Property TaxName = new Property(2, String.class, "TaxName", false, "TaxName");
        public final static Property TaxAltName = new Property(3, String.class, "TaxAltName", false, "TaxAltName");
        public final static Property TaxPercentage = new Property(4, Double.class, "TaxPercentage", false, "TaxPercentage");
        public final static Property StampDate = new Property(5, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public TaxesDao(DaoConfig config) {
        super(config);
    }
    
    public TaxesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'Taxes' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'TaxCode' TEXT NOT NULL UNIQUE ," + // 1: TaxCode
                "'TaxName' TEXT," + // 2: TaxName
                "'TaxAltName' TEXT," + // 3: TaxAltName
                "'TaxPercentage' REAL," + // 4: TaxPercentage
                "'StampDate' INTEGER NOT NULL );"); // 5: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'Taxes'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Taxes entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTaxCode());
 
        String TaxName = entity.getTaxName();
        if (TaxName != null) {
            stmt.bindString(3, TaxName);
        }
 
        String TaxAltName = entity.getTaxAltName();
        if (TaxAltName != null) {
            stmt.bindString(4, TaxAltName);
        }
 
        Double TaxPercentage = entity.getTaxPercentage();
        if (TaxPercentage != null) {
            stmt.bindDouble(5, TaxPercentage);
        }
        stmt.bindLong(6, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Taxes readEntity(Cursor cursor, int offset) {
        Taxes entity = new Taxes( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // TaxCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // TaxName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // TaxAltName
            cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4), // TaxPercentage
            new java.util.Date(cursor.getLong(offset + 5)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Taxes entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTaxCode(cursor.getString(offset + 1));
        entity.setTaxName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTaxAltName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTaxPercentage(cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Taxes entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Taxes entity) {
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
