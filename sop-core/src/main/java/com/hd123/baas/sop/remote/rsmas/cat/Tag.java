package com.hd123.baas.sop.remote.rsmas.cat;

/**
 * @Author maodapeng
 * @Since
 */
public class Tag {
    private String name;
    private String color;
    private Integer lineNo;

    public Tag() {
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public Integer getLineNo() {
        return this.lineNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }
}