package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.UserVersions;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table UserVersions.
*/
public class UserVersionsDao extends AbstractDao<UserVersions, Long> {

    public static final String TABLENAME = "UserVersions";

    /**
     * Properties of entity UserVersions.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserCode = new Property(1, String.class, "UserCode", false, "UserCode");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property VersionCode = new Property(3, String.class, "VersionCode", false, "VersionCode");
        public final static Property IMEI = new Property(4, String.class, "IMEI", false, "IMEI");
        public final static Property StampDate = new Property(5, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public UserVersionsDao(DaoConfig config) {
        super(config);
    }
    
    public UserVersionsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'UserVersions' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'UserCode' TEXT NOT NULL ," + // 1: UserCode
                "'CompanyCode' TEXT," + // 2: CompanyCode
                "'VersionCode' TEXT," + // 3: VersionCode
                "'IMEI' TEXT," + // 4: IMEI
                "'StampDate' INTEGER NOT NULL );"); // 5: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'UserVersions'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, UserVersions entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUserCode());
 
        String CompanyCode = entity.getCompanyCode();
        if (CompanyCode != null) {
            stmt.bindString(3, CompanyCode);
        }
 
        String VersionCode = entity.getVersionCode();
        if (VersionCode != null) {
            stmt.bindString(4, VersionCode);
        }
 
        String IMEI = entity.getIMEI();
        if (IMEI != null) {
            stmt.bindString(5, IMEI);
        }
        stmt.bindLong(6, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public UserVersions readEntity(Cursor cursor, int offset) {
        UserVersions entity = new UserVersions( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // UserCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // CompanyCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // VersionCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // IMEI
            new java.util.Date(cursor.getLong(offset + 5)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, UserVersions entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserCode(cursor.getString(offset + 1));
        entity.setCompanyCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setVersionCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setIMEI(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(UserVersions entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(UserVersions entity) {
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
