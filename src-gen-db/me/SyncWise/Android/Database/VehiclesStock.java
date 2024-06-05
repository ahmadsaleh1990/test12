package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table VehiclesStock.
 */
public class VehiclesStock implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String VehicleCode;
    /** Not-null value. */
    private String ItemCode;
    private Double GoodQuantity;
    private Double DamageQuantity;
    /** Not-null value. */
    private String DivisionCode;
    /** Not-null value. */
    private String CompanyCode;
    private String ItemLots;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public VehiclesStock() {
    }

    public VehiclesStock(Long id) {
        this.id = id;
    }

    public VehiclesStock(Long id, String VehicleCode, String ItemCode, Double GoodQuantity, Double DamageQuantity, String DivisionCode, String CompanyCode, String ItemLots, java.util.Date StampDate) {
        this.id = id;
        this.VehicleCode = VehicleCode;
        this.ItemCode = ItemCode;
        this.GoodQuantity = GoodQuantity;
        this.DamageQuantity = DamageQuantity;
        this.DivisionCode = DivisionCode;
        this.CompanyCode = CompanyCode;
        this.ItemLots = ItemLots;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getVehicleCode() {
        return VehicleCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setVehicleCode(String VehicleCode) {
        this.VehicleCode = VehicleCode;
    }

    /** Not-null value. */
    public String getItemCode() {
        return ItemCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setItemCode(String ItemCode) {
        this.ItemCode = ItemCode;
    }

    public Double getGoodQuantity() {
        return GoodQuantity;
    }

    public void setGoodQuantity(Double GoodQuantity) {
        this.GoodQuantity = GoodQuantity;
    }

    public Double getDamageQuantity() {
        return DamageQuantity;
    }

    public void setDamageQuantity(Double DamageQuantity) {
        this.DamageQuantity = DamageQuantity;
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

    public String getItemLots() {
        return ItemLots;
    }

    public void setItemLots(String ItemLots) {
        this.ItemLots = ItemLots;
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