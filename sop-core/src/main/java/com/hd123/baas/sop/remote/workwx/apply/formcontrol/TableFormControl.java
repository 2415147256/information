package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.hd123.baas.sop.remote.workwx.apply.ApplyFormControl;
import com.hd123.baas.sop.remote.workwx.apply.TableChildren;
import lombok.Data;

import java.util.List;

@Data
public class TableFormControl implements ApplyFormControl {

    private List<TableChildren> children;

    @Override
    public String formControlName() {
        return "Table";
    }
}
