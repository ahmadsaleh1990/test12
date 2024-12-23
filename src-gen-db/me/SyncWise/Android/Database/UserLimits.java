package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table UserLimits.
 */
public class UserLimits implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String UserCode;
    /** Not-null value. */
    private String CurrencyCode;
    /** Not-null value. */
    private String CompanyCode;
    private double UserLoadLimit;
    private double UserCreditLimit;
    private double UserBalance;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public UserLimits() {
    }

    public UserLimits(Long id) {
        this.id = id;
    }

    public UserLimits(Long id, String UserCode, String CurrencyCode, String CompanyCode, double UserLoadLimit, double UserCreditLimit, double UserBalance, java.util.Date StampDate) {
        this.id = id;
        this.UserCode = UserCode;
        this.CurrencyCode = CurrencyCode;
        this.CompanyCode = CompanyCode;
        this.UserLoadLimit = UserLoadLimit;
        this.UserCreditLimit = UserCreditLimit;
        this.UserBalance = UserBalance;
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
    public String getCurrencyCode() {
        return CurrencyCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCurrencyCode(String CurrencyCode) {
        this.CurrencyCode = CurrencyCode;
    }

    /** Not-null value. */
    public String getCompanyCode() {
        return CompanyCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCompanyCode(String CompanyCode) {
        this.CompanyCode = CompanyCode;
    }

    public double getUserLoadLimit() {
        return UserLoadLimit;
    }

    public void setUserLoadLimit(double UserLoadLimit) {
        this.UserLoadLimit = UserLoadLimit;
    }

    public double getUserCreditLimit() {
        return UserCreditLimit;
    }

    public void setUserCreditLimit(double UserCreditLimit) {
        this.UserCreditLimit = UserCreditLimit;
    }

    public double getUserBalance() {
        return UserBalance;
    }

    public void setUserBalance(double UserBalance) {
        this.UserBalance = UserBalance;
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
