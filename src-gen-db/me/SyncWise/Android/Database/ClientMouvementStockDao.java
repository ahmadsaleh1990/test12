package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ClientMouvementStock;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ClientMouvementStock.
*/
public class ClientMouvementStockDao extends AbstractDao<ClientMouvementStock, Long> {

    public static final String TABLENAME = "ClientMouvementStock";

    /**
     * Properties of entity ClientMouvementStock.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ClientCode = new Property(1, String.class, "ClientCode", false, "ClientCode");
        public final static Property ItemCode = new Property(2, String.class, "ItemCode", false, "ItemCode");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property CompanyCode = new Property(4, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property Week1 = new Property(5, Double.class, "Week1", false, "Week1");
        public final static Property Week2 = new Property(6, Double.class, "Week2", false, "Week2");
        public final static Property Week3 = new Property(7, Double.class, "Week3", false, "Week3");
        public final static Property Week4 = new Property(8, Double.class, "Week4", false, "Week4");
        public final static Property StampDate = new Property(9, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ClientMouvementStockDao(DaoConfig config) {
        super(config);
    }
    
    public ClientMouvementStockDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ClientMouvementStock' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ClientCode' TEXT NOT NULL ," + // 1: ClientCode
                "'ItemCode' TEXT NOT NULL ," + // 2: ItemCode
                "'DivisionCode' TEXT NOT NULL ," + // 3: DivisionCode
                "'CompanyCode' TEXT NOT NULL ," + // 4: CompanyCode
                "'Week1' REAL," + // 5: Week1
                "'Week2' REAL," + // 6: Week2
                "'Week3' REAL," + // 7: Week3
                "'Week4' REAL," + // 8: Week4
                "'StampDate' INTEGER NOT NULL );"); // 9: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ClientMouvementStock'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClientMouvementStock entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getClientCode());
        stmt.bindString(3, entity.getItemCode());
        stmt.bindString(4, entity.getDivisionCode());
        stmt.bindString(5, entity.getCompanyCode());
 
        Double Week1 = entity.getWeek1();
        if (Week1 != null) {
            stmt.bindDouble(6, Week1);
        }
 
        Double Week2 = entity.getWeek2();
        if (Week2 != null) {
            stmt.bindDouble(7, Week2);
        }
 
        Double Week3 = entity.getWeek3();
        if (Week3 != null) {
            stmt.bindDouble(8, Week3);
        }
 
        Double Week4 = entity.getWeek4();
        if (Week4 != null) {
            stmt.bindDouble(9, Week4);
        }
        stmt.bindLong(10, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClientMouvementStock readEntity(Cursor cursor, int offset) {
        ClientMouvementStock entity = new ClientMouvementStock( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // ClientCode
            cursor.getString(offset + 2), // ItemCode
            cursor.getString(offset + 3), // DivisionCode
            cursor.getString(offset + 4), // CompanyCode
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // Week1
            cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6), // Week2
            cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7), // Week3
            cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8), // Week4
            new java.util.Date(cursor.getLong(offset + 9)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClientMouvementStock entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setClientCode(cursor.getString(offset + 1));
        entity.setItemCode(cursor.getString(offset + 2));
        entity.setDivisionCode(cursor.getString(offset + 3));
        entity.setCompanyCode(cursor.getString(offset + 4));
        entity.setWeek1(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setWeek2(cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6));
        entity.setWeek3(cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7));
        entity.setWeek4(cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 9)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ClientMouvementStock entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ClientMouvementStock entity) {
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
