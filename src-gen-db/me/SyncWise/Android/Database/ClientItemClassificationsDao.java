package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ClientItemClassifications;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ClientItemClassifications.
*/
public class ClientItemClassificationsDao extends AbstractDao<ClientItemClassifications, Long> {

    public static final String TABLENAME = "ClientItemClassifications";

    /**
     * Properties of entity ClientItemClassifications.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserCode = new Property(1, String.class, "UserCode", false, "UserCode");
        public final static Property ClientCode = new Property(2, String.class, "ClientCode", false, "ClientCode");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property CompanyCode = new Property(4, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property ItemCode = new Property(5, String.class, "ItemCode", false, "ItemCode");
        public final static Property ClassificationLevelCode = new Property(6, String.class, "ClassificationLevelCode", false, "ClassificationLevelCode");
        public final static Property IsProcessed = new Property(7, int.class, "IsProcessed", false, "IsProcessed");
        public final static Property StampDate = new Property(8, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ClientItemClassificationsDao(DaoConfig config) {
        super(config);
    }
    
    public ClientItemClassificationsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ClientItemClassifications' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'UserCode' TEXT NOT NULL ," + // 1: UserCode
                "'ClientCode' TEXT NOT NULL ," + // 2: ClientCode
                "'DivisionCode' TEXT NOT NULL ," + // 3: DivisionCode
                "'CompanyCode' TEXT NOT NULL ," + // 4: CompanyCode
                "'ItemCode' TEXT NOT NULL ," + // 5: ItemCode
                "'ClassificationLevelCode' TEXT NOT NULL ," + // 6: ClassificationLevelCode
                "'IsProcessed' INTEGER NOT NULL ," + // 7: IsProcessed
                "'StampDate' INTEGER NOT NULL );"); // 8: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ClientItemClassifications'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClientItemClassifications entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUserCode());
        stmt.bindString(3, entity.getClientCode());
        stmt.bindString(4, entity.getDivisionCode());
        stmt.bindString(5, entity.getCompanyCode());
        stmt.bindString(6, entity.getItemCode());
        stmt.bindString(7, entity.getClassificationLevelCode());
        stmt.bindLong(8, entity.getIsProcessed());
        stmt.bindLong(9, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClientItemClassifications readEntity(Cursor cursor, int offset) {
        ClientItemClassifications entity = new ClientItemClassifications( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // UserCode
            cursor.getString(offset + 2), // ClientCode
            cursor.getString(offset + 3), // DivisionCode
            cursor.getString(offset + 4), // CompanyCode
            cursor.getString(offset + 5), // ItemCode
            cursor.getString(offset + 6), // ClassificationLevelCode
            cursor.getInt(offset + 7), // IsProcessed
            new java.util.Date(cursor.getLong(offset + 8)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClientItemClassifications entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserCode(cursor.getString(offset + 1));
        entity.setClientCode(cursor.getString(offset + 2));
        entity.setDivisionCode(cursor.getString(offset + 3));
        entity.setCompanyCode(cursor.getString(offset + 4));
        entity.setItemCode(cursor.getString(offset + 5));
        entity.setClassificationLevelCode(cursor.getString(offset + 6));
        entity.setIsProcessed(cursor.getInt(offset + 7));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 8)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ClientItemClassifications entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ClientItemClassifications entity) {
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
