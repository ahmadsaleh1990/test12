package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ClientCurrencies;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ClientCurrencies.
*/
public class ClientCurrenciesDao extends AbstractDao<ClientCurrencies, Long> {

    public static final String TABLENAME = "ClientCurrencies";

    /**
     * Properties of entity ClientCurrencies.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ClientCode = new Property(1, String.class, "ClientCode", false, "ClientCode");
        public final static Property CurrencyCode = new Property(2, String.class, "CurrencyCode", false, "CurrencyCode");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property CompanyCode = new Property(4, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property Priority = new Property(5, Integer.class, "Priority", false, "Priority");
        public final static Property StampDate = new Property(6, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ClientCurrenciesDao(DaoConfig config) {
        super(config);
    }
    
    public ClientCurrenciesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ClientCurrencies' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ClientCode' TEXT NOT NULL ," + // 1: ClientCode
                "'CurrencyCode' TEXT NOT NULL ," + // 2: CurrencyCode
                "'DivisionCode' TEXT NOT NULL ," + // 3: DivisionCode
                "'CompanyCode' TEXT NOT NULL ," + // 4: CompanyCode
                "'Priority' INTEGER," + // 5: Priority
                "'StampDate' INTEGER NOT NULL );"); // 6: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ClientCurrencies'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClientCurrencies entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getClientCode());
        stmt.bindString(3, entity.getCurrencyCode());
        stmt.bindString(4, entity.getDivisionCode());
        stmt.bindString(5, entity.getCompanyCode());
 
        Integer Priority = entity.getPriority();
        if (Priority != null) {
            stmt.bindLong(6, Priority);
        }
        stmt.bindLong(7, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClientCurrencies readEntity(Cursor cursor, int offset) {
        ClientCurrencies entity = new ClientCurrencies( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // ClientCode
            cursor.getString(offset + 2), // CurrencyCode
            cursor.getString(offset + 3), // DivisionCode
            cursor.getString(offset + 4), // CompanyCode
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // Priority
            new java.util.Date(cursor.getLong(offset + 6)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClientCurrencies entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setClientCode(cursor.getString(offset + 1));
        entity.setCurrencyCode(cursor.getString(offset + 2));
        entity.setDivisionCode(cursor.getString(offset + 3));
        entity.setCompanyCode(cursor.getString(offset + 4));
        entity.setPriority(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ClientCurrencies entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ClientCurrencies entity) {
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
