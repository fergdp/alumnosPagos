package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */

@DatabaseTable(tableName = "PaymentTypes")
public class PaymentType {

    @DatabaseField(generatedId = true)
    private Integer idPaymentType;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String description;

    public PaymentType() {
    }

    public PaymentType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getIdPaymentType() {
        return idPaymentType;
    }

    public void setIdPaymentType(Integer idPaymentType) {
        this.idPaymentType = idPaymentType;
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
