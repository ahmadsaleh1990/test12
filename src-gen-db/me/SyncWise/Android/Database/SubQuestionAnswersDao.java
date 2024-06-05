package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.SubQuestionAnswers;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SubQuestionAnswers.
*/
public class SubQuestionAnswersDao extends AbstractDao<SubQuestionAnswers, Long> {

    public static final String TABLENAME = "SubQuestionAnswers";

    /**
     * Properties of entity SubQuestionAnswers.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property SurveyID = new Property(1, Long.class, "SurveyID", false, "SurveyID");
        public final static Property QuestionID = new Property(2, Integer.class, "QuestionID", false, "QuestionID");
        public final static Property ParentQuestionAnswerID = new Property(3, Integer.class, "ParentQuestionAnswerID", false, "ParentQuestionAnswerID");
        public final static Property ParentLineID = new Property(4, Integer.class, "ParentLineID", false, "ParentLineID");
        public final static Property StampDate = new Property(5, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public SubQuestionAnswersDao(DaoConfig config) {
        super(config);
    }
    
    public SubQuestionAnswersDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SubQuestionAnswers' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'SurveyID' INTEGER," + // 1: SurveyID
                "'QuestionID' INTEGER," + // 2: QuestionID
                "'ParentQuestionAnswerID' INTEGER," + // 3: ParentQuestionAnswerID
                "'ParentLineID' INTEGER," + // 4: ParentLineID
                "'StampDate' INTEGER NOT NULL );"); // 5: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SubQuestionAnswers'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SubQuestionAnswers entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long SurveyID = entity.getSurveyID();
        if (SurveyID != null) {
            stmt.bindLong(2, SurveyID);
        }
 
        Integer QuestionID = entity.getQuestionID();
        if (QuestionID != null) {
            stmt.bindLong(3, QuestionID);
        }
 
        Integer ParentQuestionAnswerID = entity.getParentQuestionAnswerID();
        if (ParentQuestionAnswerID != null) {
            stmt.bindLong(4, ParentQuestionAnswerID);
        }
 
        Integer ParentLineID = entity.getParentLineID();
        if (ParentLineID != null) {
            stmt.bindLong(5, ParentLineID);
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
    public SubQuestionAnswers readEntity(Cursor cursor, int offset) {
        SubQuestionAnswers entity = new SubQuestionAnswers( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // SurveyID
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // QuestionID
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // ParentQuestionAnswerID
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // ParentLineID
            new java.util.Date(cursor.getLong(offset + 5)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SubQuestionAnswers entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSurveyID(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setQuestionID(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setParentQuestionAnswerID(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setParentLineID(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SubQuestionAnswers entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SubQuestionAnswers entity) {
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
