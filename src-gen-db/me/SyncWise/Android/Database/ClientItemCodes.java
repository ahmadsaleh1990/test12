package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ClientItemCodes.
 */
public class ClientItemCodes implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String ClientCode;
    private int LineID;
    /** Not-null value. */
    private String DivisionCode;
    /** Not-null value. */
    private String CompanyCode;
    /** Not-null value. */
    private String ItemCode;
    /** Not-null value. */
    private String ClientItemCode;
    private String ClientItemName;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public ClientItemCodes() {
    }

    public ClientItemCodes(Long id) {
        this.id = id;
    }

    public ClientItemCodes(Long id, String ClientCode, int LineID, String DivisionCode, String CompanyCode, String ItemCode, String ClientItemCode, String ClientItemName, java.util.Date StampDate) {
        this.id = id;
        this.ClientCode = ClientCode;
        this.LineID = LineID;
        this.DivisionCode = DivisionCode;
        this.CompanyCode = CompanyCode;
        this.ItemCode = ItemCode;
        this.ClientItemCode = ClientItemCode;
        this.ClientItemName = ClientItemName;
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

    public int getLineID() {
        return LineID;
    }

    public void setLineID(int LineID) {
        this.LineID = LineID;
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
    public String getItemCode() {
        return ItemCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setItemCode(String ItemCode) {
        this.ItemCode = ItemCode;
    }

    /** Not-null value. */
    public String getClientItemCode() {
        return ClientItemCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setClientItemCode(String ClientItemCode) {
        this.ClientItemCode = ClientItemCode;
    }

    public String getClientItemName() {
        return ClientItemName;
    }

    public void setClientItemName(String ClientItemName) {
        this.ClientItemName = ClientItemName;
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
