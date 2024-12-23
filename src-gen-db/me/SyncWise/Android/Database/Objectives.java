package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table Objectives.
 */
public class Objectives implements java.io.Serializable {

    private Long id;
    private Long ObjectiveID;
    private Integer ObjectiveType;
    private Integer ObjectiveSource;
    private Long VisitID;
    private Integer ObjectivePriorityID;
    private String ObjectiveDescription;
    private String ObjectiveAltDescription;
    private java.util.Date StartDate;
    private java.util.Date EndDate;
    private int IsProcessed;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public Objectives() {
    }

    public Objectives(Long id) {
        this.id = id;
    }

    public Objectives(Long id, Long ObjectiveID, Integer ObjectiveType, Integer ObjectiveSource, Long VisitID, Integer ObjectivePriorityID, String ObjectiveDescription, String ObjectiveAltDescription, java.util.Date StartDate, java.util.Date EndDate, int IsProcessed, java.util.Date StampDate) {
        this.id = id;
        this.ObjectiveID = ObjectiveID;
        this.ObjectiveType = ObjectiveType;
        this.ObjectiveSource = ObjectiveSource;
        this.VisitID = VisitID;
        this.ObjectivePriorityID = ObjectivePriorityID;
        this.ObjectiveDescription = ObjectiveDescription;
        this.ObjectiveAltDescription = ObjectiveAltDescription;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
        this.IsProcessed = IsProcessed;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObjectiveID() {
        return ObjectiveID;
    }

    public void setObjectiveID(Long ObjectiveID) {
        this.ObjectiveID = ObjectiveID;
    }

    public Integer getObjectiveType() {
        return ObjectiveType;
    }

    public void setObjectiveType(Integer ObjectiveType) {
        this.ObjectiveType = ObjectiveType;
    }

    public Integer getObjectiveSource() {
        return ObjectiveSource;
    }

    public void setObjectiveSource(Integer ObjectiveSource) {
        this.ObjectiveSource = ObjectiveSource;
    }

    public Long getVisitID() {
        return VisitID;
    }

    public void setVisitID(Long VisitID) {
        this.VisitID = VisitID;
    }

    public Integer getObjectivePriorityID() {
        return ObjectivePriorityID;
    }

    public void setObjectivePriorityID(Integer ObjectivePriorityID) {
        this.ObjectivePriorityID = ObjectivePriorityID;
    }

    public String getObjectiveDescription() {
        return ObjectiveDescription;
    }

    public void setObjectiveDescription(String ObjectiveDescription) {
        this.ObjectiveDescription = ObjectiveDescription;
    }

    public String getObjectiveAltDescription() {
        return ObjectiveAltDescription;
    }

    public void setObjectiveAltDescription(String ObjectiveAltDescription) {
        this.ObjectiveAltDescription = ObjectiveAltDescription;
    }

    public java.util.Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(java.util.Date StartDate) {
        this.StartDate = StartDate;
    }

    public java.util.Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(java.util.Date EndDate) {
        this.EndDate = EndDate;
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
