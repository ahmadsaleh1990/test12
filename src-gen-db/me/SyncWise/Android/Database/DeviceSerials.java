package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table DeviceSerials.
 */
public class DeviceSerials implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String DeviceSerialCode;
    /** Not-null value. */
    private String UserCode;
    /** Not-null value. */
    private String CompanyCode;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public DeviceSerials() {
    }

    public DeviceSerials(Long id) {
        this.id = id;
    }

    public DeviceSerials(Long id, String DeviceSerialCode, String UserCode, String CompanyCode, java.util.Date StampDate) {
        this.id = id;
        this.DeviceSerialCode = DeviceSerialCode;
        this.UserCode = UserCode;
        this.CompanyCode = CompanyCode;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getDeviceSerialCode() {
        return DeviceSerialCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDeviceSerialCode(String DeviceSerialCode) {
        this.DeviceSerialCode = DeviceSerialCode;
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
    public java.util.Date getStampDate() {
        return StampDate;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setStampDate(java.util.Date StampDate) {
        this.StampDate = StampDate;
    }

}