package me.SyncWise.Android.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import me.SyncWise.Android.Database.SurveyQuestions;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SurveyQuestions.
*/
public class SurveyQuestionsDao extends AbstractDao<SurveyQuestions, Long> {

    public static final String TABLENAME = "SurveyQuestions";

    /**
     * Properties of entity SurveyQuestions.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property SurveyID = new Property(1, long.class, "SurveyID", false, "SurveyID");
        public final static Property QuestionID = new Property(2, int.class, "QuestionID", false, "QuestionID");
        public final static Property QuestionDescription = new Property(3, String.class, "QuestionDescription", false, "QuestionDescription");
        public final static Property QuestionAltDescription = new Property(4, String.class, "QuestionAltDescription", false, "QuestionAltDescription");
        public final static Property QuestionTypeID = new Property(5, Integer.class, "QuestionTypeID", false, "QuestionTypeID");
        public final static Property QuestionAnswerID = new Property(6, Integer.class, "QuestionAnswerID", false, "QuestionAnswerID");
        public final static Property IsForced = new Property(7, Integer.class, "IsForced", false, "IsForced");
        public final static Property ParentQuestionID = new Property(8, Integer.class, "ParentQuestionID", false, "ParentQuestionID");
        public final static Property IsMultipleAnswer = new Property(9, Integer.class, "IsMultipleAnswer", false, "IsMultipleAnswer");
        public final static Property MinimumValue = new Property(10, Integer.class, "MinimumValue", false, "MinimumValue");
        public final static Property MaximumValue = new Property(11, Integer.class, "MaximumValue", false, "MaximumValue");
        public final static Property StampDate = new Property(12, java.util.Date.class, "StampDate", false, "StampDate");
    };


    public SurveyQuestionsDao(DaoConfig config) {
        super(config);
    }
    
    public SurveyQuestionsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SurveyQuestions' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'SurveyID' INTEGER NOT NULL ," + // 1: SurveyID
                "'QuestionID' INTEGER NOT NULL ," + // 2: QuestionID
                "'QuestionDescription' TEXT," + // 3: QuestionDescription
                "'QuestionAltDescription' TEXT," + // 4: QuestionAltDescription
                "'QuestionTypeID' INTEGER," + // 5: QuestionTypeID
                "'QuestionAnswerID' INTEGER," + // 6: QuestionAnswerID
                "'IsForced' INTEGER," + // 7: IsForced
                "'ParentQuestionID' INTEGER," + // 8: ParentQuestionID
                "'IsMultipleAnswer' INTEGER," + // 9: IsMultipleAnswer
                "'MinimumValue' INTEGER," + // 10: MinimumValue
                "'MaximumValue' INTEGER," + // 11: MaximumValue
                "'StampDate' INTEGER NOT NULL );"); // 12: StampDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SurveyQuestions'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SurveyQuestions entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSurveyID());
        stmt.bindLong(3, entity.getQuestionID());
 
        String QuestionDescription = entity.getQuestionDescription();
        if (QuestionDescription != null) {
            stmt.bindString(4, QuestionDescription);
        }
 
        String QuestionAltDescription = entity.getQuestionAltDescription();
        if (QuestionAltDescription != null) {
            stmt.bindString(5, QuestionAltDescription);
        }
 
        Integer QuestionTypeID = entity.getQuestionTypeID();
        if (QuestionTypeID != null) {
            stmt.bindLong(6, QuestionTypeID);
        }
 
        Integer QuestionAnswerID = entity.getQuestionAnswerID();
        if (QuestionAnswerID != null) {
            stmt.bindLong(7, QuestionAnswerID);
        }
 
        Integer IsForced = entity.getIsForced();
        if (IsForced != null) {
            stmt.bindLong(8, IsForced);
        }
 
        Integer ParentQuestionID = entity.getParentQuestionID();
        if (ParentQuestionID != null) {
            stmt.bindLong(9, ParentQuestionID);
        }
 
        Integer IsMultipleAnswer = entity.getIsMultipleAnswer();
        if (IsMultipleAnswer != null) {
            stmt.bindLong(10, IsMultipleAnswer);
        }
 
        Integer MinimumValue = entity.getMinimumValue();
        if (MinimumValue != null) {
            stmt.bindLong(11, MinimumValue);
        }
 
        Integer MaximumValue = entity.getMaximumValue();
        if (MaximumValue != null) {
            stmt.bindLong(12, MaximumValue);
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
    public SurveyQuestions readEntity(Cursor cursor, int offset) {
        SurveyQuestions entity = new SurveyQuestions( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // SurveyID
            cursor.getInt(offset + 2), // QuestionID
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // QuestionDescription
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // QuestionAltDescription
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // QuestionTypeID
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // QuestionAnswerID
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // IsForced
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // ParentQuestionID
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // IsMultipleAnswer
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // MinimumValue
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // MaximumValue
            new java.util.Date(cursor.getLong(offset + 12)) // StampDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SurveyQuestions entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSurveyID(cursor.getLong(offset + 1));
        entity.setQuestionID(cursor.getInt(offset + 2));
        entity.setQuestionDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setQuestionAltDescription(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setQuestionTypeID(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setQuestionAnswerID(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setIsForced(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setParentQuestionID(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setIsMultipleAnswer(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setMinimumValue(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setMaximumValue(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setStampDate(new java.util.Date(cursor.getLong(offset + 12)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SurveyQuestions entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SurveyQuestions entity) {
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
