package com.hd123.baas.sop.service.api.pms.template;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.hd123.baas.sop.excel.common.ImportExcelListener;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.service.dao.template.PromTemplateDao;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.utils.ImportResult;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.oss.api.Bucket;
import com.qianfan123.baas.common.BaasException;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GrantStoreImportProcessor {

  @Autowired(required = false)
  private Bucket bucket;
  @Autowired
  private StoreService storeService;
  @Autowired
  private PromTemplateDao promTemplateDao;

  public ImportResult<List<Store>> doImport(String tenantId, String orgId, MultipartFile file, PromTemplate template) {
    ImportResult result = new ImportResult();
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImportProcessData processData = new ImportProcessData();
      processData.setTemplate(template);
      ImportExcelListener excelListener = newExcelListener(tenantId, orgId, processData);
      EasyExcel.read(file.getInputStream(), ExcelInputRow.class, newExcelListener(tenantId, orgId, processData)).sheet().doRead();

      EasyExcel.write(outputStream, ErrorOutputRow.class)
          .useDefaultStyle(false)
          .sheet().doWrite(processData.getErrorOutputRow());
      try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
        String path = "sop/tempOf1Day/" + tenantId + "/importResult/store/" + UUID.randomUUID().toString() + "/" + file.getOriginalFilename();
        bucket.put(path, inputStream);
        result.setIgnoreCount(processData.getIgnoreCount());
        result.setFailCount(processData.getFailedCount());
        result.setSuccessCount(processData.getSuccessCount());
        result.setBackUrl(bucket.getUrl(path, Bucket.CONTENT_TYPE_OF_WILDCARD));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  public ImportExcelListener newExcelListener(String tenant, String orgId, ImportProcessData processData) {
    return new ImportExcelListener<ExcelInputRow>() {
      @Override
      public void processData(List<ExcelInputRow> excelRows) throws BaasException {
        List<Store> stores = queryStores(tenant, orgId, excelRows);

        PromTemplate template = processData.getTemplate();
        List<PromotionJoinUnits.JoinUnit> grantUnits = new ArrayList<>();
        Map<String, Store> storeMap = stores.stream().collect(Collectors.toMap(Store::getCode, Function.identity()));
        List<ErrorOutputRow> errorOutputRows = new ArrayList<>();
        int failedCount = 0;
        int ignoreCount = 0;
        for (ExcelInputRow row : excelRows) {
          Store store = storeMap.get(row.getCode());
          if (store == null) {
            errorOutputRows.add(new ErrorOutputRow(row.getCode(), "失败", "指定的门店不存在或不可用"));
            failedCount++;
          } else if (processData.getSelectedList().contains(store.getId())) {
            errorOutputRows.add(new ErrorOutputRow(row.getCode(), "忽略", "指定的门店已授权"));
            ignoreCount++;
          } else {
            grantUnits.add(new PromotionJoinUnits.JoinUnit(store.getId(), store.getCode(), store.getName()));
          }
        }
        if (grantUnits.isEmpty() == false) {
          PromotionJoinUnits data = new PromotionJoinUnits();
          data.setAllUnit(false);
          data.setStores(grantUnits);
          promTemplateDao.saveGrantUnits(template.getUuid(), data, false);
        }
        processData.getErrorOutputRow().addAll(errorOutputRows);
        processData.setFailedCount(processData.getFailedCount() + failedCount);
        processData.setIgnoreCount(processData.getIgnoreCount() + ignoreCount);
        processData.setSuccessCount(processData.getSuccessCount() + grantUnits.size());
        excelRows.clear();
      }

      /**
       * 查询门店
       */
      private List<Store> queryStores(String tenant, String orgId, List<ExcelInputRow> excelRows) throws BaasException {
        List<String> codeIn = excelRows.stream().map(ExcelInputRow::getCode).collect(Collectors.toList());
        StoreFilter filter = new StoreFilter();
        filter.setCodeIn(codeIn);
        filter.setOrgIdEq(orgId);
        filter.setPage(0);
        filter.setPageSize(0);
        return storeService.query(tenant, filter).getRecords();
      }
    };
  }

  @Data
  public static class ImportProcessData {
    private PromTemplate template;
    private List<String> selectedList = new ArrayList<>();
    private int failedCount;
    private int ignoreCount;
    private int successCount;
    private List<ErrorOutputRow> errorOutputRow = new ArrayList<>();

    public void setTemplate(PromTemplate template) {
      this.template = template;
      if (template != null && template.getGrantUnits() != null && template.getGrantUnits().getStores() != null) {
        this.selectedList = template.getGrantUnits().getStores().stream().map(Entity::getUuid).collect(Collectors.toList());
      }
    }
  }

  /**
   * @author Silent
   **/
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ExcelInputRow {

    @ExcelProperty("门店代码")
    @ApiModelProperty(value = "代码")
    public String code;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorOutputRow {
    @ExcelProperty("门店代码")
    private String code;
    @ExcelProperty("状态")
    private String state;
    @ExcelProperty("说明")
    private String message;
  }
}
