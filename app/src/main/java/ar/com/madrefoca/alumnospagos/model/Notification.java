package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */

@DatabaseTable(tableName = "Notifications")
public class Notification {

    @DatabaseField(generatedId = true)
    private Integer idNotification;

    @DatabaseField(foreign = true, columnName = "idEvent")
    private Event event;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    public Notification() {
    }

    public Notification(Event event, String name, String description) {
        this.event = event;
        this.name = name;
        this.description = description;
    }

    public Integer getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(Integer idNotification) {
        this.idNotification = idNotification;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
