package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TargetAchievements.
 */
public class TargetAchievements implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String TargetCode;
    private int LineID;
    /** Not-null value. */
    private String SubjectCode;
    private Integer TargetDetailType;
    /** Not-null value. */
    private String CompanyCode;
    /** Not-null value. */
    private String DivisionCode;
    private Double AmountAcheived;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public TargetAchievements() {
    }

    public TargetAchievements(Long id) {
        this.id = id;
    }

    public TargetAchievements(Long id, String TargetCode, int LineID, String SubjectCode, Integer TargetDetailType, String CompanyCode, String DivisionCode, Double AmountAcheived, java.util.Date StampDate) {
        this.id = id;
        this.TargetCode = TargetCode;
        this.LineID = LineID;
        this.SubjectCode = SubjectCode;
        this.TargetDetailType = TargetDetailType;
        this.CompanyCode = CompanyCode;
        this.DivisionCode = DivisionCode;
        this.AmountAcheived = AmountAcheived;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getTargetCode() {
        return TargetCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTargetCode(String TargetCode) {
        this.TargetCode = TargetCode;
    }

    public int getLineID() {
        return LineID;
    }

    public void setLineID(int LineID) {
        this.LineID = LineID;
    }

    /** Not-null value. */
    public String getSubjectCode() {
        return SubjectCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSubjectCode(String SubjectCode) {
        this.SubjectCode = SubjectCode;
    }

    public Integer getTargetDetailType() {
        return TargetDetailType;
    }

    public void setTargetDetailType(Integer TargetDetailType) {
        this.TargetDetailType = TargetDetailType;
    }

    /** Not-null value. */
    public String getCompanyCode() {
        return CompanyCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCompanyCode(String CompanyCode) {
        this.CompanyCode = CompanyCode;
    }

    /** Not-null value. */
    public String getDivisionCode() {
        return DivisionCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDivisionCode(String DivisionCode) {
        this.DivisionCode = DivisionCode;
    }

    public Double getAmountAcheived() {
        return AmountAcheived;
    }

    public void setAmountAcheived(Double AmountAcheived) {
        this.AmountAcheived = AmountAcheived;
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
