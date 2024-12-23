package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table Surveys.
 */
public class Surveys implements java.io.Serializable {

    private Long id;
    private long SurveyID;
    private String CompanyCode;
    private String SurveyDescription;
    private String SurveyAltDescription;
    private Integer SurveyType;
    private Integer IsForced;
    private java.util.Date StartDate;
    private java.util.Date EndDate;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public Surveys() {
    }

    public Surveys(Long id) {
        this.id = id;
    }

    public Surveys(Long id, long SurveyID, String CompanyCode, String SurveyDescription, String SurveyAltDescription, Integer SurveyType, Integer IsForced, java.util.Date StartDate, java.util.Date EndDate, java.util.Date StampDate) {
        this.id = id;
        this.SurveyID = SurveyID;
        this.CompanyCode = CompanyCode;
        this.SurveyDescription = SurveyDescription;
        this.SurveyAltDescription = SurveyAltDescription;
        this.SurveyType = SurveyType;
        this.IsForced = IsForced;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
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

    public String getCompanyCode() {
        return CompanyCode;
    }

    public void setCompanyCode(String CompanyCode) {
        this.CompanyCode = CompanyCode;
    }

    public String getSurveyDescription() {
        return SurveyDescription;
    }

    public void setSurveyDescription(String SurveyDescription) {
        this.SurveyDescription = SurveyDescription;
    }

    public String getSurveyAltDescription() {
        return SurveyAltDescription;
    }

    public void setSurveyAltDescription(String SurveyAltDescription) {
        this.SurveyAltDescription = SurveyAltDescription;
    }

    public Integer getSurveyType() {
        return SurveyType;
    }

    public void setSurveyType(Integer SurveyType) {
        this.SurveyType = SurveyType;
    }

    public Integer getIsForced() {
        return IsForced;
    }

    public void setIsForced(Integer IsForced) {
        this.IsForced = IsForced;
    }

    public java.util.Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(java.util.Date StartDate) {
        this.StartDate = StartDate;
    }

    public java.util.Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(java.util.Date EndDate) {
        this.EndDate = EndDate;
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
