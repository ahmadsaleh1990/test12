package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table Units.
 */
public class Units implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String UnitCode;
    private String UnitBigDescription;
    private String UnitBigAltDescription;
    private String UnitMediumDescription;
    private String UnitMediumAltDescription;
    private String UnitSmallDescription;
    private String UnitSmallAltDescription;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public Units() {
    }

    public Units(Long id) {
        this.id = id;
    }

    public Units(Long id, String UnitCode, String UnitBigDescription, String UnitBigAltDescription, String UnitMediumDescription, String UnitMediumAltDescription, String UnitSmallDescription, String UnitSmallAltDescription, java.util.Date StampDate) {
        this.id = id;
        this.UnitCode = UnitCode;
        this.UnitBigDescription = UnitBigDescription;
        this.UnitBigAltDescription = UnitBigAltDescription;
        this.UnitMediumDescription = UnitMediumDescription;
        this.UnitMediumAltDescription = UnitMediumAltDescription;
        this.UnitSmallDescription = UnitSmallDescription;
        this.UnitSmallAltDescription = UnitSmallAltDescription;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUnitCode() {
        return UnitCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUnitCode(String UnitCode) {
        this.UnitCode = UnitCode;
    }

    public String getUnitBigDescription() {
        return UnitBigDescription;
    }

    public void setUnitBigDescription(String UnitBigDescription) {
        this.UnitBigDescription = UnitBigDescription;
    }

    public String getUnitBigAltDescription() {
        return UnitBigAltDescription;
    }

    public void setUnitBigAltDescription(String UnitBigAltDescription) {
        this.UnitBigAltDescription = UnitBigAltDescription;
    }

    public String getUnitMediumDescription() {
        return UnitMediumDescription;
    }

    public void setUnitMediumDescription(String UnitMediumDescription) {
        this.UnitMediumDescription = UnitMediumDescription;
    }

    public String getUnitMediumAltDescription() {
        return UnitMediumAltDescription;
    }

    public void setUnitMediumAltDescription(String UnitMediumAltDescription) {
        this.UnitMediumAltDescription = UnitMediumAltDescription;
    }

    public String getUnitSmallDescription() {
        return UnitSmallDescription;
    }

    public void setUnitSmallDescription(String UnitSmallDescription) {
        this.UnitSmallDescription = UnitSmallDescription;
    }

    public String getUnitSmallAltDescription() {
        return UnitSmallAltDescription;
    }

    public void setUnitSmallAltDescription(String UnitSmallAltDescription) {
        this.UnitSmallAltDescription = UnitSmallAltDescription;
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
