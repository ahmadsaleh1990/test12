package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table WareHouseBarcodes.
 */
public class WareHouseBarcodes implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String WareHouseCode;
    /** Not-null value. */
    private String CompanyCode;
    /** Not-null value. */
    private String DivisionCode;
    /** Not-null value. */
    private String Barcode;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public WareHouseBarcodes() {
    }

    public WareHouseBarcodes(Long id) {
        this.id = id;
    }

    public WareHouseBarcodes(Long id, String WareHouseCode, String CompanyCode, String DivisionCode, String Barcode, java.util.Date StampDate) {
        this.id = id;
        this.WareHouseCode = WareHouseCode;
        this.CompanyCode = CompanyCode;
        this.DivisionCode = DivisionCode;
        this.Barcode = Barcode;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getWareHouseCode() {
        return WareHouseCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setWareHouseCode(String WareHouseCode) {
        this.WareHouseCode = WareHouseCode;
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

    /** Not-null value. */
    public String getBarcode() {
        return Barcode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setBarcode(String Barcode) {
        this.Barcode = Barcode;
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
