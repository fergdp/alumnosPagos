package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */
@DatabaseTable(tableName = "Attendees")
public class Attendee {

    @DatabaseField(generatedId = true)
    private Integer attendeeId;

    @DatabaseField(foreign = true, columnName = "idAttendeeType")
    private AttendeeType attendeeType;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String lastName;

    @DatabaseField
    private Integer age;

    @DatabaseField
    private String cellphoneNumber;

    @DatabaseField
    private String facebookProfile;

    @DatabaseField
    private String email;

    @DatabaseField
    private String alias;

    @DatabaseField
    private String state;

    public Attendee() {

    }

    public Attendee(String name, String lastName, Integer age, String cellphoneNumber,
                    String facebookProfile, String email) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.cellphoneNumber = cellphoneNumber;
        this.facebookProfile = facebookProfile;
        this.email = email;
    }

    public Attendee(String name, String lastName, AttendeeType attendeeType, String cellphoneNumber) {
        this.name = name;
        this.lastName = lastName;
        this.attendeeType = attendeeType;
        this.cellphoneNumber = cellphoneNumber;
    }

    public Attendee(String name, String cellphoneNumber) {
        this.name = name;
        this.cellphoneNumber = cellphoneNumber;
    }

    public Integer getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Integer attendeeId) {
        this.attendeeId = attendeeId;
    }

    public AttendeeType getAttendeeType() {
        return attendeeType;
    }

    public void setAttendeeType(AttendeeType attendeeType) {
        this.attendeeType = attendeeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getFacebookProfile() {
        return facebookProfile;
    }

    public void setFacebookProfile(String facebookProfile) {
        this.facebookProfile = facebookProfile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias() {
        if(this.getLastName() != null) {
            this.alias = this.getName() + " " + this.getLastName();
        } else {
            this.alias = this.getName();
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
