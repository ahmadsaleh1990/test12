package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ShareOfShelfTracker.
 */
public class ShareOfShelfTracker implements java.io.Serializable {

    private Long id;
    private long TransactionID;
    /** Not-null value. */
    private String UserCode;
    /** Not-null value. */
    private String ClientCode;
    /** Not-null value. */
    private String CompanyCode;
    /** Not-null value. */
    private String DivisionCode;
    /** Not-null value. */
    private String BrandCode;
    private Long VisitID;
    private Double ShareOfShelf;
    private Double ShareOfShelfCompetitor;
    private Double Category;
    private String Notes;
    private int IsProcessed;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public ShareOfShelfTracker() {
    }

    public ShareOfShelfTracker(Long id) {
        this.id = id;
    }

    public ShareOfShelfTracker(Long id, long TransactionID, String UserCode, String ClientCode, String CompanyCode, String DivisionCode, String BrandCode, Long VisitID, Double ShareOfShelf, Double ShareOfShelfCompetitor, Double Category, String Notes, int IsProcessed, java.util.Date StampDate) {
        this.id = id;
        this.TransactionID = TransactionID;
        this.UserCode = UserCode;
        this.ClientCode = ClientCode;
        this.CompanyCode = CompanyCode;
        this.DivisionCode = DivisionCode;
        this.BrandCode = BrandCode;
        this.VisitID = VisitID;
        this.ShareOfShelf = ShareOfShelf;
        this.ShareOfShelfCompetitor = ShareOfShelfCompetitor;
        this.Category = Category;
        this.Notes = Notes;
        this.IsProcessed = IsProcessed;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(long TransactionID) {
        this.TransactionID = TransactionID;
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
    public String getClientCode() {
        return ClientCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setClientCode(String ClientCode) {
        this.ClientCode = ClientCode;
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
    public String getBrandCode() {
        return BrandCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setBrandCode(String BrandCode) {
        this.BrandCode = BrandCode;
    }

    public Long getVisitID() {
        return VisitID;
    }

    public void setVisitID(Long VisitID) {
        this.VisitID = VisitID;
    }

    public Double getShareOfShelf() {
        return ShareOfShelf;
    }

    public void setShareOfShelf(Double ShareOfShelf) {
        this.ShareOfShelf = ShareOfShelf;
    }

    public Double getShareOfShelfCompetitor() {
        return ShareOfShelfCompetitor;
    }

    public void setShareOfShelfCompetitor(Double ShareOfShelfCompetitor) {
        this.ShareOfShelfCompetitor = ShareOfShelfCompetitor;
    }

    public Double getCategory() {
        return Category;
    }

    public void setCategory(Double Category) {
        this.Category = Category;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String Notes) {
        this.Notes = Notes;
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
