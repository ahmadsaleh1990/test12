package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ItemDivisions;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ItemDivisions.
*/
public class ItemDivisionsDao extends AbstractDao<ItemDivisions, Long> {

    public static final String TABLENAME = "ItemDivisions";

    /**
     * Properties of entity ItemDivisions.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ItemCode = new Property(1, String.class, "ItemCode", false, "ItemCode");
        public final static Property DivisionCode = new Property(2, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property CompanyCode = new Property(3, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property StampDate = new Property(4, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ItemDivisionsDao(DaoConfig config) {
        super(config);
    }
    
    public ItemDivisionsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ItemDivisions' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ItemCode' TEXT NOT NULL ," + // 1: ItemCode
                "'DivisionCode' TEXT NOT NULL ," + // 2: DivisionCode
                "'CompanyCode' TEXT NOT NULL ," + // 3: CompanyCode
                "'StampDate' INTEGER NOT NULL );"); // 4: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ItemDivisions'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ItemDivisions entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getItemCode());
        stmt.bindString(3, entity.getDivisionCode());
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
    public ItemDivisions readEntity(Cursor cursor, int offset) {
        ItemDivisions entity = new ItemDivisions( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // ItemCode
            cursor.getString(offset + 2), // DivisionCode
            cursor.getString(offset + 3), // CompanyCode
            new java.util.Date(cursor.getLong(offset + 4)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ItemDivisions entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setItemCode(cursor.getString(offset + 1));
        entity.setDivisionCode(cursor.getString(offset + 2));
        entity.setCompanyCode(cursor.getString(offset + 3));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ItemDivisions entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ItemDivisions entity) {
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
