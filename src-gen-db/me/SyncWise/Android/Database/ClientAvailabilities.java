package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ClientAvailabilities.
 */
public class ClientAvailabilities implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String UserCode;
    /** Not-null value. */
    private String ClientCode;
    /** Not-null value. */
    private String DivisionCode;
    /** Not-null value. */
    private String CompanyCode;
    /** Not-null value. */
    private String Day;
    private long AvailabilityID;
    private java.util.Date StartTime;
    private java.util.Date EndTime;
    private Integer AvailabilityStatus;
    private int IsProcessed;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public ClientAvailabilities() {
    }

    public ClientAvailabilities(Long id) {
        this.id = id;
    }

    public ClientAvailabilities(Long id, String UserCode, String ClientCode, String DivisionCode, String CompanyCode, String Day, long AvailabilityID, java.util.Date StartTime, java.util.Date EndTime, Integer AvailabilityStatus, int IsProcessed, java.util.Date StampDate) {
        this.id = id;
        this.UserCode = UserCode;
        this.ClientCode = ClientCode;
        this.DivisionCode = DivisionCode;
        this.CompanyCode = CompanyCode;
        this.Day = Day;
        this.AvailabilityID = AvailabilityID;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.AvailabilityStatus = AvailabilityStatus;
        this.IsProcessed = IsProcessed;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    /** Not-null value. */
    public String getDivisionCode() {
        return DivisionCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDivisionCode(String DivisionCode) {
        this.DivisionCode = DivisionCode;
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
    public String getDay() {
        return Day;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDay(String Day) {
        this.Day = Day;
    }

    public long getAvailabilityID() {
        return AvailabilityID;
    }

    public void setAvailabilityID(long AvailabilityID) {
        this.AvailabilityID = AvailabilityID;
    }

    public java.util.Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(java.util.Date StartTime) {
        this.StartTime = StartTime;
    }

    public java.util.Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(java.util.Date EndTime) {
        this.EndTime = EndTime;
    }

    public Integer getAvailabilityStatus() {
        return AvailabilityStatus;
    }

    public void setAvailabilityStatus(Integer AvailabilityStatus) {
        this.AvailabilityStatus = AvailabilityStatus;
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