package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.DuoUsers;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DuoUsers.
*/
public class DuoUsersDao extends AbstractDao<DuoUsers, Long> {

    public static final String TABLENAME = "DuoUsers";

    /**
     * Properties of entity DuoUsers.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserCode = new Property(1, String.class, "UserCode", false, "UserCode");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property DuoCode = new Property(3, String.class, "DuoCode", false, "DuoCode");
        public final static Property StampDate = new Property(4, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public DuoUsersDao(DaoConfig config) {
        super(config);
    }
    
    public DuoUsersDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DuoUsers' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'UserCode' TEXT," + // 1: UserCode
                "'CompanyCode' TEXT," + // 2: CompanyCode
                "'DuoCode' TEXT," + // 3: DuoCode
                "'StampDate' INTEGER NOT NULL );"); // 4: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DuoUsers'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DuoUsers entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String UserCode = entity.getUserCode();
        if (UserCode != null) {
            stmt.bindString(2, UserCode);
        }
 
        String CompanyCode = entity.getCompanyCode();
        if (CompanyCode != null) {
            stmt.bindString(3, CompanyCode);
        }
 
        String DuoCode = entity.getDuoCode();
        if (DuoCode != null) {
            stmt.bindString(4, DuoCode);
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
    public DuoUsers readEntity(Cursor cursor, int offset) {
        DuoUsers entity = new DuoUsers( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // UserCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // CompanyCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // DuoCode
            new java.util.Date(cursor.getLong(offset + 4)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DuoUsers entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserCode(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCompanyCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDuoCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DuoUsers entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DuoUsers entity) {
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
