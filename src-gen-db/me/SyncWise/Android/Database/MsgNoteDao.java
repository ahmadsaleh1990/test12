package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.MsgNote;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MsgNote.
*/
public class MsgNoteDao extends AbstractDao<MsgNote, Long> {

    public static final String TABLENAME = "MsgNote";

    /**
     * Properties of entity MsgNote.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MsgNoteCode = new Property(1, String.class, "MsgNoteCode", false, "MsgNoteCode");
        public final static Property UserCode = new Property(2, String.class, "UserCode", false, "UserCode");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property ClientCode = new Property(4, String.class, "ClientCode", false, "ClientCode");
        public final static Property CompanyCode = new Property(5, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property NoteDate = new Property(6, java.util.Date.class, "NoteDate", false, "NoteDate");
        public final static Property MsgBody = new Property(7, String.class, "MsgBody", false, "MsgBody");
        public final static Property IsProcessed = new Property(8, int.class, "IsProcessed", false, "IsProcessed");
        public final static Property StampDate = new Property(9, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public MsgNoteDao(DaoConfig config) {
        super(config);
    }
    
    public MsgNoteDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MsgNote' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'MsgNoteCode' TEXT NOT NULL UNIQUE ," + // 1: MsgNoteCode
                "'UserCode' TEXT," + // 2: UserCode
                "'DivisionCode' TEXT," + // 3: DivisionCode
                "'ClientCode' TEXT," + // 4: ClientCode
                "'CompanyCode' TEXT," + // 5: CompanyCode
                "'NoteDate' INTEGER," + // 6: NoteDate
                "'MsgBody' TEXT," + // 7: MsgBody
                "'IsProcessed' INTEGER NOT NULL ," + // 8: IsProcessed
                "'StampDate' INTEGER NOT NULL );"); // 9: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MsgNote'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MsgNote entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getMsgNoteCode());
 
        String UserCode = entity.getUserCode();
        if (UserCode != null) {
            stmt.bindString(3, UserCode);
        }
 
        String DivisionCode = entity.getDivisionCode();
        if (DivisionCode != null) {
            stmt.bindString(4, DivisionCode);
        }
 
        String ClientCode = entity.getClientCode();
        if (ClientCode != null) {
            stmt.bindString(5, ClientCode);
        }
 
        String CompanyCode = entity.getCompanyCode();
        if (CompanyCode != null) {
            stmt.bindString(6, CompanyCode);
        }
 
        java.util.Date NoteDate = entity.getNoteDate();
        if (NoteDate != null) {
            stmt.bindLong(7, NoteDate.getTime());
        }
 
        String MsgBody = entity.getMsgBody();
        if (MsgBody != null) {
            stmt.bindString(8, MsgBody);
        }
        stmt.bindLong(9, entity.getIsProcessed());
        stmt.bindLong(10, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public MsgNote readEntity(Cursor cursor, int offset) {
        MsgNote entity = new MsgNote( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // MsgNoteCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // UserCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // DivisionCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // ClientCode
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // CompanyCode
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // NoteDate
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // MsgBody
            cursor.getInt(offset + 8), // IsProcessed
            new java.util.Date(cursor.getLong(offset + 9)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MsgNote entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMsgNoteCode(cursor.getString(offset + 1));
        entity.setUserCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDivisionCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setClientCode(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCompanyCode(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNoteDate(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setMsgBody(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setIsProcessed(cursor.getInt(offset + 8));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 9)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MsgNote entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MsgNote entity) {
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
