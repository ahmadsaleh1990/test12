package me.SyncWise.Android.Database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SurveyImages.
 */
public class SurveyImages implements java.io.Serializable {

    private Long id;
    private long SurveyID;
    private int QuestionID;
    private int LineID;
    private String ImageName;
    private String ImagePath;
    /** Not-null value. */
    private java.util.Date StampDate;

	private static final long serialVersionUID = 1L;

    public SurveyImages() {
    }

    public SurveyImages(Long id) {
        this.id = id;
    }

    public SurveyImages(Long id, long SurveyID, int QuestionID, int LineID, String ImageName, String ImagePath, java.util.Date StampDate) {
        this.id = id;
        this.SurveyID = SurveyID;
        this.QuestionID = QuestionID;
        this.LineID = LineID;
        this.ImageName = ImageName;
        this.ImagePath = ImagePath;
        this.StampDate = StampDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSurveyID() {
        return SurveyID;
    }

    public void setSurveyID(long SurveyID) {
        this.SurveyID = SurveyID;
    }

    public int getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(int QuestionID) {
        this.QuestionID = QuestionID;
    }

    public int getLineID() {
        return LineID;
    }

    public void setLineID(int LineID) {
        this.LineID = LineID;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String ImageName) {
        this.ImageName = ImageName;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String ImagePath) {
        this.ImagePath = ImagePath;
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