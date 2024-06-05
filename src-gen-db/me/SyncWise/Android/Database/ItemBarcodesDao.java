package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ItemBarcodes;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ItemBarcodes.
*/
public class ItemBarcodesDao extends AbstractDao<ItemBarcodes, Long> {

    public static final String TABLENAME = "ItemBarcodes";

    /**
     * Properties of entity ItemBarcodes.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ItemCode = new Property(1, String.class, "ItemCode", false, "ItemCode");
        public final static Property ItemBarcode = new Property(2, String.class, "ItemBarcode", false, "ItemBarcode");
        public final static Property CompanyCode = new Property(3, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property StampDate = new Property(4, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ItemBarcodesDao(DaoConfig config) {
        super(config);
    }
    
    public ItemBarcodesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ItemBarcodes' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ItemCode' TEXT NOT NULL ," + // 1: ItemCode
                "'ItemBarcode' TEXT NOT NULL ," + // 2: ItemBarcode
                "'CompanyCode' TEXT NOT NULL ," + // 3: CompanyCode
                "'StampDate' INTEGER NOT NULL );"); // 4: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ItemBarcodes'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ItemBarcodes entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getItemCode());
        stmt.bindString(3, entity.getItemBarcode());
        stmt.bindString(4, entity.getCompanyCode());
        stmt.bindLong(5, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ItemBarcodes readEntity(Cursor cursor, int offset) {
        ItemBarcodes entity = new ItemBarcodes( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // ItemCode
            cursor.getString(offset + 2), // ItemBarcode
            cursor.getString(offset + 3), // CompanyCode
            new java.util.Date(cursor.getLong(offset + 4)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ItemBarcodes entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setItemCode(cursor.getString(offset + 1));
        entity.setItemBarcode(cursor.getString(offset + 2));
        entity.setCompanyCode(cursor.getString(offset + 3));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ItemBarcodes entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ItemBarcodes entity) {
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