package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table WarehouseQuantities.
 */
public class WarehouseQuantities implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String WarehouseCode;
    /** Not-null value. */
    private String ItemCode;
    /** Not-null value. */
    private String DivisonCode;
    /** Not-null value. */
    private String CompanyCode;
    private Double QuantityBig;
    private Double QuantityMedium;
    private Double QuantitySmall;
    private Double MinimumQuantity;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public WarehouseQuantities() {
    }

    public WarehouseQuantities(Long id) {
        this.id = id;
    }

    public WarehouseQuantities(Long id, String WarehouseCode, String ItemCode, String DivisonCode, String CompanyCode, Double QuantityBig, Double QuantityMedium, Double QuantitySmall, Double MinimumQuantity, java.util.Date StampDate) {
        this.id = id;
        this.WarehouseCode = WarehouseCode;
        this.ItemCode = ItemCode;
        this.DivisonCode = DivisonCode;
        this.CompanyCode = CompanyCode;
        this.QuantityBig = QuantityBig;
        this.QuantityMedium = QuantityMedium;
        this.QuantitySmall = QuantitySmall;
        this.MinimumQuantity = MinimumQuantity;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getWarehouseCode() {
        return WarehouseCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setWarehouseCode(String WarehouseCode) {
        this.WarehouseCode = WarehouseCode;
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
    public String getDivisonCode() {
        return DivisonCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDivisonCode(String DivisonCode) {
        this.DivisonCode = DivisonCode;
    }

    /** Not-null value. */
    public String getCompanyCode() {
        return CompanyCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCompanyCode(String CompanyCode) {
        this.CompanyCode = CompanyCode;
    }

    public Double getQuantityBig() {
        return QuantityBig;
    }

    public void setQuantityBig(Double QuantityBig) {
        this.QuantityBig = QuantityBig;
    }

    public Double getQuantityMedium() {
        return QuantityMedium;
    }

    public void setQuantityMedium(Double QuantityMedium) {
        this.QuantityMedium = QuantityMedium;
    }

    public Double getQuantitySmall() {
        return QuantitySmall;
    }

    public void setQuantitySmall(Double QuantitySmall) {
        this.QuantitySmall = QuantitySmall;
    }

    public Double getMinimumQuantity() {
        return MinimumQuantity;
    }

    public void setMinimumQuantity(Double MinimumQuantity) {
        this.MinimumQuantity = MinimumQuantity;
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
