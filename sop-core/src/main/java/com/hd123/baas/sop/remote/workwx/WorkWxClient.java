package com.hd123.baas.sop.remote.workwx;

import com.hd123.baas.sop.remote.workwx.request.ApprovalReq;
import com.hd123.baas.sop.remote.workwx.request.MobileWorkWxReq;
import com.hd123.baas.sop.remote.workwx.request.TemplateWorkWxReq;
import com.hd123.baas.sop.remote.workwx.request.WorkWxApprovalApply;
import com.hd123.baas.sop.remote.workwx.response.WorkApplyDetailResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkApplyResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkApplyTemplateResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkWxImageResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkWxTokenResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkWxUploadResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkWxUserResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author W.J.H.7
 */
@FeignClient(name = "workWxClient", configuration = WorkWxConfiguration.class)
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
public interface WorkWxClient {
  String TOKEN_URL = "cgi-bin/gettoken";
  String IMAGE = "image";
  String VOICE = "voice";
  String VIDEO = "video";
  String FILE = "file";

  @ApiOperation("获取token")
  @GetMapping(value = "/cgi-bin/gettoken")
  WorkWxTokenResponse token(
      @RequestParam(value = "corpid") String corpId,
      @RequestParam(value = "corpsecret") String secret);

  @ApiOperation("根据手机号获取用户ID")
  @GetMapping(value = "/cgi-bin/user/getuserid")
  WorkWxUserResponse getUserByMobile(
      @RequestHeader(value = "tenant") String tenant,
      @RequestBody MobileWorkWxReq request);

  @ApiOperation("根据用户ID获取用户详情")
  @GetMapping(value = "/cgi-bin/user/get")
  WorkWxUserResponse getUser(
      @RequestHeader(value = "tenant") String tenant,
      @RequestParam("userid") String userid);

  @ApiOperation("根据模板id获取模板")
  @PostMapping(value = "/cgi-bin/oa/gettemplatedetail")
  WorkApplyTemplateResponse getTemplateDetail(
      @RequestHeader(value = "tenant") String tenant,
      @RequestBody TemplateWorkWxReq req);

  @ApiOperation("提交申请")
  @PostMapping(value = "/cgi-bin/oa/applyevent")
  WorkApplyResponse apply(
      @RequestHeader(value = "tenant") String tenant,
      @RequestBody WorkWxApprovalApply req);

  @ApiOperation("获取审批申请详情")
  @PostMapping(value = "cgi-bin/oa/getapprovaldetail")
  WorkApplyDetailResponse getApprovalDetail(
      @RequestHeader(value = "tenant") String tenant,
      @RequestBody ApprovalReq req);

  @ApiOperation(("上传图片"))
  @PostMapping(value = "/cgi-bin/media/uploadimg", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  WorkWxImageResponse uploadImage(
      @RequestHeader(value = "tenant") String tenant,
      @RequestPart("file") MultipartFile file);

  @ApiOperation(("上传临时素材"))
  @PostMapping(value = "cgi-bin/media/upload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  WorkWxUploadResponse upload(
      @RequestHeader(value = "tenant") String tenant,
      @RequestParam(value = "type") String type,
      @RequestPart("file") MultipartFile file);

}
