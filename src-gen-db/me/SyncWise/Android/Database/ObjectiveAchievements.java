package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ObjectiveAchievements.
 */
public class ObjectiveAchievements implements java.io.Serializable {

    private Long id;
    private Long ObjectiveID;
    private Integer AchievementType;
    private String UserCode;
    private String ClientCode;
    private String DivisionCode;
    private String CompanyCode;
    private Long VisitID;
    private Integer ObjectiveAchievement;
    private java.util.Date AchievementDate;
    private String ReasonCode;
    private String Notes;
    private int IsProcessed;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public ObjectiveAchievements() {
    }

    public ObjectiveAchievements(Long id) {
        this.id = id;
    }

    public ObjectiveAchievements(Long id, Long ObjectiveID, Integer AchievementType, String UserCode, String ClientCode, String DivisionCode, String CompanyCode, Long VisitID, Integer ObjectiveAchievement, java.util.Date AchievementDate, String ReasonCode, String Notes, int IsProcessed, java.util.Date StampDate) {
        this.id = id;
        this.ObjectiveID = ObjectiveID;
        this.AchievementType = AchievementType;
        this.UserCode = UserCode;
        this.ClientCode = ClientCode;
        this.DivisionCode = DivisionCode;
        this.CompanyCode = CompanyCode;
        this.VisitID = VisitID;
        this.ObjectiveAchievement = ObjectiveAchievement;
        this.AchievementDate = AchievementDate;
        this.ReasonCode = ReasonCode;
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

    public Long getObjectiveID() {
        return ObjectiveID;
    }

    public void setObjectiveID(Long ObjectiveID) {
        this.ObjectiveID = ObjectiveID;
    }

    public Integer getAchievementType() {
        return AchievementType;
    }

    public void setAchievementType(Integer AchievementType) {
        this.AchievementType = AchievementType;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String UserCode) {
        this.UserCode = UserCode;
    }

    public String getClientCode() {
        return ClientCode;
    }

    public void setClientCode(String ClientCode) {
        this.ClientCode = ClientCode;
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

    public Long getVisitID() {
        return VisitID;
    }

    public void setVisitID(Long VisitID) {
        this.VisitID = VisitID;
    }

    public Integer getObjectiveAchievement() {
        return ObjectiveAchievement;
    }

    public void setObjectiveAchievement(Integer ObjectiveAchievement) {
        this.ObjectiveAchievement = ObjectiveAchievement;
    }

    public java.util.Date getAchievementDate() {
        return AchievementDate;
    }

    public void setAchievementDate(java.util.Date AchievementDate) {
        this.AchievementDate = AchievementDate;
    }

    public String getReasonCode() {
        return ReasonCode;
    }

    public void setReasonCode(String ReasonCode) {
        this.ReasonCode = ReasonCode;
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