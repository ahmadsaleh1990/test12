package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table Permissions.
 */
public class Permissions implements java.io.Serializable {

    private Long id;
    private int PermissionID;
    private String PermissionName;
    private String PermissionAltName;
    private String PermissionDefaultValue;
    private Integer PermissionTypeID;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public Permissions() {
    }

    public Permissions(Long id) {
        this.id = id;
    }

    public Permissions(Long id, int PermissionID, String PermissionName, String PermissionAltName, String PermissionDefaultValue, Integer PermissionTypeID, java.util.Date StampDate) {
        this.id = id;
        this.PermissionID = PermissionID;
        this.PermissionName = PermissionName;
        this.PermissionAltName = PermissionAltName;
        this.PermissionDefaultValue = PermissionDefaultValue;
        this.PermissionTypeID = PermissionTypeID;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPermissionID() {
        return PermissionID;
    }

    public void setPermissionID(int PermissionID) {
        this.PermissionID = PermissionID;
    }

    public String getPermissionName() {
        return PermissionName;
    }

    public void setPermissionName(String PermissionName) {
        this.PermissionName = PermissionName;
    }

    public String getPermissionAltName() {
        return PermissionAltName;
    }

    public void setPermissionAltName(String PermissionAltName) {
        this.PermissionAltName = PermissionAltName;
    }

    public String getPermissionDefaultValue() {
        return PermissionDefaultValue;
    }

    public void setPermissionDefaultValue(String PermissionDefaultValue) {
        this.PermissionDefaultValue = PermissionDefaultValue;
    }

    public Integer getPermissionTypeID() {
        return PermissionTypeID;
    }

    public void setPermissionTypeID(Integer PermissionTypeID) {
        this.PermissionTypeID = PermissionTypeID;
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
