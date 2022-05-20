package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.hd123.baas.sop.remote.workwx.apply.ApplyFormControl;
import lombok.Data;

import java.util.List;

/**
 * @author YUAN
 */
@Data
public class FileFormControl implements ApplyFormControl {

    private List<FileData> files;

    @Override
    public String formControlName() {
        return "File";
    }
}
