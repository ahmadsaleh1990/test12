package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.NewClients;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table NewClients.
*/
public class NewClientsDao extends AbstractDao<NewClients, Long> {

    public static final String TABLENAME = "NewClients";

    /**
     * Properties of entity NewClients.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ClientCode = new Property(1, String.class, "ClientCode", false, "ClientCode");
        public final static Property CompanyCode = new Property(2, String.class, "CompanyCode", false, "CompanyCode");
        public final static Property DivisionCode = new Property(3, String.class, "DivisionCode", false, "DivisionCode");
        public final static Property AccountNumber = new Property(4, String.class, "AccountNumber", false, "AccountNumber");
        public final static Property UserCode = new Property(5, String.class, "UserCode", false, "UserCode");
        public final static Property ClientName = new Property(6, String.class, "ClientName", false, "ClientName");
        public final static Property ClientAltName = new Property(7, String.class, "ClientAltName", false, "ClientAltName");
        public final static Property ClientPhone = new Property(8, String.class, "ClientPhone", false, "ClientPhone");
        public final static Property Mobile = new Property(9, String.class, "Mobile", false, "Mobile");
        public final static Property Fax = new Property(10, String.class, "Fax", false, "Fax");
        public final static Property Email = new Property(11, String.class, "Email", false, "Email");
        public final static Property FinacialNumber = new Property(12, String.class, "FinacialNumber", false, "FinacialNumber");
        public final static Property CNSS = new Property(13, String.class, "CNSS", false, "CNSS");
        public final static Property SyndicateNumber = new Property(14, String.class, "SyndicateNumber", false, "SyndicateNumber");
        public final static Property LicenseNumber = new Property(15, String.class, "LicenseNumber", false, "LicenseNumber");
        public final static Property ClientAddress = new Property(16, String.class, "ClientAddress", false, "ClientAddress");
        public final static Property Latitude = new Property(17, String.class, "Latitude", false, "Latitude");
        public final static Property Longitude = new Property(18, String.class, "Longitude", false, "Longitude");
        public final static Property IsProcessed = new Property(19, int.class, "IsProcessed", false, "IsProcessed");
        public final static Property StampDate = new Property(20, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public NewClientsDao(DaoConfig config) {
        super(config);
    }
    
    public NewClientsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'NewClients' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ClientCode' TEXT NOT NULL ," + // 1: ClientCode
                "'CompanyCode' TEXT NOT NULL ," + // 2: CompanyCode
                "'DivisionCode' TEXT NOT NULL ," + // 3: DivisionCode
                "'AccountNumber' TEXT," + // 4: AccountNumber
                "'UserCode' TEXT," + // 5: UserCode
                "'ClientName' TEXT," + // 6: ClientName
                "'ClientAltName' TEXT," + // 7: ClientAltName
                "'ClientPhone' TEXT," + // 8: ClientPhone
                "'Mobile' TEXT," + // 9: Mobile
                "'Fax' TEXT," + // 10: Fax
                "'Email' TEXT," + // 11: Email
                "'FinacialNumber' TEXT," + // 12: FinacialNumber
                "'CNSS' TEXT," + // 13: CNSS
                "'SyndicateNumber' TEXT," + // 14: SyndicateNumber
                "'LicenseNumber' TEXT," + // 15: LicenseNumber
                "'ClientAddress' TEXT," + // 16: ClientAddress
                "'Latitude' TEXT," + // 17: Latitude
                "'Longitude' TEXT," + // 18: Longitude
                "'IsProcessed' INTEGER NOT NULL ," + // 19: IsProcessed
                "'StampDate' INTEGER NOT NULL );"); // 20: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'NewClients'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, NewClients entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getClientCode());
        stmt.bindString(3, entity.getCompanyCode());
        stmt.bindString(4, entity.getDivisionCode());
 
        String AccountNumber = entity.getAccountNumber();
        if (AccountNumber != null) {
            stmt.bindString(5, AccountNumber);
        }
 
        String UserCode = entity.getUserCode();
        if (UserCode != null) {
            stmt.bindString(6, UserCode);
        }
 
        String ClientName = entity.getClientName();
        if (ClientName != null) {
            stmt.bindString(7, ClientName);
        }
 
        String ClientAltName = entity.getClientAltName();
        if (ClientAltName != null) {
            stmt.bindString(8, ClientAltName);
        }
 
        String ClientPhone = entity.getClientPhone();
        if (ClientPhone != null) {
            stmt.bindString(9, ClientPhone);
        }
 
        String Mobile = entity.getMobile();
        if (Mobile != null) {
            stmt.bindString(10, Mobile);
        }
 
        String Fax = entity.getFax();
        if (Fax != null) {
            stmt.bindString(11, Fax);
        }
 
        String Email = entity.getEmail();
        if (Email != null) {
            stmt.bindString(12, Email);
        }
 
        String FinacialNumber = entity.getFinacialNumber();
        if (FinacialNumber != null) {
            stmt.bindString(13, FinacialNumber);
        }
 
        String CNSS = entity.getCNSS();
        if (CNSS != null) {
            stmt.bindString(14, CNSS);
        }
 
        String SyndicateNumber = entity.getSyndicateNumber();
        if (SyndicateNumber != null) {
            stmt.bindString(15, SyndicateNumber);
        }
 
        String LicenseNumber = entity.getLicenseNumber();
        if (LicenseNumber != null) {
            stmt.bindString(16, LicenseNumber);
        }
 
        String ClientAddress = entity.getClientAddress();
        if (ClientAddress != null) {
            stmt.bindString(17, ClientAddress);
        }
 
        String Latitude = entity.getLatitude();
        if (Latitude != null) {
            stmt.bindString(18, Latitude);
        }
 
        String Longitude = entity.getLongitude();
        if (Longitude != null) {
            stmt.bindString(19, Longitude);
        }
        stmt.bindLong(20, entity.getIsProcessed());
        stmt.bindLong(21, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public NewClients readEntity(Cursor cursor, int offset) {
        NewClients entity = new NewClients( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // ClientCode
            cursor.getString(offset + 2), // CompanyCode
            cursor.getString(offset + 3), // DivisionCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // AccountNumber
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // UserCode
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // ClientName
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // ClientAltName
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // ClientPhone
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // Mobile
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // Fax
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // Email
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // FinacialNumber
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // CNSS
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // SyndicateNumber
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // LicenseNumber
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // ClientAddress
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // Latitude
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // Longitude
            cursor.getInt(offset + 19), // IsProcessed
            new java.util.Date(cursor.getLong(offset + 20)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, NewClients entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setClientCode(cursor.getString(offset + 1));
        entity.setCompanyCode(cursor.getString(offset + 2));
        entity.setDivisionCode(cursor.getString(offset + 3));
        entity.setAccountNumber(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUserCode(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setClientName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setClientAltName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setClientPhone(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setMobile(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setFax(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setEmail(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setFinacialNumber(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setCNSS(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setSyndicateNumber(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setLicenseNumber(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setClientAddress(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setLatitude(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setLongitude(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setIsProcessed(cursor.getInt(offset + 19));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 20)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(NewClients entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(NewClients entity) {
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
