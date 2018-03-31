package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */

@DatabaseTable(tableName = "Places")
public class Place {

    @DatabaseField(generatedId = true, canBeNull = false)
    private Integer idPlace;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String address;

    @DatabaseField
    private String gpsLocation;

    @DatabaseField
    private String phone;

    @DatabaseField
    private String email;

    @DatabaseField
    private String facebookLink;

    public Place() {
    }

    public Place(String name, String address, String gpsLocation, String phone, String email,
                 String facebookLink) {
        this.name = name;
        this.address = address;
        this.gpsLocation = gpsLocation;
        this.phone = phone;
        this.email = email;
        this.facebookLink = facebookLink;
    }

    public Integer getIdplace() {
        return idPlace;
    }

    public void setId(Integer idPlace) {
        this.idPlace = idPlace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(String gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }
}
