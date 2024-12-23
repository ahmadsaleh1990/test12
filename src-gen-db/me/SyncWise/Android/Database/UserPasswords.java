package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table UserPasswords.
 */
public class UserPasswords implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String PasswordCode;
    private int PasswordType;
    /** Not-null value. */
    private String UserCode;
    /** Not-null value. */
    private String CompanyCode;
    /** Not-null value. */
    private String DestinationUserCode;
    /** Not-null value. */
    private String DestinationCompanyCode;
    private Integer TransactionHeaderType;
    private Integer TransactionDetailType;
    private java.util.Date StartTime;
    private java.util.Date EndTime;
    private int IsProcessed;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public UserPasswords() {
    }

    public UserPasswords(Long id) {
        this.id = id;
    }

    public UserPasswords(Long id, String PasswordCode, int PasswordType, String UserCode, String CompanyCode, String DestinationUserCode, String DestinationCompanyCode, Integer TransactionHeaderType, Integer TransactionDetailType, java.util.Date StartTime, java.util.Date EndTime, int IsProcessed, java.util.Date StampDate) {
        this.id = id;
        this.PasswordCode = PasswordCode;
        this.PasswordType = PasswordType;
        this.UserCode = UserCode;
        this.CompanyCode = CompanyCode;
        this.DestinationUserCode = DestinationUserCode;
        this.DestinationCompanyCode = DestinationCompanyCode;
        this.TransactionHeaderType = TransactionHeaderType;
        this.TransactionDetailType = TransactionDetailType;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
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
    public String getPasswordCode() {
        return PasswordCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPasswordCode(String PasswordCode) {
        this.PasswordCode = PasswordCode;
    }

    public int getPasswordType() {
        return PasswordType;
    }

    public void setPasswordType(int PasswordType) {
        this.PasswordType = PasswordType;
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
    public String getCompanyCode() {
        return CompanyCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCompanyCode(String CompanyCode) {
        this.CompanyCode = CompanyCode;
    }

    /** Not-null value. */
    public String getDestinationUserCode() {
        return DestinationUserCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDestinationUserCode(String DestinationUserCode) {
        this.DestinationUserCode = DestinationUserCode;
    }

    /** Not-null value. */
    public String getDestinationCompanyCode() {
        return DestinationCompanyCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDestinationCompanyCode(String DestinationCompanyCode) {
        this.DestinationCompanyCode = DestinationCompanyCode;
    }

    public Integer getTransactionHeaderType() {
        return TransactionHeaderType;
    }

    public void setTransactionHeaderType(Integer TransactionHeaderType) {
        this.TransactionHeaderType = TransactionHeaderType;
    }

    public Integer getTransactionDetailType() {
        return TransactionDetailType;
    }

    public void setTransactionDetailType(Integer TransactionDetailType) {
        this.TransactionDetailType = TransactionDetailType;
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
