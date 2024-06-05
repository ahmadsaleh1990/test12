package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CollectionInvoices.
 */
public class CollectionInvoices implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String CollectionCode;
    /** Not-null value. */
    private String InvoiceCode;
    /** Not-null value. */
    private String DivisionCode;
    /** Not-null value. */
    private String CompanyCode;
    private Double TotalAmount;
    private String CurrencyCode;
    private Integer InvoiceSource;
    private Double CurrencyRate;
    private java.util.Date InvoiceDate;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public CollectionInvoices() {
    }

    public CollectionInvoices(Long id) {
        this.id = id;
    }

    public CollectionInvoices(Long id, String CollectionCode, String InvoiceCode, String DivisionCode, String CompanyCode, Double TotalAmount, String CurrencyCode, Integer InvoiceSource, Double CurrencyRate, java.util.Date InvoiceDate, java.util.Date StampDate) {
        this.id = id;
        this.CollectionCode = CollectionCode;
        this.InvoiceCode = InvoiceCode;
        this.DivisionCode = DivisionCode;
        this.CompanyCode = CompanyCode;
        this.TotalAmount = TotalAmount;
        this.CurrencyCode = CurrencyCode;
        this.InvoiceSource = InvoiceSource;
        this.CurrencyRate = CurrencyRate;
        this.InvoiceDate = InvoiceDate;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getCollectionCode() {
        return CollectionCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCollectionCode(String CollectionCode) {
        this.CollectionCode = CollectionCode;
    }

    /** Not-null value. */
    public String getInvoiceCode() {
        return InvoiceCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setInvoiceCode(String InvoiceCode) {
        this.InvoiceCode = InvoiceCode;
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

    public Double getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(Double TotalAmount) {
        this.TotalAmount = TotalAmount;
    }

    public String getCurrencyCode() {
        return CurrencyCode;
    }

    public void setCurrencyCode(String CurrencyCode) {
        this.CurrencyCode = CurrencyCode;
    }

    public Integer getInvoiceSource() {
        return InvoiceSource;
    }

    public void setInvoiceSource(Integer InvoiceSource) {
        this.InvoiceSource = InvoiceSource;
    }

    public Double getCurrencyRate() {
        return CurrencyRate;
    }

    public void setCurrencyRate(Double CurrencyRate) {
        this.CurrencyRate = CurrencyRate;
    }

    public java.util.Date getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(java.util.Date InvoiceDate) {
        this.InvoiceDate = InvoiceDate;
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