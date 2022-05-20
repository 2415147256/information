package com.hd123.baas.sop.excel.job.explosive;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.hd123.baas.sop.excel.common.ImportExcelListener;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivityLine;
import com.hd123.baas.sop.service.api.promotion.EntityType;
import com.hd123.baas.sop.service.api.promotion.PomEntity;
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
 * @author dpmao
 */
@Component("ExplosiveSkuImportProcessorImpl")
public class ExplosiveSkuImportProcessorImpl implements ExplosiveSkuImportProcessor {

  @Autowired(required = false)
  private Bucket bucket;
  @Autowired
  private SkuService skuService;

  @Override
  public ImportResult<List<Sku>> doImport(String tenantId, String orgId, MultipartFile file, ExplosiveSkuImportProcessor.Params param) {
    ImportResult result = new ImportResult();
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImportProcessData processData = new ImportProcessData();
      BeanUtils.copyProperties(param, processData);
      EasyExcel.read(file.getInputStream(), ExcelInputRow.class, newExcelListener(tenantId, orgId, processData)).sheet().doRead();
      result.setIgnoreCount(processData.getIgnoreCount());
      result.setFailCount(processData.getFailedCount());
      result.setSuccessCount(processData.getSkuList().size());
      result.setSuccessList(processData.getSkuList());

      if (!processData.getErrorRowData().isEmpty()) {
        EasyExcel.write(outputStream, ErrorOutputRow.class)
            .useDefaultStyle(false)
            .sheet().doWrite(processData.getErrorRowData());
        try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
          String path = "sop/tempOf1Day/" + tenantId + "/importResult/sku/" + UUID.randomUUID().toString() + "/" + "促销商品导入失败明细.xlsx";
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
        List<ExplosiveActivityLine> result = new ArrayList<>();
        for (ExcelInputRow row : excelRows) {
          if (StringUtils.isBlank(row.getCode())) {
            errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "商品代码不能为空"));
            failedCount++;
            continue;
          }
          BigDecimal qpc = new BigDecimal("1");
          Sku sku = skuMap.get(MessageFormat.format("{0},{1}", row.getCode().toUpperCase(), qpc.stripTrailingZeros().toPlainString()));
          if (sku == null) {
            errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "指定规格为1*1的商品不存在或不可用"));
            failedCount++;
            continue;
          }
          BigDecimal alcQpc;
          try {
            alcQpc = new BigDecimal(row.getAlcQpc());
          } catch (Exception e) {
            errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "订货规格解析失败，注意1*1请填写为1"));
            failedCount++;
            continue;
          }
          Sku aclSku = skuMap.get(MessageFormat.format("{0},{1}", row.getCode().toUpperCase(), alcQpc.stripTrailingZeros().toPlainString()));
          if (aclSku == null) {
            errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "指定订货的商品不存在或不可用"));
            failedCount++;
            continue;
          }
          String skuKey = MessageFormat.format("{0},{1}", sku.getGoodsGid(), sku.getQpc().stripTrailingZeros().toPlainString());
          if (processData.getSelectedList().contains(skuKey)) {
            errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "忽略", "指定的商品已选中"));
            ignoreCount++;
          } else if (processData.getExcludeList().contains(skuKey)) {
            errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "指定的商品此处不被允许"));
            failedCount++;
          } else if (processData.getSkuKeys().contains(skuKey)) {
            errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "忽略", "指定的商品重复导入"));
            ignoreCount++;
          } else {
            processData.getSkuKeys().add(skuKey);
            PomEntity pomEntity = new PomEntity();
            pomEntity.setUuid(sku.getGoodsGid());
            pomEntity.setCode(sku.getCode());
            pomEntity.setName(sku.name);
            pomEntity.setEntityType(EntityType.product);
            pomEntity.setMeasureUnit(sku.getUnit());
            pomEntity.setQpc(qpc);
            pomEntity.setAlcQpc(alcQpc);
            pomEntity.setAlcUnit(aclSku.getUnit());
            pomEntity.setCategory(sku.getCategory());
            pomEntity.setPrice(sku.getPrice());
            pomEntity.setSkuId(sku.getId());
            BigDecimal prmPrice;
            BigDecimal minSignQty;
            BigDecimal maxSignQty;
            BigDecimal suggestQty;
            try {
              prmPrice = new BigDecimal(row.getPrmPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "促销价解析失败"));
              failedCount++;
              continue;
            }
            if (prmPrice.compareTo(BigDecimal.ZERO) <= 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "促销价必须大于0"));
              failedCount++;
              continue;
            } else if (prmPrice.compareTo(new BigDecimal("99999999.99")) > 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "促销价超过最大值(99999999.99)"));
              failedCount++;
              continue;
            }

            try {
              minSignQty = new BigDecimal(row.getMinSignQty());
              if (new BigDecimal(minSignQty.intValue()).compareTo(minSignQty) != 0) {
                errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最小订货份数不为整数"));
                failedCount++;
                continue;
              }
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最小订货份数解析失败"));
              failedCount++;
              continue;
            }
            if (minSignQty.compareTo(BigDecimal.ZERO) < 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最小订货份数必须大于等于0"));
              failedCount++;
              continue;
            } else if (minSignQty.compareTo(new BigDecimal("99999999.99")) > 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最小订货份数超过最大值(99999999.99)"));
              failedCount++;
              continue;
            }

            try {
              maxSignQty = new BigDecimal(row.getMaxSignQty());
              if (new BigDecimal(maxSignQty.intValue()).compareTo(maxSignQty) != 0) {
                errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最大订货份数不为整数"));
                failedCount++;
                continue;
              }
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最大订货份数解析失败"));
              failedCount++;
              continue;
            }
            if (maxSignQty.compareTo(BigDecimal.ZERO) <= 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最大订货份数必须大于0"));
              failedCount++;
              continue;
            } else if (maxSignQty.compareTo(minSignQty) < 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最大订货份数不能小于每门店最小订货份数"));
              failedCount++;
              continue;
            } else if (maxSignQty.compareTo(new BigDecimal("99999999.99")) > 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "每门店最大订货份数超过最大值(99999999.99)"));
              failedCount++;
              continue;
            }
            try {
              suggestQty = new BigDecimal(row.getSuggestQty());
              if (new BigDecimal(suggestQty.intValue()).compareTo(suggestQty) != 0) {
                errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "建议份数不为整数"));
                failedCount++;
                continue;
              }
            } catch (Exception e) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "建议份数解析失败"));
              failedCount++;
              continue;
            }
            if (suggestQty.compareTo(BigDecimal.ZERO) < 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "建议份数必须大于0"));
              failedCount++;
              continue;
            } else if (suggestQty.compareTo(minSignQty) < 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "建议份数不能小于每日最小订货份数"));
              failedCount++;
              continue;
            } else if (suggestQty.compareTo(maxSignQty) > 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "建议份数不能大于每日最大订货份数"));
              failedCount++;
              continue;
            } else if (suggestQty.compareTo(new BigDecimal("99999999.99")) > 0) {
              errorOutputRows.add(new ExplosiveSkuImportProcessorImpl.ErrorOutputRow(row, "失败", "建议份数超过最大值(99999999.99)"));
              failedCount++;
              continue;
            }

            ExplosiveActivityLine item = new ExplosiveActivityLine();
            item.setEntity(pomEntity);
            item.setPrmPrice(prmPrice);
            item.setMinSignQty(minSignQty);
            item.setMaxSignQty(maxSignQty);
            item.setSuggestQty(suggestQty);
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
       * 查询sku
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
   * @author Silent
   **/
  @Data
  public static class ExcelInputRow {
    @ExcelProperty("商品代码")
    public String code;
    @ExcelProperty("促销价")
    public String prmPrice;
    @ExcelProperty("每门店最小订货份数")
    public String minSignQty;
    @ExcelProperty("每门店最大订货份数")
    public String maxSignQty;
    @ExcelProperty("订货规格")
    public String alcQpc;
    @ExcelProperty("建议份数")
    public String suggestQty;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorOutputRow {
    @ExcelProperty("商品代码")
    private String code;
    @ExcelProperty("促销价")
    public String prmPrice;
    @ExcelProperty("每门店最小订货份数")
    public String minSignQty;
    @ExcelProperty("每门店最大订货份数")
    public String maxSignQty;
    @ExcelProperty("订货规格")
    public String alcQpc;
    @ExcelProperty("建议份数")
    public String suggestQty;
    @ExcelProperty("状态")
    private String state;
    @ExcelProperty("说明")
    private String message;

    public ErrorOutputRow(ExcelInputRow source, String state, String message) {
      BeanUtils.copyProperties(source, this);
      this.state = state;
      this.message = message;
    }
  }
}
