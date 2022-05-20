package com.hd123.baas.sop.remote.rsh6sop.fcf;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class H6ProcessOrder implements Serializable {
    private String uuid;
    private String StoreGid;
    private Date ocrTime;
    private List<H6FreshGoods> details = new ArrayList<>();

    @Getter
    @Setter
    public static class H6FreshGoods {
        private String gdGid;
        private BigDecimal qty;
    }
}
