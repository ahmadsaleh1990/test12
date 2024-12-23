package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.TransactionHeaders;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table TransactionHeaders.
*/
public class TransactionHeadersDao extends AbstractDao<TransactionHeaders, Long> {

    public static final String TABLENAME = "TransactionHeaders";

    /**
     * Properties of entity TransactionHeaders.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TransactionCode = new Property(1, String.class, "TransactionCode", false, "TransactionCode");
        public final static Property TransactionType = new Property(2, Integer.class, "TransactionType", false, "TransactionType");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property CompanyCode = new Property(4, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property CurrencyCode = new Property(5, String.class, "CurrencyCode", false, "CurrencyCode");
        public final static Property ClientCode = new Property(6, String.class, "ClientCode", false, "ClientCode");
        public final static Property UserCode = new Property(7, String.class, "UserCode", false, "UserCode");
        public final static Property VisitID = new Property(8, Long.class, "VisitID", false, "VisitID");
        public final static Property JourneyCode = new Property(9, String.class, "JourneyCode", false, "JourneyCode");
        public final static Property IssueDate = new Property(10, java.util.Date.class, "IssueDate", false, "IssueDate");
        public final static Property DeliveryDate = new Property(11, java.util.Date.class, "DeliveryDate", false, "DeliveryDate");
        public final static Property GrossAmount = new Property(12, Double.class, "GrossAmount", false, "GrossAmount");
        public final static Property DiscountAmount = new Property(13, Double.class, "DiscountAmount", false, "DiscountAmount");
        public final static Property NetAmount = new Property(14, Double.class, "NetAmount", false, "NetAmount");
        public final static Property TaxAmount = new Property(15, Double.class, "TaxAmount", false, "TaxAmount");
        public final static Property TotalTaxAmount = new Property(16, Double.class, "TotalTaxAmount", false, "TotalTaxAmount");
        public final static Property RemainingAmount = new Property(17, Double.class, "RemainingAmount", false, "RemainingAmount");
        public final static Property TransactionStatus = new Property(18, Integer.class, "TransactionStatus", false, "TransactionStatus");
        public final static Property Info1 = new Property(19, String.class, "Info1", false, "Info1");
        public final static Property Info2 = new Property(20, String.class, "Info2", false, "Info2");
        public final static Property Info3 = new Property(21, String.class, "Info3", false, "Info3");
        public final static Property Info4 = new Property(22, String.class, "Info4", false, "Info4");
        public final static Property Info5 = new Property(23, String.class, "Info5", false, "Info5");
        public final static Property ApprovedRequestReturnReference = new Property(24, String.class, "ApprovedRequestReturnReference", false, "ApprovedRequestReturnReference");
        public final static Property TransactionPasswordCode = new Property(25, String.class, "TransactionPasswordCode", false, "TransactionPasswordCode");
        public final static Property Notes = new Property(26, String.class, "Notes", false, "Notes");
        public final static Property ClientReferenceNumber = new Property(27, String.class, "ClientReferenceNumber", false, "ClientReferenceNumber");
        public final static Property PrintingTimes = new Property(28, Integer.class, "PrintingTimes", false, "PrintingTimes");
        public final static Property DeviceType = new Property(29, Integer.class, "DeviceType", false, "DeviceType");
        public final static Property IsProcessed = new Property(30, int.class, "IsProcessed", false, "IsProcessed");
        public final static Property StampDate = new Property(31, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public TransactionHeadersDao(DaoConfig config) {
        super(config);
    }
    
    public TransactionHeadersDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'TransactionHeaders' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'TransactionCode' TEXT NOT NULL UNIQUE ," + // 1: TransactionCode
                "'TransactionType' INTEGER," + // 2: TransactionType
                "'DivisionCode' TEXT," + // 3: DivisionCode
                "'CompanyCode' TEXT," + // 4: CompanyCode
                "'CurrencyCode' TEXT," + // 5: CurrencyCode
                "'ClientCode' TEXT," + // 6: ClientCode
                "'UserCode' TEXT," + // 7: UserCode
                "'VisitID' INTEGER," + // 8: VisitID
                "'JourneyCode' TEXT," + // 9: JourneyCode
                "'IssueDate' INTEGER," + // 10: IssueDate
                "'DeliveryDate' INTEGER," + // 11: DeliveryDate
                "'GrossAmount' REAL," + // 12: GrossAmount
                "'DiscountAmount' REAL," + // 13: DiscountAmount
                "'NetAmount' REAL," + // 14: NetAmount
                "'TaxAmount' REAL," + // 15: TaxAmount
                "'TotalTaxAmount' REAL," + // 16: TotalTaxAmount
                "'RemainingAmount' REAL," + // 17: RemainingAmount
                "'TransactionStatus' INTEGER," + // 18: TransactionStatus
                "'Info1' TEXT," + // 19: Info1
                "'Info2' TEXT," + // 20: Info2
                "'Info3' TEXT," + // 21: Info3
                "'Info4' TEXT," + // 22: Info4
                "'Info5' TEXT," + // 23: Info5
                "'ApprovedRequestReturnReference' TEXT," + // 24: ApprovedRequestReturnReference
                "'TransactionPasswordCode' TEXT," + // 25: TransactionPasswordCode
                "'Notes' TEXT," + // 26: Notes
                "'ClientReferenceNumber' TEXT," + // 27: ClientReferenceNumber
                "'PrintingTimes' INTEGER," + // 28: PrintingTimes
                "'DeviceType' INTEGER," + // 29: DeviceType
                "'IsProcessed' INTEGER NOT NULL ," + // 30: IsProcessed
                "'StampDate' INTEGER NOT NULL );"); // 31: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'TransactionHeaders'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TransactionHeaders entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTransactionCode());
 
        Integer TransactionType = entity.getTransactionType();
        if (TransactionType != null) {
            stmt.bindLong(3, TransactionType);
        }
 
        String DivisionCode = entity.getDivisionCode();
        if (DivisionCode != null) {
            stmt.bindString(4, DivisionCode);
        }
 
        String CompanyCode = entity.getCompanyCode();
        if (CompanyCode != null) {
            stmt.bindString(5, CompanyCode);
        }
 
        String CurrencyCode = entity.getCurrencyCode();
        if (CurrencyCode != null) {
            stmt.bindString(6, CurrencyCode);
        }
 
        String ClientCode = entity.getClientCode();
        if (ClientCode != null) {
            stmt.bindString(7, ClientCode);
        }
 
        String UserCode = entity.getUserCode();
        if (UserCode != null) {
            stmt.bindString(8, UserCode);
        }
 
        Long VisitID = entity.getVisitID();
        if (VisitID != null) {
            stmt.bindLong(9, VisitID);
        }
 
        String JourneyCode = entity.getJourneyCode();
        if (JourneyCode != null) {
            stmt.bindString(10, JourneyCode);
        }
 
        java.util.Date IssueDate = entity.getIssueDate();
        if (IssueDate != null) {
            stmt.bindLong(11, IssueDate.getTime());
        }
 
        java.util.Date DeliveryDate = entity.getDeliveryDate();
        if (DeliveryDate != null) {
            stmt.bindLong(12, DeliveryDate.getTime());
        }
 
        Double GrossAmount = entity.getGrossAmount();
        if (GrossAmount != null) {
            stmt.bindDouble(13, GrossAmount);
        }
 
        Double DiscountAmount = entity.getDiscountAmount();
        if (DiscountAmount != null) {
            stmt.bindDouble(14, DiscountAmount);
        }
 
        Double NetAmount = entity.getNetAmount();
        if (NetAmount != null) {
            stmt.bindDouble(15, NetAmount);
        }
 
        Double TaxAmount = entity.getTaxAmount();
        if (TaxAmount != null) {
            stmt.bindDouble(16, TaxAmount);
        }
 
        Double TotalTaxAmount = entity.getTotalTaxAmount();
        if (TotalTaxAmount != null) {
            stmt.bindDouble(17, TotalTaxAmount);
        }
 
        Double RemainingAmount = entity.getRemainingAmount();
        if (RemainingAmount != null) {
            stmt.bindDouble(18, RemainingAmount);
        }
 
        Integer TransactionStatus = entity.getTransactionStatus();
        if (TransactionStatus != null) {
            stmt.bindLong(19, TransactionStatus);
        }
 
        String Info1 = entity.getInfo1();
        if (Info1 != null) {
            stmt.bindString(20, Info1);
        }
 
        String Info2 = entity.getInfo2();
        if (Info2 != null) {
            stmt.bindString(21, Info2);
        }
 
        String Info3 = entity.getInfo3();
        if (Info3 != null) {
            stmt.bindString(22, Info3);
        }
 
        String Info4 = entity.getInfo4();
        if (Info4 != null) {
            stmt.bindString(23, Info4);
        }
 
        String Info5 = entity.getInfo5();
        if (Info5 != null) {
            stmt.bindString(24, Info5);
        }
 
        String ApprovedRequestReturnReference = entity.getApprovedRequestReturnReference();
        if (ApprovedRequestReturnReference != null) {
            stmt.bindString(25, ApprovedRequestReturnReference);
        }
 
        String TransactionPasswordCode = entity.getTransactionPasswordCode();
        if (TransactionPasswordCode != null) {
            stmt.bindString(26, TransactionPasswordCode);
        }
 
        String Notes = entity.getNotes();
        if (Notes != null) {
            stmt.bindString(27, Notes);
        }
 
        String ClientReferenceNumber = entity.getClientReferenceNumber();
        if (ClientReferenceNumber != null) {
            stmt.bindString(28, ClientReferenceNumber);
        }
 
        Integer PrintingTimes = entity.getPrintingTimes();
        if (PrintingTimes != null) {
            stmt.bindLong(29, PrintingTimes);
        }
 
        Integer DeviceType = entity.getDeviceType();
        if (DeviceType != null) {
            stmt.bindLong(30, DeviceType);
        }
        stmt.bindLong(31, entity.getIsProcessed());
        stmt.bindLong(32, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public TransactionHeaders readEntity(Cursor cursor, int offset) {
        TransactionHeaders entity = new TransactionHeaders( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // TransactionCode
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // TransactionType
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // DivisionCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // CompanyCode
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // CurrencyCode
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // ClientCode
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // UserCode
            cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8), // VisitID
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // JourneyCode
            cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)), // IssueDate
            cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)), // DeliveryDate
            cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12), // GrossAmount
            cursor.isNull(offset + 13) ? null : cursor.getDouble(offset + 13), // DiscountAmount
            cursor.isNull(offset + 14) ? null : cursor.getDouble(offset + 14), // NetAmount
            cursor.isNull(offset + 15) ? null : cursor.getDouble(offset + 15), // TaxAmount
            cursor.isNull(offset + 16) ? null : cursor.getDouble(offset + 16), // TotalTaxAmount
            cursor.isNull(offset + 17) ? null : cursor.getDouble(offset + 17), // RemainingAmount
            cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18), // TransactionStatus
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // Info1
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // Info2
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // Info3
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // Info4
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // Info5
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // ApprovedRequestReturnReference
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // TransactionPasswordCode
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // Notes
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27), // ClientReferenceNumber
            cursor.isNull(offset + 28) ? null : cursor.getInt(offset + 28), // PrintingTimes
            cursor.isNull(offset + 29) ? null : cursor.getInt(offset + 29), // DeviceType
            cursor.getInt(offset + 30), // IsProcessed
            new java.util.Date(cursor.getLong(offset + 31)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TransactionHeaders entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTransactionCode(cursor.getString(offset + 1));
        entity.setTransactionType(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setDivisionCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCompanyCode(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCurrencyCode(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setClientCode(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setUserCode(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setVisitID(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
        entity.setJourneyCode(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setIssueDate(cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)));
        entity.setDeliveryDate(cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)));
        entity.setGrossAmount(cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12));
        entity.setDiscountAmount(cursor.isNull(offset + 13) ? null : cursor.getDouble(offset + 13));
        entity.setNetAmount(cursor.isNull(offset + 14) ? null : cursor.getDouble(offset + 14));
        entity.setTaxAmount(cursor.isNull(offset + 15) ? null : cursor.getDouble(offset + 15));
        entity.setTotalTaxAmount(cursor.isNull(offset + 16) ? null : cursor.getDouble(offset + 16));
        entity.setRemainingAmount(cursor.isNull(offset + 17) ? null : cursor.getDouble(offset + 17));
        entity.setTransactionStatus(cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18));
        entity.setInfo1(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setInfo2(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setInfo3(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setInfo4(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setInfo5(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setApprovedRequestReturnReference(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setTransactionPasswordCode(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setNotes(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setClientReferenceNumber(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
        entity.setPrintingTimes(cursor.isNull(offset + 28) ? null : cursor.getInt(offset + 28));
        entity.setDeviceType(cursor.isNull(offset + 29) ? null : cursor.getInt(offset + 29));
        entity.setIsProcessed(cursor.getInt(offset + 30));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 31)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(TransactionHeaders entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(TransactionHeaders entity) {
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
