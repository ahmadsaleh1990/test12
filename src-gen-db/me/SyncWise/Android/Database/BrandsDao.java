package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.Brands;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table Brands.
*/
public class BrandsDao extends AbstractDao<Brands, Long> {

    public static final String TABLENAME = "Brands";

    /**
     * Properties of entity Brands.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property BrandCode = new Property(1, String.class, "BrandCode", false, "BrandCode");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property BrandDescription = new Property(3, String.class, "BrandDescription", false, "BrandDescription");
        public final static Property BrandAltDescription = new Property(4, String.class, "BrandAltDescription", false, "BrandAltDescription");
        public final static Property DivisionCode = new Property(5, Double.class, "DivisionCode", false, "DivisionCode");
        public final static Property StampDate = new Property(6, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public BrandsDao(DaoConfig config) {
        super(config);
    }
    
    public BrandsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'Brands' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'BrandCode' TEXT NOT NULL ," + // 1: BrandCode
                "'CompanyCode' TEXT NOT NULL ," + // 2: CompanyCode
                "'BrandDescription' TEXT," + // 3: BrandDescription
                "'BrandAltDescription' TEXT," + // 4: BrandAltDescription
                "'DivisionCode' REAL," + // 5: DivisionCode
                "'StampDate' INTEGER NOT NULL );"); // 6: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'Brands'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Brands entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getBrandCode());
        stmt.bindString(3, entity.getCompanyCode());
 
        String BrandDescription = entity.getBrandDescription();
        if (BrandDescription != null) {
            stmt.bindString(4, BrandDescription);
        }
 
        String BrandAltDescription = entity.getBrandAltDescription();
        if (BrandAltDescription != null) {
            stmt.bindString(5, BrandAltDescription);
        }
 
        Double DivisionCode = entity.getDivisionCode();
        if (DivisionCode != null) {
            stmt.bindDouble(6, DivisionCode);
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
    public Brands readEntity(Cursor cursor, int offset) {
        Brands entity = new Brands( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // BrandCode
            cursor.getString(offset + 2), // CompanyCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // BrandDescription
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // BrandAltDescription
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // DivisionCode
            new java.util.Date(cursor.getLong(offset + 6)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Brands entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBrandCode(cursor.getString(offset + 1));
        entity.setCompanyCode(cursor.getString(offset + 2));
        entity.setBrandDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBrandAltDescription(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDivisionCode(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Brands entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Brands entity) {
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
