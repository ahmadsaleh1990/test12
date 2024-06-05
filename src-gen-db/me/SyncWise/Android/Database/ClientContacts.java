package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ClientContacts.
 */
public class ClientContacts implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String ClientCode;
    /** Not-null value. */
    private String DivisionCode;
    /** Not-null value. */
    private String CompanyCode;
    private int ClientContactID;
    /** Not-null value. */
    private String UserCode;
    private String ClientContactName;
    private String ClientContactAltName;
    private String ClientContactPhone;
    private String ClientContactTitle;
    private Integer ClientContactSource;
    private int IsProcessed;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public ClientContacts() {
    }

    public ClientContacts(Long id) {
        this.id = id;
    }

    public ClientContacts(Long id, String ClientCode, String DivisionCode, String CompanyCode, int ClientContactID, String UserCode, String ClientContactName, String ClientContactAltName, String ClientContactPhone, String ClientContactTitle, Integer ClientContactSource, int IsProcessed, java.util.Date StampDate) {
        this.id = id;
        this.ClientCode = ClientCode;
        this.DivisionCode = DivisionCode;
        this.CompanyCode = CompanyCode;
        this.ClientContactID = ClientContactID;
        this.UserCode = UserCode;
        this.ClientContactName = ClientContactName;
        this.ClientContactAltName = ClientContactAltName;
        this.ClientContactPhone = ClientContactPhone;
        this.ClientContactTitle = ClientContactTitle;
        this.ClientContactSource = ClientContactSource;
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

    public int getClientContactID() {
        return ClientContactID;
    }

    public void setClientContactID(int ClientContactID) {
        this.ClientContactID = ClientContactID;
    }

    /** Not-null value. */
    public String getUserCode() {
        return UserCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserCode(String UserCode) {
        this.UserCode = UserCode;
    }

    public String getClientContactName() {
        return ClientContactName;
    }

    public void setClientContactName(String ClientContactName) {
        this.ClientContactName = ClientContactName;
    }

    public String getClientContactAltName() {
        return ClientContactAltName;
    }

    public void setClientContactAltName(String ClientContactAltName) {
        this.ClientContactAltName = ClientContactAltName;
    }

    public String getClientContactPhone() {
        return ClientContactPhone;
    }

    public void setClientContactPhone(String ClientContactPhone) {
        this.ClientContactPhone = ClientContactPhone;
    }

    public String getClientContactTitle() {
        return ClientContactTitle;
    }

    public void setClientContactTitle(String ClientContactTitle) {
        this.ClientContactTitle = ClientContactTitle;
    }

    public Integer getClientContactSource() {
        return ClientContactSource;
    }

    public void setClientContactSource(Integer ClientContactSource) {
        this.ClientContactSource = ClientContactSource;
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