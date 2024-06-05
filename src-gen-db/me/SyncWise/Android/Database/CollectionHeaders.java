package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CollectionHeaders.
 */
public class CollectionHeaders implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String CollectionCode;
    private Long VisitID;
    private String JourneyCode;
    private String ClientCode;
    private Double TotalAmount;
    private String CurrencyCode;
    private String UserCode;
    private String DivisionCode;
    private String UserDivisionCode;
    private String CompanyCode;
    private String UserCompanyCode;
    private java.util.Date CollectionDate;
    private Integer CollectionType;
    private Integer CollectionStatus;
    private Integer IsPDCCleared;
    private Integer PaymentType;
    private Integer IsSetteled;
    private String PasswordCode;
    private String ParentCode;
    private Integer PrintingTimes;
    private String Notes;
    private String ManualReference;
    private int IsProcessed;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public CollectionHeaders() {
    }

    public CollectionHeaders(Long id) {
        this.id = id;
    }

    public CollectionHeaders(Long id, String CollectionCode, Long VisitID, String JourneyCode, String ClientCode, Double TotalAmount, String CurrencyCode, String UserCode, String DivisionCode, String UserDivisionCode, String CompanyCode, String UserCompanyCode, java.util.Date CollectionDate, Integer CollectionType, Integer CollectionStatus, Integer IsPDCCleared, Integer PaymentType, Integer IsSetteled, String PasswordCode, String ParentCode, Integer PrintingTimes, String Notes, String ManualReference, int IsProcessed, java.util.Date StampDate) {
        this.id = id;
        this.CollectionCode = CollectionCode;
        this.VisitID = VisitID;
        this.JourneyCode = JourneyCode;
        this.ClientCode = ClientCode;
        this.TotalAmount = TotalAmount;
        this.CurrencyCode = CurrencyCode;
        this.UserCode = UserCode;
        this.DivisionCode = DivisionCode;
        this.UserDivisionCode = UserDivisionCode;
        this.CompanyCode = CompanyCode;
        this.UserCompanyCode = UserCompanyCode;
        this.CollectionDate = CollectionDate;
        this.CollectionType = CollectionType;
        this.CollectionStatus = CollectionStatus;
        this.IsPDCCleared = IsPDCCleared;
        this.PaymentType = PaymentType;
        this.IsSetteled = IsSetteled;
        this.PasswordCode = PasswordCode;
        this.ParentCode = ParentCode;
        this.PrintingTimes = PrintingTimes;
        this.Notes = Notes;
        this.ManualReference = ManualReference;
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
    public String getCollectionCode() {
        return CollectionCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCollectionCode(String CollectionCode) {
        this.CollectionCode = CollectionCode;
    }

    public Long getVisitID() {
        return VisitID;
    }

    public void setVisitID(Long VisitID) {
        this.VisitID = VisitID;
    }

    public String getJourneyCode() {
        return JourneyCode;
    }

    public void setJourneyCode(String JourneyCode) {
        this.JourneyCode = JourneyCode;
    }

    public String getClientCode() {
        return ClientCode;
    }

    public void setClientCode(String ClientCode) {
        this.ClientCode = ClientCode;
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

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String UserCode) {
        this.UserCode = UserCode;
    }

    public String getDivisionCode() {
        return DivisionCode;
    }

    public void setDivisionCode(String DivisionCode) {
        this.DivisionCode = DivisionCode;
    }

    public String getUserDivisionCode() {
        return UserDivisionCode;
    }

    public void setUserDivisionCode(String UserDivisionCode) {
        this.UserDivisionCode = UserDivisionCode;
    }

    public String getCompanyCode() {
        return CompanyCode;
    }

    public void setCompanyCode(String CompanyCode) {
        this.CompanyCode = CompanyCode;
    }

    public String getUserCompanyCode() {
        return UserCompanyCode;
    }

    public void setUserCompanyCode(String UserCompanyCode) {
        this.UserCompanyCode = UserCompanyCode;
    }

    public java.util.Date getCollectionDate() {
        return CollectionDate;
    }

    public void setCollectionDate(java.util.Date CollectionDate) {
        this.CollectionDate = CollectionDate;
    }

    public Integer getCollectionType() {
        return CollectionType;
    }

    public void setCollectionType(Integer CollectionType) {
        this.CollectionType = CollectionType;
    }

    public Integer getCollectionStatus() {
        return CollectionStatus;
    }

    public void setCollectionStatus(Integer CollectionStatus) {
        this.CollectionStatus = CollectionStatus;
    }

    public Integer getIsPDCCleared() {
        return IsPDCCleared;
    }

    public void setIsPDCCleared(Integer IsPDCCleared) {
        this.IsPDCCleared = IsPDCCleared;
    }

    public Integer getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(Integer PaymentType) {
        this.PaymentType = PaymentType;
    }

    public Integer getIsSetteled() {
        return IsSetteled;
    }

    public void setIsSetteled(Integer IsSetteled) {
        this.IsSetteled = IsSetteled;
    }

    public String getPasswordCode() {
        return PasswordCode;
    }

    public void setPasswordCode(String PasswordCode) {
        this.PasswordCode = PasswordCode;
    }

    public String getParentCode() {
        return ParentCode;
    }

    public void setParentCode(String ParentCode) {
        this.ParentCode = ParentCode;
    }

    public Integer getPrintingTimes() {
        return PrintingTimes;
    }

    public void setPrintingTimes(Integer PrintingTimes) {
        this.PrintingTimes = PrintingTimes;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String Notes) {
        this.Notes = Notes;
    }

    public String getManualReference() {
        return ManualReference;
    }

    public void setManualReference(String ManualReference) {
        this.ManualReference = ManualReference;
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
