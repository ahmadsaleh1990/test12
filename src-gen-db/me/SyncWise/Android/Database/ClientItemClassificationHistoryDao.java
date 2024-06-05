package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ClientItemClassificationHistory;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ClientItemClassificationHistory.
*/
public class ClientItemClassificationHistoryDao extends AbstractDao<ClientItemClassificationHistory, Long> {

    public static final String TABLENAME = "ClientItemClassificationHistory";

    /**
     * Properties of entity ClientItemClassificationHistory.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ClientItemClassificationID = new Property(1, long.class, "ClientItemClassificationID", false, "ClientItemClassificationID");
        public final static Property UserCode = new Property(2, String.class, "UserCode", false, "UserCode");
        public final static Property ClientCode = new Property(3, String.class, "ClientCode", false, "ClientCode");
        public final static Property DivisionCode = new Property(4, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property CompanyCode = new Property(5, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property ItemCode = new Property(6, String.class, "ItemCode", false, "ItemCode");
        public final static Property OldClassificationLevelCode = new Property(7, String.class, "OldClassificationLevelCode", false, "OldClassificationLevelCode");
        public final static Property NewClassificationLevelCode = new Property(8, String.class, "NewClassificationLevelCode", false, "NewClassificationLevelCode");
        public final static Property ReasonCode = new Property(9, String.class, "ReasonCode", false, "ReasonCode");
        public final static Property Remarks = new Property(10, String.class, "Remarks", false, "Remarks");
        public final static Property IsProcessed = new Property(11, int.class, "IsProcessed", false, "IsProcessed");
        public final static Property StampDate = new Property(12, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ClientItemClassificationHistoryDao(DaoConfig config) {
        super(config);
    }
    
    public ClientItemClassificationHistoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ClientItemClassificationHistory' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ClientItemClassificationID' INTEGER NOT NULL ," + // 1: ClientItemClassificationID
                "'UserCode' TEXT NOT NULL ," + // 2: UserCode
                "'ClientCode' TEXT NOT NULL ," + // 3: ClientCode
                "'DivisionCode' TEXT NOT NULL ," + // 4: DivisionCode
                "'CompanyCode' TEXT NOT NULL ," + // 5: CompanyCode
                "'ItemCode' TEXT NOT NULL ," + // 6: ItemCode
                "'OldClassificationLevelCode' TEXT," + // 7: OldClassificationLevelCode
                "'NewClassificationLevelCode' TEXT NOT NULL ," + // 8: NewClassificationLevelCode
                "'ReasonCode' TEXT," + // 9: ReasonCode
                "'Remarks' TEXT," + // 10: Remarks
                "'IsProcessed' INTEGER NOT NULL ," + // 11: IsProcessed
                "'StampDate' INTEGER NOT NULL );"); // 12: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ClientItemClassificationHistory'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClientItemClassificationHistory entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getClientItemClassificationID());
        stmt.bindString(3, entity.getUserCode());
        stmt.bindString(4, entity.getClientCode());
        stmt.bindString(5, entity.getDivisionCode());
        stmt.bindString(6, entity.getCompanyCode());
        stmt.bindString(7, entity.getItemCode());
 
        String OldClassificationLevelCode = entity.getOldClassificationLevelCode();
        if (OldClassificationLevelCode != null) {
            stmt.bindString(8, OldClassificationLevelCode);
        }
        stmt.bindString(9, entity.getNewClassificationLevelCode());
 
        String ReasonCode = entity.getReasonCode();
        if (ReasonCode != null) {
            stmt.bindString(10, ReasonCode);
        }
 
        String Remarks = entity.getRemarks();
        if (Remarks != null) {
            stmt.bindString(11, Remarks);
        }
        stmt.bindLong(12, entity.getIsProcessed());
        stmt.bindLong(13, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClientItemClassificationHistory readEntity(Cursor cursor, int offset) {
        ClientItemClassificationHistory entity = new ClientItemClassificationHistory( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // ClientItemClassificationID
            cursor.getString(offset + 2), // UserCode
            cursor.getString(offset + 3), // ClientCode
            cursor.getString(offset + 4), // DivisionCode
            cursor.getString(offset + 5), // CompanyCode
            cursor.getString(offset + 6), // ItemCode
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // OldClassificationLevelCode
            cursor.getString(offset + 8), // NewClassificationLevelCode
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // ReasonCode
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // Remarks
            cursor.getInt(offset + 11), // IsProcessed
            new java.util.Date(cursor.getLong(offset + 12)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClientItemClassificationHistory entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setClientItemClassificationID(cursor.getLong(offset + 1));
        entity.setUserCode(cursor.getString(offset + 2));
        entity.setClientCode(cursor.getString(offset + 3));
        entity.setDivisionCode(cursor.getString(offset + 4));
        entity.setCompanyCode(cursor.getString(offset + 5));
        entity.setItemCode(cursor.getString(offset + 6));
        entity.setOldClassificationLevelCode(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setNewClassificationLevelCode(cursor.getString(offset + 8));
        entity.setReasonCode(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setRemarks(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setIsProcessed(cursor.getInt(offset + 11));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 12)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ClientItemClassificationHistory entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ClientItemClassificationHistory entity) {
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