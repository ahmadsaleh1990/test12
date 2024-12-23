package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ClientItemHistory;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ClientItemHistory.
*/
public class ClientItemHistoryDao extends AbstractDao<ClientItemHistory, Long> {

    public static final String TABLENAME = "ClientItemHistory";

    /**
     * Properties of entity ClientItemHistory.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ClientCode = new Property(1, String.class, "ClientCode", false, "ClientCode");
        public final static Property ItemCode = new Property(2, String.class, "ItemCode", false, "ItemCode");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property CompanyCode = new Property(4, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property StampDate = new Property(5, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ClientItemHistoryDao(DaoConfig config) {
        super(config);
    }
    
    public ClientItemHistoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ClientItemHistory' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ClientCode' TEXT NOT NULL ," + // 1: ClientCode
                "'ItemCode' TEXT NOT NULL ," + // 2: ItemCode
                "'DivisionCode' TEXT NOT NULL ," + // 3: DivisionCode
                "'CompanyCode' TEXT NOT NULL ," + // 4: CompanyCode
                "'StampDate' INTEGER NOT NULL );"); // 5: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ClientItemHistory'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClientItemHistory entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getClientCode());
        stmt.bindString(3, entity.getItemCode());
        stmt.bindString(4, entity.getDivisionCode());
        stmt.bindString(5, entity.getCompanyCode());
        stmt.bindLong(6, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClientItemHistory readEntity(Cursor cursor, int offset) {
        ClientItemHistory entity = new ClientItemHistory( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // ClientCode
            cursor.getString(offset + 2), // ItemCode
            cursor.getString(offset + 3), // DivisionCode
            cursor.getString(offset + 4), // CompanyCode
            new java.util.Date(cursor.getLong(offset + 5)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClientItemHistory entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setClientCode(cursor.getString(offset + 1));
        entity.setItemCode(cursor.getString(offset + 2));
        entity.setDivisionCode(cursor.getString(offset + 3));
        entity.setCompanyCode(cursor.getString(offset + 4));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ClientItemHistory entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ClientItemHistory entity) {
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
