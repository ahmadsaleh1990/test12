package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table NewSkuListDivisions.
 */
public class NewSkuListDivisions implements java.io.Serializable {

    private Long id;
    private String DivisionCode;
    private String CompanyCode;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public NewSkuListDivisions() {
    }

    public NewSkuListDivisions(Long id) {
        this.id = id;
    }

    public NewSkuListDivisions(Long id, String DivisionCode, String CompanyCode, java.util.Date StampDate) {
        this.id = id;
        this.DivisionCode = DivisionCode;
        this.CompanyCode = CompanyCode;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDivisionCode() {
        return DivisionCode;
    }

    public void setDivisionCode(String DivisionCode) {
        this.DivisionCode = DivisionCode;
    }

    public String getCompanyCode() {
        return CompanyCode;
    }

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
