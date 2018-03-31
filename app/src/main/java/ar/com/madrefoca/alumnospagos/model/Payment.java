package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */

@DatabaseTable(tableName = "Payments")
public class Payment {

    @DatabaseField(generatedId = true)
    private Integer idPayment;

    @DatabaseField(foreign = true, columnName = "idCoupon")
    private Coupon coupon;

    @DatabaseField(foreign = true, columnName = "idPaymentType")
    private PaymentType paymentType;

    @DatabaseField(canBeNull = false)
    private Double amount;

    public Payment() {
    }

    public Payment(Coupon coupon, PaymentType paymentType, Double amount) {
        this.coupon = coupon;
        this.paymentType = paymentType;
        this.amount = amount;
    }

    public Integer getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(Integer idPayment) {
        this.idPayment = idPayment;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
