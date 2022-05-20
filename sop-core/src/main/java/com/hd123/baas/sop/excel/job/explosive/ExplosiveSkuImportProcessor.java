package com.hd123.baas.sop.excel.job.explosive;

import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.utils.ImportResult;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


public interface ExplosiveSkuImportProcessor {
  ImportResult<List<Sku>> doImport(String tenantId, String orgId, MultipartFile file, Params param);

  @Data
  class Params {
    private List<String> selectedList = new ArrayList<>();
    private List<String> excludeList = new ArrayList<>();
    private boolean prmGoods;
  }
}
