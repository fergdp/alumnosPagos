package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */

@DatabaseTable(tableName = "EventTypes")
public class EventType {

    @DatabaseField(generatedId = true)
    private Integer idEventType;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String description;

    public EventType() {
    }

    public EventType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getIdEventType() {
        return idEventType;
    }

    public void setIdEventType(Integer idEventType) {
        this.idEventType = idEventType;
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
