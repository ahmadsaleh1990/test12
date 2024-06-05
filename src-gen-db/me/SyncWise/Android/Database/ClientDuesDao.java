package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.ClientDues;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ClientDues.
*/
public class ClientDuesDao extends AbstractDao<ClientDues, Long> {

    public static final String TABLENAME = "ClientDues";

    /**
     * Properties of entity ClientDues.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property InvoiceCode = new Property(1, String.class, "InvoiceCode", false, "InvoiceCode");
        public final static Property ClientCode = new Property(2, String.class, "ClientCode", false, "ClientCode");
        public final static Property CurrencyCode = new Property(3, String.class, "CurrencyCode", false, "CurrencyCode");
        public final static Property InvoiceType = new Property(4, Integer.class, "InvoiceType", false, "InvoiceType");
        public final static Property DivisionCode = new Property(5, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property CompanyCode = new Property(6, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property OriginalAmount = new Property(7, Double.class, "OriginalAmount", false, "OriginalAmount");
        public final static Property RemainingAmount = new Property(8, Double.class, "RemainingAmount", false, "RemainingAmount");
        public final static Property DueDate = new Property(9, java.util.Date.class, "DueDate", false, "DueDate");
        public final static Property IssueDate = new Property(10, java.util.Date.class, "IssueDate", false, "IssueDate");
        public final static Property RelatedAccount = new Property(11, String.class, "RelatedAccount", false, "RelatedAccount");
        public final static Property StampDate = new Property(12, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public ClientDuesDao(DaoConfig config) {
        super(config);
    }
    
    public ClientDuesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ClientDues' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'InvoiceCode' TEXT NOT NULL ," + // 1: InvoiceCode
                "'ClientCode' TEXT," + // 2: ClientCode
                "'CurrencyCode' TEXT," + // 3: CurrencyCode
                "'InvoiceType' INTEGER," + // 4: InvoiceType
                "'DivisionCode' TEXT NOT NULL ," + // 5: DivisionCode
                "'CompanyCode' TEXT NOT NULL ," + // 6: CompanyCode
                "'OriginalAmount' REAL," + // 7: OriginalAmount
                "'RemainingAmount' REAL," + // 8: RemainingAmount
                "'DueDate' INTEGER," + // 9: DueDate
                "'IssueDate' INTEGER," + // 10: IssueDate
                "'RelatedAccount' TEXT," + // 11: RelatedAccount
                "'StampDate' INTEGER NOT NULL );"); // 12: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ClientDues'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClientDues entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getInvoiceCode());
 
        String ClientCode = entity.getClientCode();
        if (ClientCode != null) {
            stmt.bindString(3, ClientCode);
        }
 
        String CurrencyCode = entity.getCurrencyCode();
        if (CurrencyCode != null) {
            stmt.bindString(4, CurrencyCode);
        }
 
        Integer InvoiceType = entity.getInvoiceType();
        if (InvoiceType != null) {
            stmt.bindLong(5, InvoiceType);
        }
        stmt.bindString(6, entity.getDivisionCode());
        stmt.bindString(7, entity.getCompanyCode());
 
        Double OriginalAmount = entity.getOriginalAmount();
        if (OriginalAmount != null) {
            stmt.bindDouble(8, OriginalAmount);
        }
 
        Double RemainingAmount = entity.getRemainingAmount();
        if (RemainingAmount != null) {
            stmt.bindDouble(9, RemainingAmount);
        }
 
        java.util.Date DueDate = entity.getDueDate();
        if (DueDate != null) {
            stmt.bindLong(10, DueDate.getTime());
        }
 
        java.util.Date IssueDate = entity.getIssueDate();
        if (IssueDate != null) {
            stmt.bindLong(11, IssueDate.getTime());
        }
 
        String RelatedAccount = entity.getRelatedAccount();
        if (RelatedAccount != null) {
            stmt.bindString(12, RelatedAccount);
        }
        stmt.bindLong(13, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClientDues readEntity(Cursor cursor, int offset) {
        ClientDues entity = new ClientDues( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // InvoiceCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ClientCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // CurrencyCode
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // InvoiceType
            cursor.getString(offset + 5), // DivisionCode
            cursor.getString(offset + 6), // CompanyCode
            cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7), // OriginalAmount
            cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8), // RemainingAmount
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // DueDate
            cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)), // IssueDate
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // RelatedAccount
            new java.util.Date(cursor.getLong(offset + 12)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClientDues entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setInvoiceCode(cursor.getString(offset + 1));
        entity.setClientCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCurrencyCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setInvoiceType(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setDivisionCode(cursor.getString(offset + 5));
        entity.setCompanyCode(cursor.getString(offset + 6));
        entity.setOriginalAmount(cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7));
        entity.setRemainingAmount(cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8));
        entity.setDueDate(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setIssueDate(cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)));
        entity.setRelatedAccount(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 12)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ClientDues entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ClientDues entity) {
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