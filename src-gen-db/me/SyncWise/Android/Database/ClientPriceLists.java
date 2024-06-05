package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ClientPriceLists.
 */
public class ClientPriceLists implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String ClientCode;
    /** Not-null value. */
    private String PriceListCode;
    /** Not-null value. */
    private String DivisionCode;
    /** Not-null value. */
    private String CompanyCode;
    private Integer Priority;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public ClientPriceLists() {
    }

    public ClientPriceLists(Long id) {
        this.id = id;
    }

    public ClientPriceLists(Long id, String ClientCode, String PriceListCode, String DivisionCode, String CompanyCode, Integer Priority, java.util.Date StampDate) {
        this.id = id;
        this.ClientCode = ClientCode;
        this.PriceListCode = PriceListCode;
        this.DivisionCode = DivisionCode;
        this.CompanyCode = CompanyCode;
        this.Priority = Priority;
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
    public String getPriceListCode() {
        return PriceListCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPriceListCode(String PriceListCode) {
        this.PriceListCode = PriceListCode;
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

    public Integer getPriority() {
        return Priority;
    }

    public void setPriority(Integer Priority) {
        this.Priority = Priority;
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