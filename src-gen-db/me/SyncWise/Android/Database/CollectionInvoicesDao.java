package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.CollectionInvoices;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CollectionInvoices.
*/
public class CollectionInvoicesDao extends AbstractDao<CollectionInvoices, Long> {

    public static final String TABLENAME = "CollectionInvoices";

    /**
     * Properties of entity CollectionInvoices.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CollectionCode = new Property(1, String.class, "CollectionCode", false, "CollectionCode");
        public final static Property InvoiceCode = new Property(2, String.class, "InvoiceCode", false, "InvoiceCode");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property CompanyCode = new Property(4, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property TotalAmount = new Property(5, Double.class, "TotalAmount", false, "TotalAmount");
        public final static Property CurrencyCode = new Property(6, String.class, "CurrencyCode", false, "CurrencyCode");
        public final static Property InvoiceSource = new Property(7, Integer.class, "InvoiceSource", false, "InvoiceSource");
        public final static Property CurrencyRate = new Property(8, Double.class, "CurrencyRate", false, "CurrencyRate");
        public final static Property InvoiceDate = new Property(9, java.util.Date.class, "InvoiceDate", false, "InvoiceDate");
        public final static Property StampDate = new Property(10, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public CollectionInvoicesDao(DaoConfig config) {
        super(config);
    }
    
    public CollectionInvoicesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CollectionInvoices' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'CollectionCode' TEXT NOT NULL ," + // 1: CollectionCode
                "'InvoiceCode' TEXT NOT NULL ," + // 2: InvoiceCode
                "'DivisionCode' TEXT NOT NULL ," + // 3: DivisionCode
                "'CompanyCode' TEXT NOT NULL ," + // 4: CompanyCode
                "'TotalAmount' REAL," + // 5: TotalAmount
                "'CurrencyCode' TEXT," + // 6: CurrencyCode
                "'InvoiceSource' INTEGER," + // 7: InvoiceSource
                "'CurrencyRate' REAL," + // 8: CurrencyRate
                "'InvoiceDate' INTEGER," + // 9: InvoiceDate
                "'StampDate' INTEGER NOT NULL );"); // 10: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CollectionInvoices'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CollectionInvoices entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getCollectionCode());
        stmt.bindString(3, entity.getInvoiceCode());
        stmt.bindString(4, entity.getDivisionCode());
        stmt.bindString(5, entity.getCompanyCode());
 
        Double TotalAmount = entity.getTotalAmount();
        if (TotalAmount != null) {
            stmt.bindDouble(6, TotalAmount);
        }
 
        String CurrencyCode = entity.getCurrencyCode();
        if (CurrencyCode != null) {
            stmt.bindString(7, CurrencyCode);
        }
 
        Integer InvoiceSource = entity.getInvoiceSource();
        if (InvoiceSource != null) {
            stmt.bindLong(8, InvoiceSource);
        }
 
        Double CurrencyRate = entity.getCurrencyRate();
        if (CurrencyRate != null) {
            stmt.bindDouble(9, CurrencyRate);
        }
 
        java.util.Date InvoiceDate = entity.getInvoiceDate();
        if (InvoiceDate != null) {
            stmt.bindLong(10, InvoiceDate.getTime());
        }
        stmt.bindLong(11, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CollectionInvoices readEntity(Cursor cursor, int offset) {
        CollectionInvoices entity = new CollectionInvoices( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // CollectionCode
            cursor.getString(offset + 2), // InvoiceCode
            cursor.getString(offset + 3), // DivisionCode
            cursor.getString(offset + 4), // CompanyCode
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // TotalAmount
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // CurrencyCode
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // InvoiceSource
            cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8), // CurrencyRate
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // InvoiceDate
            new java.util.Date(cursor.getLong(offset + 10)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CollectionInvoices entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCollectionCode(cursor.getString(offset + 1));
        entity.setInvoiceCode(cursor.getString(offset + 2));
        entity.setDivisionCode(cursor.getString(offset + 3));
        entity.setCompanyCode(cursor.getString(offset + 4));
        entity.setTotalAmount(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setCurrencyCode(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setInvoiceSource(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setCurrencyRate(cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8));
        entity.setInvoiceDate(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 10)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CollectionInvoices entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CollectionInvoices entity) {
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
