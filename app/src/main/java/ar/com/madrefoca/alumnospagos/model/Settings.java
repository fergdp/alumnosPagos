package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by developer on 1/31/18.
 */

public class Settings {
    @DatabaseField(generatedId = true)
    private Integer idSetting;

    @DatabaseField
    private String key;

    @DatabaseField
    private String value;

    public Integer getIdSetting() {
        return idSetting;
    }

    public void setIdSetting(Integer idSetting) {
        this.idSetting = idSetting;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
