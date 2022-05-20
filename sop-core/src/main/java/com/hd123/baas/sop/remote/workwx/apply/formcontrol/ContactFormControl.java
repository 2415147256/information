package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.hd123.baas.sop.remote.workwx.apply.ApplyFormControl;
import lombok.Data;

import java.util.List;

@Data
public class ContactFormControl implements ApplyFormControl {

    private List<ContactMember> members;

    private List<ContactDepartment> departments;

    @Override
    public String formControlName() {
        return "Contact";
    }
}
