package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */

@DatabaseTable(tableName = "AttendeeTypes")
public class AttendeeType {

    @DatabaseField(generatedId = true)
    private Integer idAttendeeType;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String description;

    public AttendeeType() {
    }

    public AttendeeType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getIdAttendeeType() {
        return idAttendeeType;
    }

    public void setIdAttendeeType(Integer idAttendeeType) {

        this.idAttendeeType = idAttendeeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
