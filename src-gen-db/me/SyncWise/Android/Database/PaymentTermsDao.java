package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.PaymentTerms;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PaymentTerms.
*/
public class PaymentTermsDao extends AbstractDao<PaymentTerms, Long> {

    public static final String TABLENAME = "PaymentTerms";

    /**
     * Properties of entity PaymentTerms.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PaymentTermCode = new Property(1, String.class, "PaymentTermCode", false, "PaymentTermCode");
        public final static Property PaymentTermName = new Property(2, String.class, "PaymentTermName", false, "PaymentTermName");
        public final static Property PaymentTermAltName = new Property(3, String.class, "PaymentTermAltName", false, "PaymentTermAltName");
        public final static Property PaymentTermLimit = new Property(4, Integer.class, "PaymentTermLimit", false, "PaymentTermLimit");
        public final static Property StampDate = new Property(5, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public PaymentTermsDao(DaoConfig config) {
        super(config);
    }
    
    public PaymentTermsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PaymentTerms' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PaymentTermCode' TEXT NOT NULL UNIQUE ," + // 1: PaymentTermCode
                "'PaymentTermName' TEXT," + // 2: PaymentTermName
                "'PaymentTermAltName' TEXT," + // 3: PaymentTermAltName
                "'PaymentTermLimit' INTEGER," + // 4: PaymentTermLimit
                "'StampDate' INTEGER NOT NULL );"); // 5: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PaymentTerms'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PaymentTerms entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getPaymentTermCode());
 
        String PaymentTermName = entity.getPaymentTermName();
        if (PaymentTermName != null) {
            stmt.bindString(3, PaymentTermName);
        }
 
        String PaymentTermAltName = entity.getPaymentTermAltName();
        if (PaymentTermAltName != null) {
            stmt.bindString(4, PaymentTermAltName);
        }
 
        Integer PaymentTermLimit = entity.getPaymentTermLimit();
        if (PaymentTermLimit != null) {
            stmt.bindLong(5, PaymentTermLimit);
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
    public PaymentTerms readEntity(Cursor cursor, int offset) {
        PaymentTerms entity = new PaymentTerms( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // PaymentTermCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // PaymentTermName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // PaymentTermAltName
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // PaymentTermLimit
            new java.util.Date(cursor.getLong(offset + 5)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PaymentTerms entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPaymentTermCode(cursor.getString(offset + 1));
        entity.setPaymentTermName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPaymentTermAltName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPaymentTermLimit(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PaymentTerms entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PaymentTerms entity) {
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
