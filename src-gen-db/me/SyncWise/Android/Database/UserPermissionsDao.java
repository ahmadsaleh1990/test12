package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.UserPermissions;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table UserPermissions.
*/
public class UserPermissionsDao extends AbstractDao<UserPermissions, Long> {

    public static final String TABLENAME = "UserPermissions";

    /**
     * Properties of entity UserPermissions.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PermissionID = new Property(1, int.class, "PermissionID", false, "PermissionID");
        public final static Property UserCode = new Property(2, String.class, "UserCode", false, "UserCode");
        public final static Property CompanyCode = new Property(3, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property PermissionName = new Property(4, String.class, "PermissionName", false, "PermissionName");
        public final static Property PermissionValue = new Property(5, String.class, "PermissionValue", false, "PermissionValue");
        public final static Property StampDate = new Property(6, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public UserPermissionsDao(DaoConfig config) {
        super(config);
    }
    
    public UserPermissionsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'UserPermissions' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PermissionID' INTEGER NOT NULL ," + // 1: PermissionID
                "'UserCode' TEXT NOT NULL ," + // 2: UserCode
                "'CompanyCode' TEXT NOT NULL ," + // 3: CompanyCode
                "'PermissionName' TEXT," + // 4: PermissionName
                "'PermissionValue' TEXT," + // 5: PermissionValue
                "'StampDate' INTEGER NOT NULL );"); // 6: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'UserPermissions'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, UserPermissions entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPermissionID());
        stmt.bindString(3, entity.getUserCode());
        stmt.bindString(4, entity.getCompanyCode());
 
        String PermissionName = entity.getPermissionName();
        if (PermissionName != null) {
            stmt.bindString(5, PermissionName);
        }
 
        String PermissionValue = entity.getPermissionValue();
        if (PermissionValue != null) {
            stmt.bindString(6, PermissionValue);
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
    public UserPermissions readEntity(Cursor cursor, int offset) {
        UserPermissions entity = new UserPermissions( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // PermissionID
            cursor.getString(offset + 2), // UserCode
            cursor.getString(offset + 3), // CompanyCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // PermissionName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // PermissionValue
            new java.util.Date(cursor.getLong(offset + 6)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, UserPermissions entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPermissionID(cursor.getInt(offset + 1));
        entity.setUserCode(cursor.getString(offset + 2));
        entity.setCompanyCode(cursor.getString(offset + 3));
        entity.setPermissionName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPermissionValue(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(UserPermissions entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(UserPermissions entity) {
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