package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ClientUserCollection;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ClientUserCollection.
*/
public class ClientUserCollectionDao extends AbstractDao<ClientUserCollection, Long> {

    public static final String TABLENAME = "ClientUserCollection";

    /**
     * Properties of entity ClientUserCollection.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserCode = new Property(1, String.class, "UserCode", false, "UserCode");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property ClientCode = new Property(3, String.class, "ClientCode", false, "ClientCode");
        public final static Property ClientCompanyCode = new Property(4, String.class, "ClientCompanyCode", false, "ClientCompanyCode");
        public final static Property DivisionCode = new Property(5, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property StampDate = new Property(6, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ClientUserCollectionDao(DaoConfig config) {
        super(config);
    }
    
    public ClientUserCollectionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ClientUserCollection' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'UserCode' TEXT NOT NULL ," + // 1: UserCode
                "'CompanyCode' TEXT NOT NULL ," + // 2: CompanyCode
                "'ClientCode' TEXT NOT NULL ," + // 3: ClientCode
                "'ClientCompanyCode' TEXT NOT NULL ," + // 4: ClientCompanyCode
                "'DivisionCode' TEXT NOT NULL ," + // 5: DivisionCode
                "'StampDate' INTEGER NOT NULL );"); // 6: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ClientUserCollection'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClientUserCollection entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUserCode());
        stmt.bindString(3, entity.getCompanyCode());
        stmt.bindString(4, entity.getClientCode());
        stmt.bindString(5, entity.getClientCompanyCode());
        stmt.bindString(6, entity.getDivisionCode());
        stmt.bindLong(7, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClientUserCollection readEntity(Cursor cursor, int offset) {
        ClientUserCollection entity = new ClientUserCollection( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // UserCode
            cursor.getString(offset + 2), // CompanyCode
            cursor.getString(offset + 3), // ClientCode
            cursor.getString(offset + 4), // ClientCompanyCode
            cursor.getString(offset + 5), // DivisionCode
            new java.util.Date(cursor.getLong(offset + 6)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClientUserCollection entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserCode(cursor.getString(offset + 1));
        entity.setCompanyCode(cursor.getString(offset + 2));
        entity.setClientCode(cursor.getString(offset + 3));
        entity.setClientCompanyCode(cursor.getString(offset + 4));
        entity.setDivisionCode(cursor.getString(offset + 5));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ClientUserCollection entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ClientUserCollection entity) {
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
