package com.hd123.baas.sop.excel.job.explosivev2;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.hd123.baas.sop.excel.common.ImportExcelListener;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
import com.hd123.baas.sop.excel.job.explosive.ExplosiveSkuImportProcessor;
import com.hd123.baas.sop.utils.ImportResult;
import com.hd123.rumba.oss.api.Bucket;
import com.qianfan123.baas.common.BaasException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author shenmin
 */
@Component("ExplosiveSkuLineImportProcessorImpl")
public class ExplosiveSkuLineImportProcessorImpl implements ExplosiveSkuImportProcessor {

  @Autowired(required = false)
  private Bucket bucket;
  @Autowired
  private SkuService skuService;


  @Override
  public ImportResult<List<Sku>> doImport(String tenantId, String orgId, MultipartFile file, Params param) {
    ImportResult result = new ImportResult();
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImportProcessData processData = new ImportProcessData();
      BeanUtils.copyProperties(param, processData);
      EasyExcel.read(file.getInputStream(), ExcelInputRow.class, newExcelListener(tenantId, orgId, processData)).sheet().headRowNumber(3).doRead();
      result.setIgnoreCount(processData.getIgnoreCount());
      result.setFailCount(processData.getFailedCount());
      result.setSuccessCount(processData.getSkuList().size());
      result.setSuccessList(processData.getSkuList());

      if (!processData.getErrorRowData().isEmpty()) {
        EasyExcel.write(outputStream, ErrorOutputRow.class)
            .useDefaultStyle(false)
            .sheet().doWrite(processData.getErrorRowData());
        try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
          String path = "sop/tempOf1Day/" + tenantId + "/importResult/sku/" + UUID.randomUUID().toString() + "/" + "??????????????????????????????.xlsx";
          bucket.put(path, inputStream);
          result.setBackUrl(bucket.getUrl(path, Bucket.CONTENT_TYPE_OF_WILDCARD));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
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
        List<Sku> skus = querySkus(tenant, orgId, excelRows);

        Map<String, Sku> skuMap = new HashMap<>();
        for (Sku sku : skus) {
          skuMap.put(MessageFormat.format("{0},{1}", sku.getCode().toUpperCase(), sku.getQpc().stripTrailingZeros().toPlainString()), sku);
        }

        List<ErrorOutputRow> errorOutputRows = new ArrayList<>();
        int failedCount = 0;
        int ignoreCount = 0;
        List<ExplosiveV2Line> result = new ArrayList<>();
        for (ExcelInputRow row : excelRows) {
          if (StringUtils.isBlank(row.getCode())) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "????????????????????????"));
            failedCount++;
            continue;
          }
          BigDecimal qpc = new BigDecimal("1");
          Sku sku = skuMap.get(MessageFormat.format("{0},{1}", row.getCode().toUpperCase(), qpc.stripTrailingZeros().toPlainString()));
          if (sku == null) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "???????????????1*1??????????????????????????????"));
            failedCount++;
            continue;
          }
          BigDecimal skuQpc;
          try {
            skuQpc = new BigDecimal(row.getSkuQpc());
          } catch (Exception e) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "?????????????????????????????????1*1????????????1"));
            failedCount++;
            continue;
          }
          Sku aclSku = skuMap.get(MessageFormat.format("{0},{1}", row.getCode().toUpperCase(), skuQpc.stripTrailingZeros().toPlainString()));
          if (aclSku == null) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "??????????????????????????????????????????"));
            failedCount++;
            continue;
          }
          String skuKey = MessageFormat.format("{0},{1}", sku.getGoodsGid(), sku.getQpc().stripTrailingZeros().toPlainString());
          if (processData.getSelectedList().contains(skuKey)) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "????????????????????????"));
            ignoreCount++;
          } else if (processData.getExcludeList().contains(skuKey)) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "?????????????????????????????????"));
            failedCount++;
          } else if (processData.getSkuKeys().contains(skuKey)) {
            errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "???????????????????????????"));
            ignoreCount++;
          } else {
            processData.getSkuKeys().add(skuKey);
            ExplosiveV2Line item = new ExplosiveV2Line();
            item.setSkuGid(sku.getGoodsGid());
            item.setSkuId(sku.getId());
            item.setSkuCode(sku.getCode());
            item.setSkuName(sku.getName());
            item.setSkuUnit(aclSku.getUnit());
            item.setSkuQpc(skuQpc);
            BigDecimal inPrice;
            BigDecimal minQty;
            BigDecimal limitQty;
            try {
              inPrice = new BigDecimal(row.getInPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "?????????????????????"));
              failedCount++;
              continue;
            }
            if (inPrice.compareTo(BigDecimal.ZERO) <= 0) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "?????????????????????0"));
              failedCount++;
              continue;
            } else if (inPrice.compareTo(new BigDecimal("99999999.99")) > 0) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "????????????????????????(99999999.99)"));
              failedCount++;
              continue;
            }

            try {
              minQty = new BigDecimal(row.getMinQty()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "????????????????????????????????????"));
              failedCount++;
              continue;
            }
            if (minQty.compareTo(BigDecimal.ZERO) < 0) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "??????????????????????????????????????????0"));
              failedCount++;
              continue;
            } else if (minQty.compareTo(new BigDecimal("99999999.99")) > 0) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "???????????????????????????????????????(99999999.99)"));
              failedCount++;
              continue;
            }
            if (row.getIsLimit() == null) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "????????????????????????"));
              failedCount++;
              continue;
            }
            if (!"???".equals(row.getIsLimit()) && !"???".equals(row.getIsLimit())) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "??????????????????????????? ???  ???  ???"));
              failedCount++;
              continue;
            }
            try {
              if ("???".equals(row.getIsLimit())) {
                limitQty = new BigDecimal(row.getLimitQty()).setScale(2, BigDecimal.ROUND_HALF_UP);
                if (limitQty.compareTo(BigDecimal.ZERO) <= 0) {
                  errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "????????????????????????????????????0"));
                  failedCount++;
                  continue;
                } else if (limitQty.compareTo(minQty) < 0) {
                  errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "????????????????????????????????????????????????????????????"));
                  failedCount++;
                  continue;
                } else if (limitQty.compareTo(new BigDecimal("99999999.99")) > 0) {
                  errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "???????????????????????????????????????(99999999.99)"));
                  failedCount++;
                  continue;
                }
              } else {
                limitQty = BigDecimal.ZERO;
              }
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuLineImportProcessorImpl.ErrorOutputRow(row, "??????", "????????????????????????????????????"));
              failedCount++;
              continue;
            }
            item.setInPrice(inPrice);
            item.setMinQty(minQty);
            item.setLimitQty(limitQty);
            item.setRemark(row.getRemark());
            result.add(item);
          }
        }

        processData.getSkuList().addAll(result);
        processData.getErrorRowData().addAll(errorOutputRows);
        processData.setFailedCount(processData.getFailedCount() + failedCount);
        processData.setIgnoreCount(processData.getIgnoreCount() + ignoreCount);
        excelRows.clear();
      }

      /**
       * ??????sku
       */
      private List<Sku> querySkus(String tenant, String orgId, List<ExcelInputRow> excelRows) throws BaasException {
        List<String> codeIn = excelRows.stream().map(ExcelInputRow::getCode).collect(Collectors.toList());
        SkuFilter filter = new SkuFilter();
        filter.setCodeIn(codeIn);
        filter.setOrgIdEq(orgId);
        filter.setPage(0);
        filter.setPageSize(0);
        filter.setFetchParts("category");
        return skuService.query(tenant, filter).getRecords();
      }
    };
  }

  @Data
  public static class ImportProcessData {
    private List skuList = new ArrayList<>();
    private List<String> skuKeys = new ArrayList<>();
    private List<String> selectedList = new ArrayList<>();
    private List<String> excludeList = new ArrayList<>();
    private int failedCount;
    private int ignoreCount;
    private List<ErrorOutputRow> errorRowData = new ArrayList<>();
    private boolean prmGoods;
  }

  /**
   * @author shenmin
   **/
  @Data
  public static class ExcelInputRow {
    @ExcelProperty("????????????????????????")
    public String code;
    @ExcelProperty("????????????????????????")
    public String skuQpc;
    @ExcelProperty("??????????????????")
    public String skuUnit;
    @ExcelProperty("????????????????????????????????????")
    public String inPrice;
    @ExcelProperty("????????????????????????")
    public String isLimit;
    @ExcelProperty("???????????????????????????")
    public String limitQty;
    @ExcelProperty("???????????????????????????????????????")
    public String minQty;
    @ExcelProperty("??????")
    public String remark;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorOutputRow {
    @ExcelProperty("????????????????????????")
    public String code;
    @ExcelProperty("????????????????????????")
    public String skuQpc;
    @ExcelProperty("??????????????????")
    public String skuUnit;
    @ExcelProperty("????????????????????????????????????")
    public String inPrice;
    @ExcelProperty("????????????????????????")
    public String isLimit;
    @ExcelProperty("???????????????????????????")
    public String limitQty;
    @ExcelProperty("???????????????????????????????????????")
    public String minQty;
    @ExcelProperty("??????")
    public String remark;
    @ExcelProperty("??????")
    private String state;
    @ExcelProperty("??????")
    private String message;

    public ErrorOutputRow(ExcelInputRow source, String state, String message) {
      BeanUtils.copyProperties(source, this);
      this.state = state;
      this.message = message;
    }
  }
}
