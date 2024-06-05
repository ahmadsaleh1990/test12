package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.SuggestedUserLoad;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SuggestedUserLoad.
*/
public class SuggestedUserLoadDao extends AbstractDao<SuggestedUserLoad, Long> {

    public static final String TABLENAME = "SuggestedUserLoad";

    /**
     * Properties of entity SuggestedUserLoad.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserCode = new Property(1, String.class, "UserCode", false, "UserCode");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property SuggestedDay = new Property(4, String.class, "SuggestedDay", false, "SuggestedDay");
        public final static Property LineId = new Property(5, int.class, "LineId", false, "LineId");
        public final static Property ItemCode = new Property(6, String.class, "ItemCode", false, "ItemCode");
        public final static Property SuggestedQuantity = new Property(7, Double.class, "SuggestedQuantity", false, "SuggestedQuantity");
        public final static Property StampDate = new Property(8, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public SuggestedUserLoadDao(DaoConfig config) {
        super(config);
    }
    
    public SuggestedUserLoadDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SuggestedUserLoad' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'UserCode' TEXT NOT NULL ," + // 1: UserCode
                "'CompanyCode' TEXT NOT NULL ," + // 2: CompanyCode
                "'DivisionCode' TEXT NOT NULL ," + // 3: DivisionCode
                "'SuggestedDay' TEXT NOT NULL ," + // 4: SuggestedDay
                "'LineId' INTEGER NOT NULL ," + // 5: LineId
                "'ItemCode' TEXT," + // 6: ItemCode
                "'SuggestedQuantity' REAL," + // 7: SuggestedQuantity
                "'StampDate' INTEGER NOT NULL );"); // 8: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SuggestedUserLoad'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SuggestedUserLoad entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUserCode());
        stmt.bindString(3, entity.getCompanyCode());
        stmt.bindString(4, entity.getDivisionCode());
        stmt.bindString(5, entity.getSuggestedDay());
        stmt.bindLong(6, entity.getLineId());
 
        String ItemCode = entity.getItemCode();
        if (ItemCode != null) {
            stmt.bindString(7, ItemCode);
        }
 
        Double SuggestedQuantity = entity.getSuggestedQuantity();
        if (SuggestedQuantity != null) {
            stmt.bindDouble(8, SuggestedQuantity);
        }
        stmt.bindLong(9, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SuggestedUserLoad readEntity(Cursor cursor, int offset) {
        SuggestedUserLoad entity = new SuggestedUserLoad( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // UserCode
            cursor.getString(offset + 2), // CompanyCode
            cursor.getString(offset + 3), // DivisionCode
            cursor.getString(offset + 4), // SuggestedDay
            cursor.getInt(offset + 5), // LineId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // ItemCode
            cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7), // SuggestedQuantity
            new java.util.Date(cursor.getLong(offset + 8)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SuggestedUserLoad entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserCode(cursor.getString(offset + 1));
        entity.setCompanyCode(cursor.getString(offset + 2));
        entity.setDivisionCode(cursor.getString(offset + 3));
        entity.setSuggestedDay(cursor.getString(offset + 4));
        entity.setLineId(cursor.getInt(offset + 5));
        entity.setItemCode(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSuggestedQuantity(cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 8)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SuggestedUserLoad entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SuggestedUserLoad entity) {
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
