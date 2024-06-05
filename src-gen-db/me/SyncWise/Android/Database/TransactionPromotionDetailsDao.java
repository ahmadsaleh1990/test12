package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.TransactionPromotionDetails;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table TransactionPromotionDetails.
*/
public class TransactionPromotionDetailsDao extends AbstractDao<TransactionPromotionDetails, Long> {

    public static final String TABLENAME = "TransactionPromotionDetails";

    /**
     * Properties of entity TransactionPromotionDetails.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TransactionCode = new Property(1, String.class, "TransactionCode", false, "TransactionCode");
        public final static Property LineID = new Property(2, int.class, "LineID", false, "LineID");
        public final static Property ParentLineID = new Property(3, int.class, "ParentLineID", false, "ParentLineID");
        public final static Property ItemCodeOrdered = new Property(4, String.class, "ItemCodeOrdered", false, "ItemCodeOrdered");
        public final static Property QuantityOrdered = new Property(5, Double.class, "QuantityOrdered", false, "QuantityOrdered");
        public final static Property ItemCodeOffered = new Property(6, String.class, "ItemCodeOffered", false, "ItemCodeOffered");
        public final static Property QuantityOffered = new Property(7, Double.class, "QuantityOffered", false, "QuantityOffered");
        public final static Property PromotionID = new Property(8, Integer.class, "PromotionID", false, "PromotionID");
        public final static Property PromotionHeaderType = new Property(9, Integer.class, "PromotionHeaderType", false, "PromotionHeaderType");
        public final static Property PromotionType = new Property(10, Integer.class, "PromotionType", false, "PromotionType");
        public final static Property DiscountPercentage = new Property(11, Double.class, "DiscountPercentage", false, "DiscountPercentage");
        public final static Property DiscountAmount = new Property(12, Double.class, "DiscountAmount", false, "DiscountAmount");
        public final static Property TotalLineAmount = new Property(13, Double.class, "TotalLineAmount", false, "TotalLineAmount");
        public final static Property StampDate = new Property(14, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public TransactionPromotionDetailsDao(DaoConfig config) {
        super(config);
    }
    
    public TransactionPromotionDetailsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'TransactionPromotionDetails' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'TransactionCode' TEXT NOT NULL ," + // 1: TransactionCode
                "'LineID' INTEGER NOT NULL ," + // 2: LineID
                "'ParentLineID' INTEGER NOT NULL ," + // 3: ParentLineID
                "'ItemCodeOrdered' TEXT," + // 4: ItemCodeOrdered
                "'QuantityOrdered' REAL," + // 5: QuantityOrdered
                "'ItemCodeOffered' TEXT," + // 6: ItemCodeOffered
                "'QuantityOffered' REAL," + // 7: QuantityOffered
                "'PromotionID' INTEGER," + // 8: PromotionID
                "'PromotionHeaderType' INTEGER," + // 9: PromotionHeaderType
                "'PromotionType' INTEGER," + // 10: PromotionType
                "'DiscountPercentage' REAL," + // 11: DiscountPercentage
                "'DiscountAmount' REAL," + // 12: DiscountAmount
                "'TotalLineAmount' REAL," + // 13: TotalLineAmount
                "'StampDate' INTEGER NOT NULL );"); // 14: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'TransactionPromotionDetails'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TransactionPromotionDetails entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTransactionCode());
        stmt.bindLong(3, entity.getLineID());
        stmt.bindLong(4, entity.getParentLineID());
 
        String ItemCodeOrdered = entity.getItemCodeOrdered();
        if (ItemCodeOrdered != null) {
            stmt.bindString(5, ItemCodeOrdered);
        }
 
        Double QuantityOrdered = entity.getQuantityOrdered();
        if (QuantityOrdered != null) {
            stmt.bindDouble(6, QuantityOrdered);
        }
 
        String ItemCodeOffered = entity.getItemCodeOffered();
        if (ItemCodeOffered != null) {
            stmt.bindString(7, ItemCodeOffered);
        }
 
        Double QuantityOffered = entity.getQuantityOffered();
        if (QuantityOffered != null) {
            stmt.bindDouble(8, QuantityOffered);
        }
 
        Integer PromotionID = entity.getPromotionID();
        if (PromotionID != null) {
            stmt.bindLong(9, PromotionID);
        }
 
        Integer PromotionHeaderType = entity.getPromotionHeaderType();
        if (PromotionHeaderType != null) {
            stmt.bindLong(10, PromotionHeaderType);
        }
 
        Integer PromotionType = entity.getPromotionType();
        if (PromotionType != null) {
            stmt.bindLong(11, PromotionType);
        }
 
        Double DiscountPercentage = entity.getDiscountPercentage();
        if (DiscountPercentage != null) {
            stmt.bindDouble(12, DiscountPercentage);
        }
 
        Double DiscountAmount = entity.getDiscountAmount();
        if (DiscountAmount != null) {
            stmt.bindDouble(13, DiscountAmount);
        }
 
        Double TotalLineAmount = entity.getTotalLineAmount();
        if (TotalLineAmount != null) {
            stmt.bindDouble(14, TotalLineAmount);
        }
        stmt.bindLong(15, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public TransactionPromotionDetails readEntity(Cursor cursor, int offset) {
        TransactionPromotionDetails entity = new TransactionPromotionDetails( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // TransactionCode
            cursor.getInt(offset + 2), // LineID
            cursor.getInt(offset + 3), // ParentLineID
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // ItemCodeOrdered
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // QuantityOrdered
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // ItemCodeOffered
            cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7), // QuantityOffered
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // PromotionID
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // PromotionHeaderType
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // PromotionType
            cursor.isNull(offset + 11) ? null : cursor.getDouble(offset + 11), // DiscountPercentage
            cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12), // DiscountAmount
            cursor.isNull(offset + 13) ? null : cursor.getDouble(offset + 13), // TotalLineAmount
            new java.util.Date(cursor.getLong(offset + 14)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TransactionPromotionDetails entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTransactionCode(cursor.getString(offset + 1));
        entity.setLineID(cursor.getInt(offset + 2));
        entity.setParentLineID(cursor.getInt(offset + 3));
        entity.setItemCodeOrdered(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setQuantityOrdered(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setItemCodeOffered(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setQuantityOffered(cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7));
        entity.setPromotionID(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setPromotionHeaderType(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setPromotionType(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setDiscountPercentage(cursor.isNull(offset + 11) ? null : cursor.getDouble(offset + 11));
        entity.setDiscountAmount(cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12));
        entity.setTotalLineAmount(cursor.isNull(offset + 13) ? null : cursor.getDouble(offset + 13));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 14)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(TransactionPromotionDetails entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(TransactionPromotionDetails entity) {
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