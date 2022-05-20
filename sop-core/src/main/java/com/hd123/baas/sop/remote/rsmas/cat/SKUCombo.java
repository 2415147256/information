package com.hd123.baas.sop.remote.rsmas.cat;



import java.math.BigDecimal;

/**
 * @Author maodapeng
 * @Since
 */
public class SKUCombo extends SKU {
    private static final long serialVersionUID = 1L;
    private InputCode inputCode;
    private BigDecimal quantity;

    public SKUCombo() {
    }

    public InputCode getInputCode() {
        return this.inputCode;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public void setInputCode(InputCode inputCode) {
        this.inputCode = inputCode;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}