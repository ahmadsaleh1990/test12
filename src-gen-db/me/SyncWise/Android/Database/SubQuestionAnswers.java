package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SubQuestionAnswers.
 */
public class SubQuestionAnswers implements java.io.Serializable {

    private Long id;
    private Long SurveyID;
    private Integer QuestionID;
    private Integer ParentQuestionAnswerID;
    private Integer ParentLineID;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public SubQuestionAnswers() {
    }

    public SubQuestionAnswers(Long id) {
        this.id = id;
    }

    public SubQuestionAnswers(Long id, Long SurveyID, Integer QuestionID, Integer ParentQuestionAnswerID, Integer ParentLineID, java.util.Date StampDate) {
        this.id = id;
        this.SurveyID = SurveyID;
        this.QuestionID = QuestionID;
        this.ParentQuestionAnswerID = ParentQuestionAnswerID;
        this.ParentLineID = ParentLineID;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyID() {
        return SurveyID;
    }

    public void setSurveyID(Long SurveyID) {
        this.SurveyID = SurveyID;
    }

    public Integer getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(Integer QuestionID) {
        this.QuestionID = QuestionID;
    }

    public Integer getParentQuestionAnswerID() {
        return ParentQuestionAnswerID;
    }

    public void setParentQuestionAnswerID(Integer ParentQuestionAnswerID) {
        this.ParentQuestionAnswerID = ParentQuestionAnswerID;
    }

    public Integer getParentLineID() {
        return ParentLineID;
    }

    public void setParentLineID(Integer ParentLineID) {
        this.ParentLineID = ParentLineID;
    }

    /** Not-null value. */
    public java.util.Date getStampDate() {
        return StampDate;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setStampDate(java.util.Date StampDate) {
        this.StampDate = StampDate;
    }

}
