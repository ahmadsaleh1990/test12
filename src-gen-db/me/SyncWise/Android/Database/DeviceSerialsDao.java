package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.DeviceSerials;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DeviceSerials.
*/
public class DeviceSerialsDao extends AbstractDao<DeviceSerials, Long> {

    public static final String TABLENAME = "DeviceSerials";

    /**
     * Properties of entity DeviceSerials.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceSerialCode = new Property(1, String.class, "DeviceSerialCode", false, "DeviceSerialCode");
        public final static Property UserCode = new Property(2, String.class, "UserCode", false, "UserCode");
        public final static Property CompanyCode = new Property(3, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property StampDate = new Property(4, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public DeviceSerialsDao(DaoConfig config) {
        super(config);
    }
    
    public DeviceSerialsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DeviceSerials' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'DeviceSerialCode' TEXT NOT NULL ," + // 1: DeviceSerialCode
                "'UserCode' TEXT NOT NULL ," + // 2: UserCode
                "'CompanyCode' TEXT NOT NULL ," + // 3: CompanyCode
                "'StampDate' INTEGER NOT NULL );"); // 4: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DeviceSerials'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DeviceSerials entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getDeviceSerialCode());
        stmt.bindString(3, entity.getUserCode());
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
    public DeviceSerials readEntity(Cursor cursor, int offset) {
        DeviceSerials entity = new DeviceSerials( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // DeviceSerialCode
            cursor.getString(offset + 2), // UserCode
            cursor.getString(offset + 3), // CompanyCode
            new java.util.Date(cursor.getLong(offset + 4)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DeviceSerials entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceSerialCode(cursor.getString(offset + 1));
        entity.setUserCode(cursor.getString(offset + 2));
        entity.setCompanyCode(cursor.getString(offset + 3));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DeviceSerials entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DeviceSerials entity) {
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
