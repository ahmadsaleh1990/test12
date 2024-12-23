package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.QuestionAnswers;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table QuestionAnswers.
*/
public class QuestionAnswersDao extends AbstractDao<QuestionAnswers, Long> {

    public static final String TABLENAME = "QuestionAnswers";

    /**
     * Properties of entity QuestionAnswers.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property QuestionAnswerID = new Property(1, int.class, "QuestionAnswerID", false, "QuestionAnswerID");
        public final static Property LineID = new Property(2, int.class, "LineID", false, "LineID");
        public final static Property QuestionAnswerDescription = new Property(3, String.class, "QuestionAnswerDescription", false, "QuestionAnswerDescription");
        public final static Property QuestionAnswerAltDescription = new Property(4, String.class, "QuestionAnswerAltDescription", false, "QuestionAnswerAltDescription");
        public final static Property StampDate = new Property(5, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public QuestionAnswersDao(DaoConfig config) {
        super(config);
    }
    
    public QuestionAnswersDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'QuestionAnswers' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'QuestionAnswerID' INTEGER NOT NULL ," + // 1: QuestionAnswerID
                "'LineID' INTEGER NOT NULL ," + // 2: LineID
                "'QuestionAnswerDescription' TEXT," + // 3: QuestionAnswerDescription
                "'QuestionAnswerAltDescription' TEXT," + // 4: QuestionAnswerAltDescription
                "'StampDate' INTEGER NOT NULL );"); // 5: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'QuestionAnswers'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, QuestionAnswers entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getQuestionAnswerID());
        stmt.bindLong(3, entity.getLineID());
 
        String QuestionAnswerDescription = entity.getQuestionAnswerDescription();
        if (QuestionAnswerDescription != null) {
            stmt.bindString(4, QuestionAnswerDescription);
        }
 
        String QuestionAnswerAltDescription = entity.getQuestionAnswerAltDescription();
        if (QuestionAnswerAltDescription != null) {
            stmt.bindString(5, QuestionAnswerAltDescription);
        }
        stmt.bindLong(6, entity.getStampDate().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public QuestionAnswers readEntity(Cursor cursor, int offset) {
        QuestionAnswers entity = new QuestionAnswers( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // QuestionAnswerID
            cursor.getInt(offset + 2), // LineID
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // QuestionAnswerDescription
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // QuestionAnswerAltDescription
            new java.util.Date(cursor.getLong(offset + 5)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, QuestionAnswers entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setQuestionAnswerID(cursor.getInt(offset + 1));
        entity.setLineID(cursor.getInt(offset + 2));
        entity.setQuestionAnswerDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setQuestionAnswerAltDescription(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(QuestionAnswers entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(QuestionAnswers entity) {
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
