package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TransactionSequences.
 */
public class TransactionSequences implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String UserCode;
    /** Not-null value. */
    private String CompanyCode;
    private Integer SalesOrder;
    private Integer SalesInvoice;
    private Integer SalesReturn;
    private Integer SalesRFR;
    private Integer Collection;
    private Integer Movements;
    private Integer ClientStockCount;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public TransactionSequences() {
    }

    public TransactionSequences(Long id) {
        this.id = id;
    }

    public TransactionSequences(Long id, String UserCode, String CompanyCode, Integer SalesOrder, Integer SalesInvoice, Integer SalesReturn, Integer SalesRFR, Integer Collection, Integer Movements, Integer ClientStockCount, java.util.Date StampDate) {
        this.id = id;
        this.UserCode = UserCode;
        this.CompanyCode = CompanyCode;
        this.SalesOrder = SalesOrder;
        this.SalesInvoice = SalesInvoice;
        this.SalesReturn = SalesReturn;
        this.SalesRFR = SalesRFR;
        this.Collection = Collection;
        this.Movements = Movements;
        this.ClientStockCount = ClientStockCount;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getSalesOrder() {
        return SalesOrder;
    }

    public void setSalesOrder(Integer SalesOrder) {
        this.SalesOrder = SalesOrder;
    }

    public Integer getSalesInvoice() {
        return SalesInvoice;
    }

    public void setSalesInvoice(Integer SalesInvoice) {
        this.SalesInvoice = SalesInvoice;
    }

    public Integer getSalesReturn() {
        return SalesReturn;
    }

    public void setSalesReturn(Integer SalesReturn) {
        this.SalesReturn = SalesReturn;
    }

    public Integer getSalesRFR() {
        return SalesRFR;
    }

    public void setSalesRFR(Integer SalesRFR) {
        this.SalesRFR = SalesRFR;
    }

    public Integer getCollection() {
        return Collection;
    }

    public void setCollection(Integer Collection) {
        this.Collection = Collection;
    }

    public Integer getMovements() {
        return Movements;
    }

    public void setMovements(Integer Movements) {
        this.Movements = Movements;
    }

    public Integer getClientStockCount() {
        return ClientStockCount;
    }

    public void setClientStockCount(Integer ClientStockCount) {
        this.ClientStockCount = ClientStockCount;
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
