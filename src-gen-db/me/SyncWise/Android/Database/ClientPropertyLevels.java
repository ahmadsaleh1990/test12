package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ClientPropertyLevels.
 */
public class ClientPropertyLevels implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String ClientPropertyLevelCode;
    private String ClientPropertyLevelDescription;
    private String ClientPropertyLevelAltDescription;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public ClientPropertyLevels() {
    }

    public ClientPropertyLevels(Long id) {
        this.id = id;
    }

    public ClientPropertyLevels(Long id, String ClientPropertyLevelCode, String ClientPropertyLevelDescription, String ClientPropertyLevelAltDescription, java.util.Date StampDate) {
        this.id = id;
        this.ClientPropertyLevelCode = ClientPropertyLevelCode;
        this.ClientPropertyLevelDescription = ClientPropertyLevelDescription;
        this.ClientPropertyLevelAltDescription = ClientPropertyLevelAltDescription;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getClientPropertyLevelCode() {
        return ClientPropertyLevelCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setClientPropertyLevelCode(String ClientPropertyLevelCode) {
        this.ClientPropertyLevelCode = ClientPropertyLevelCode;
    }

    public String getClientPropertyLevelDescription() {
        return ClientPropertyLevelDescription;
    }

    public void setClientPropertyLevelDescription(String ClientPropertyLevelDescription) {
        this.ClientPropertyLevelDescription = ClientPropertyLevelDescription;
    }

    public String getClientPropertyLevelAltDescription() {
        return ClientPropertyLevelAltDescription;
    }

    public void setClientPropertyLevelAltDescription(String ClientPropertyLevelAltDescription) {
        this.ClientPropertyLevelAltDescription = ClientPropertyLevelAltDescription;
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