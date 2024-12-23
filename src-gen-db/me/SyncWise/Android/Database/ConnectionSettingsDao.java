package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ConnectionSettings;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ConnectionSettings.
*/
public class ConnectionSettingsDao extends AbstractDao<ConnectionSettings, Long> {

    public static final String TABLENAME = "ConnectionSettings";

    /**
     * Properties of entity ConnectionSettings.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ConnectionSettingID = new Property(1, int.class, "ConnectionSettingID", false, "ConnectionSettingID");
        public final static Property ConnectionSettingType = new Property(2, String.class, "ConnectionSettingType", false, "ConnectionSettingType");
        public final static Property ConnectionSettingDescription = new Property(3, String.class, "ConnectionSettingDescription", false, "ConnectionSettingDescription");
        public final static Property ConnectionSettingAltDescription = new Property(4, String.class, "ConnectionSettingAltDescription", false, "ConnectionSettingAltDescription");
        public final static Property ConnectionSettingURL = new Property(5, String.class, "ConnectionSettingURL", false, "ConnectionSettingURL");
        public final static Property StampDate = new Property(6, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ConnectionSettingsDao(DaoConfig config) {
        super(config);
    }
    
    public ConnectionSettingsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ConnectionSettings' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ConnectionSettingID' INTEGER NOT NULL UNIQUE ," + // 1: ConnectionSettingID
                "'ConnectionSettingType' TEXT," + // 2: ConnectionSettingType
                "'ConnectionSettingDescription' TEXT," + // 3: ConnectionSettingDescription
                "'ConnectionSettingAltDescription' TEXT," + // 4: ConnectionSettingAltDescription
                "'ConnectionSettingURL' TEXT," + // 5: ConnectionSettingURL
                "'StampDate' INTEGER NOT NULL );"); // 6: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ConnectionSettings'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ConnectionSettings entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getConnectionSettingID());
 
        String ConnectionSettingType = entity.getConnectionSettingType();
        if (ConnectionSettingType != null) {
            stmt.bindString(3, ConnectionSettingType);
        }
 
        String ConnectionSettingDescription = entity.getConnectionSettingDescription();
        if (ConnectionSettingDescription != null) {
            stmt.bindString(4, ConnectionSettingDescription);
        }
 
        String ConnectionSettingAltDescription = entity.getConnectionSettingAltDescription();
        if (ConnectionSettingAltDescription != null) {
            stmt.bindString(5, ConnectionSettingAltDescription);
        }
 
        String ConnectionSettingURL = entity.getConnectionSettingURL();
        if (ConnectionSettingURL != null) {
            stmt.bindString(6, ConnectionSettingURL);
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
    public ConnectionSettings readEntity(Cursor cursor, int offset) {
        ConnectionSettings entity = new ConnectionSettings( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // ConnectionSettingID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ConnectionSettingType
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ConnectionSettingDescription
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // ConnectionSettingAltDescription
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // ConnectionSettingURL
            new java.util.Date(cursor.getLong(offset + 6)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ConnectionSettings entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setConnectionSettingID(cursor.getInt(offset + 1));
        entity.setConnectionSettingType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setConnectionSettingDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setConnectionSettingAltDescription(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setConnectionSettingURL(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ConnectionSettings entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ConnectionSettings entity) {
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
