package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.AreaLevels;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table AreaLevels.
*/
public class AreaLevelsDao extends AbstractDao<AreaLevels, Long> {

    public static final String TABLENAME = "AreaLevels";

    /**
     * Properties of entity AreaLevels.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property AreaLevelCode = new Property(1, String.class, "AreaLevelCode", false, "AreaLevelCode");
        public final static Property AreaLevelDescription = new Property(2, String.class, "AreaLevelDescription", false, "AreaLevelDescription");
        public final static Property AreaLevelAltDescription = new Property(3, String.class, "AreaLevelAltDescription", false, "AreaLevelAltDescription");
        public final static Property Sequence = new Property(4, Integer.class, "Sequence", false, "Sequence");
        public final static Property StampDate = new Property(5, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public AreaLevelsDao(DaoConfig config) {
        super(config);
    }
    
    public AreaLevelsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'AreaLevels' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'AreaLevelCode' TEXT NOT NULL UNIQUE ," + // 1: AreaLevelCode
                "'AreaLevelDescription' TEXT," + // 2: AreaLevelDescription
                "'AreaLevelAltDescription' TEXT," + // 3: AreaLevelAltDescription
                "'Sequence' INTEGER," + // 4: Sequence
                "'StampDate' INTEGER NOT NULL );"); // 5: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'AreaLevels'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, AreaLevels entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getAreaLevelCode());
 
        String AreaLevelDescription = entity.getAreaLevelDescription();
        if (AreaLevelDescription != null) {
            stmt.bindString(3, AreaLevelDescription);
        }
 
        String AreaLevelAltDescription = entity.getAreaLevelAltDescription();
        if (AreaLevelAltDescription != null) {
            stmt.bindString(4, AreaLevelAltDescription);
        }
 
        Integer Sequence = entity.getSequence();
        if (Sequence != null) {
            stmt.bindLong(5, Sequence);
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
    public AreaLevels readEntity(Cursor cursor, int offset) {
        AreaLevels entity = new AreaLevels( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // AreaLevelCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // AreaLevelDescription
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // AreaLevelAltDescription
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // Sequence
            new java.util.Date(cursor.getLong(offset + 5)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, AreaLevels entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAreaLevelCode(cursor.getString(offset + 1));
        entity.setAreaLevelDescription(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAreaLevelAltDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSequence(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(AreaLevels entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(AreaLevels entity) {
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