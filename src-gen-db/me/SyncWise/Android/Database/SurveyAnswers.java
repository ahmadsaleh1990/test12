package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SurveyAnswers.
 */
public class SurveyAnswers implements java.io.Serializable {

    private Long id;
    private long SurveyID;
    private int QuestionID;
    private int LineID;
    private int SurveyType;
    private long SurveyAnswerID;
    /** Not-null value. */
    private String UserCode;
    /** Not-null value. */
    private String ClientCode;
    private String DivisionCode;
    private String CompanyCode;
    private Long VisitID;
    private String AnswerDescription;
    private Integer AnswerStatus;
    private String QuestionDescription;
    private String QuestionAltDescription;
    private String SelectionCode;
    private int IsProcessed;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public SurveyAnswers() {
    }

    public SurveyAnswers(Long id) {
        this.id = id;
    }

    public SurveyAnswers(Long id, long SurveyID, int QuestionID, int LineID, int SurveyType, long SurveyAnswerID, String UserCode, String ClientCode, String DivisionCode, String CompanyCode, Long VisitID, String AnswerDescription, Integer AnswerStatus, String QuestionDescription, String QuestionAltDescription, String SelectionCode, int IsProcessed, java.util.Date StampDate) {
        this.id = id;
        this.SurveyID = SurveyID;
        this.QuestionID = QuestionID;
        this.LineID = LineID;
        this.SurveyType = SurveyType;
        this.SurveyAnswerID = SurveyAnswerID;
        this.UserCode = UserCode;
        this.ClientCode = ClientCode;
        this.DivisionCode = DivisionCode;
        this.CompanyCode = CompanyCode;
        this.VisitID = VisitID;
        this.AnswerDescription = AnswerDescription;
        this.AnswerStatus = AnswerStatus;
        this.QuestionDescription = QuestionDescription;
        this.QuestionAltDescription = QuestionAltDescription;
        this.SelectionCode = SelectionCode;
        this.IsProcessed = IsProcessed;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSurveyID() {
        return SurveyID;
    }

    public void setSurveyID(long SurveyID) {
        this.SurveyID = SurveyID;
    }

    public int getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(int QuestionID) {
        this.QuestionID = QuestionID;
    }

    public int getLineID() {
        return LineID;
    }

    public void setLineID(int LineID) {
        this.LineID = LineID;
    }

    public int getSurveyType() {
        return SurveyType;
    }

    public void setSurveyType(int SurveyType) {
        this.SurveyType = SurveyType;
    }

    public long getSurveyAnswerID() {
        return SurveyAnswerID;
    }

    public void setSurveyAnswerID(long SurveyAnswerID) {
        this.SurveyAnswerID = SurveyAnswerID;
    }

    /** Not-null value. */
    public String getUserCode() {
        return UserCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserCode(String UserCode) {
        this.UserCode = UserCode;
    }

    /** Not-null value. */
    public String getClientCode() {
        return ClientCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setClientCode(String ClientCode) {
        this.ClientCode = ClientCode;
    }

    public String getDivisionCode() {
        return DivisionCode;
    }

    public void setDivisionCode(String DivisionCode) {
        this.DivisionCode = DivisionCode;
    }

    public String getCompanyCode() {
        return CompanyCode;
    }

    public void setCompanyCode(String CompanyCode) {
        this.CompanyCode = CompanyCode;
    }

    public Long getVisitID() {
        return VisitID;
    }

    public void setVisitID(Long VisitID) {
        this.VisitID = VisitID;
    }

    public String getAnswerDescription() {
        return AnswerDescription;
    }

    public void setAnswerDescription(String AnswerDescription) {
        this.AnswerDescription = AnswerDescription;
    }

    public Integer getAnswerStatus() {
        return AnswerStatus;
    }

    public void setAnswerStatus(Integer AnswerStatus) {
        this.AnswerStatus = AnswerStatus;
    }

    public String getQuestionDescription() {
        return QuestionDescription;
    }

    public void setQuestionDescription(String QuestionDescription) {
        this.QuestionDescription = QuestionDescription;
    }

    public String getQuestionAltDescription() {
        return QuestionAltDescription;
    }

    public void setQuestionAltDescription(String QuestionAltDescription) {
        this.QuestionAltDescription = QuestionAltDescription;
    }

    public String getSelectionCode() {
        return SelectionCode;
    }

    public void setSelectionCode(String SelectionCode) {
        this.SelectionCode = SelectionCode;
    }

    public int getIsProcessed() {
        return IsProcessed;
    }

    public void setIsProcessed(int IsProcessed) {
        this.IsProcessed = IsProcessed;
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