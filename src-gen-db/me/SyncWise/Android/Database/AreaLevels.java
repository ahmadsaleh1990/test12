package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table AreaLevels.
 */
public class AreaLevels implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String AreaLevelCode;
    private String AreaLevelDescription;
    private String AreaLevelAltDescription;
    private Integer Sequence;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public AreaLevels() {
    }

    public AreaLevels(Long id) {
        this.id = id;
    }

    public AreaLevels(Long id, String AreaLevelCode, String AreaLevelDescription, String AreaLevelAltDescription, Integer Sequence, java.util.Date StampDate) {
        this.id = id;
        this.AreaLevelCode = AreaLevelCode;
        this.AreaLevelDescription = AreaLevelDescription;
        this.AreaLevelAltDescription = AreaLevelAltDescription;
        this.Sequence = Sequence;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getAreaLevelCode() {
        return AreaLevelCode;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setAreaLevelCode(String AreaLevelCode) {
        this.AreaLevelCode = AreaLevelCode;
    }

    public String getAreaLevelDescription() {
        return AreaLevelDescription;
    }

    public void setAreaLevelDescription(String AreaLevelDescription) {
        this.AreaLevelDescription = AreaLevelDescription;
    }

    public String getAreaLevelAltDescription() {
        return AreaLevelAltDescription;
    }

    public void setAreaLevelAltDescription(String AreaLevelAltDescription) {
        this.AreaLevelAltDescription = AreaLevelAltDescription;
    }

    public Integer getSequence() {
        return Sequence;
    }

    public void setSequence(Integer Sequence) {
        this.Sequence = Sequence;
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