package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ClientPropertyLevels;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ClientPropertyLevels.
*/
public class ClientPropertyLevelsDao extends AbstractDao<ClientPropertyLevels, Long> {

    public static final String TABLENAME = "ClientPropertyLevels";

    /**
     * Properties of entity ClientPropertyLevels.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ClientPropertyLevelCode = new Property(1, String.class, "ClientPropertyLevelCode", false, "ClientPropertyLevelCode");
        public final static Property ClientPropertyLevelDescription = new Property(2, String.class, "ClientPropertyLevelDescription", false, "ClientPropertyLevelDescription");
        public final static Property ClientPropertyLevelAltDescription = new Property(3, String.class, "ClientPropertyLevelAltDescription", false, "ClientPropertyLevelAltDescription");
        public final static Property StampDate = new Property(4, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ClientPropertyLevelsDao(DaoConfig config) {
        super(config);
    }
    
    public ClientPropertyLevelsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ClientPropertyLevels' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ClientPropertyLevelCode' TEXT NOT NULL UNIQUE ," + // 1: ClientPropertyLevelCode
                "'ClientPropertyLevelDescription' TEXT," + // 2: ClientPropertyLevelDescription
                "'ClientPropertyLevelAltDescription' TEXT," + // 3: ClientPropertyLevelAltDescription
                "'StampDate' INTEGER NOT NULL );"); // 4: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ClientPropertyLevels'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClientPropertyLevels entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getClientPropertyLevelCode());
 
        String ClientPropertyLevelDescription = entity.getClientPropertyLevelDescription();
        if (ClientPropertyLevelDescription != null) {
            stmt.bindString(3, ClientPropertyLevelDescription);
        }
 
        String ClientPropertyLevelAltDescription = entity.getClientPropertyLevelAltDescription();
        if (ClientPropertyLevelAltDescription != null) {
            stmt.bindString(4, ClientPropertyLevelAltDescription);
        }
        stmt.bindLong(5, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClientPropertyLevels readEntity(Cursor cursor, int offset) {
        ClientPropertyLevels entity = new ClientPropertyLevels( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // ClientPropertyLevelCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ClientPropertyLevelDescription
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ClientPropertyLevelAltDescription
            new java.util.Date(cursor.getLong(offset + 4)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClientPropertyLevels entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setClientPropertyLevelCode(cursor.getString(offset + 1));
        entity.setClientPropertyLevelDescription(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setClientPropertyLevelAltDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ClientPropertyLevels entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ClientPropertyLevels entity) {
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