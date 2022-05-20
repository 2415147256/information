package com.hd123.baas.sop.remote.rsias.inv;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lins
 */
@Getter
@Setter
public class RsIasPageResponse<T> {
    private int code;
    private String msg;
    public boolean success = true;
    private T data;
    private int page;
    private int pageSize;
    private int pageCount;
    private int total;
}
