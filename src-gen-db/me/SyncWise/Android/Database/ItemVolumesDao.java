package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ItemVolumes;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ItemVolumes.
*/
public class ItemVolumesDao extends AbstractDao<ItemVolumes, Long> {

    public static final String TABLENAME = "ItemVolumes";

    /**
     * Properties of entity ItemVolumes.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ItemCode = new Property(1, String.class, "ItemCode", false, "ItemCode");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property BigVolumeCM = new Property(3, Double.class, "BigVolumeCM", false, "BigVolumeCM");
        public final static Property MediumVolumeCM = new Property(4, Double.class, "MediumVolumeCM", false, "MediumVolumeCM");
        public final static Property SmallVolumeCM = new Property(5, Double.class, "SmallVolumeCM", false, "SmallVolumeCM");
        public final static Property StampDate = new Property(6, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ItemVolumesDao(DaoConfig config) {
        super(config);
    }
    
    public ItemVolumesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ItemVolumes' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ItemCode' TEXT NOT NULL ," + // 1: ItemCode
                "'CompanyCode' TEXT NOT NULL ," + // 2: CompanyCode
                "'BigVolumeCM' REAL," + // 3: BigVolumeCM
                "'MediumVolumeCM' REAL," + // 4: MediumVolumeCM
                "'SmallVolumeCM' REAL," + // 5: SmallVolumeCM
                "'StampDate' INTEGER NOT NULL );"); // 6: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ItemVolumes'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ItemVolumes entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getItemCode());
        stmt.bindString(3, entity.getCompanyCode());
 
        Double BigVolumeCM = entity.getBigVolumeCM();
        if (BigVolumeCM != null) {
            stmt.bindDouble(4, BigVolumeCM);
        }
 
        Double MediumVolumeCM = entity.getMediumVolumeCM();
        if (MediumVolumeCM != null) {
            stmt.bindDouble(5, MediumVolumeCM);
        }
 
        Double SmallVolumeCM = entity.getSmallVolumeCM();
        if (SmallVolumeCM != null) {
            stmt.bindDouble(6, SmallVolumeCM);
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
    public ItemVolumes readEntity(Cursor cursor, int offset) {
        ItemVolumes entity = new ItemVolumes( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // ItemCode
            cursor.getString(offset + 2), // CompanyCode
            cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3), // BigVolumeCM
            cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4), // MediumVolumeCM
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // SmallVolumeCM
            new java.util.Date(cursor.getLong(offset + 6)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ItemVolumes entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setItemCode(cursor.getString(offset + 1));
        entity.setCompanyCode(cursor.getString(offset + 2));
        entity.setBigVolumeCM(cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3));
        entity.setMediumVolumeCM(cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4));
        entity.setSmallVolumeCM(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ItemVolumes entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ItemVolumes entity) {
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
