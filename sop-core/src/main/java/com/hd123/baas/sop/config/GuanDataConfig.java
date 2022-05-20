package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
@BcGroup(name = "观远数据")
public class GuanDataConfig {

  @BcKey(name = "业务地址")
  private String biUrl = "https://app.mayidata.com";
  @BcKey(name = "观远私钥")
  private String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKl+0Tihp4YY9KusCCSICidm32VW/IM07TDZK1GuhSUuci1zvZzt+82xqiAnneJ0Z7sPwBflV9qDFr/0RIWKDMn00HYkIvLikuWWpNctNbczPem+WVYJA+CZbdzK/TJcOhPvkMnGQUyy1X5LfIy0N8cbh1uBIbaFRcxWoGLt4aQnAgMBAAECgYA4G9oo1CK/2n0MY3uilEJAPubPBle7G4c1d37GoG5FG7YCY+EuFP4ZXqoB0PuMHprEKvedMXe+x0x9qOASENwgaKP0K2+IxhOglJs3dfGz6kzjofd1LdhvGhuDUDVWaREcjxIIvbXqTNql32gxWEpXCJAZ88Gdn50sWCnLwlHLgQJBAN8mLTG/7Y0psiV2zNnjQR4e2sHRi54MopHz+2TsVh0uqZNC2MAVwQRogjOQ5LrRwF8LQuZKfWF1eNIlI15dEucCQQDCcpdmblioXybEJdCiM++6atm2G3QfBp5GaOg78bN/CpgJOc0bqXBwH2Ex3iv2edKj/42meAFAqsR5eG3TrfzBAkEAl+JFOqp3BvENZ0CQN/HdPaIkpW16CU5yTMNzJgNSrbQ4CZqjK0LjSJvVm0GQ3bOsq0Rf+Z1T78TkQqyygST6mwJAEH5V10tu28FOcX7fppKPOBnOI8NKY0NVc5V8dXE4D4Ofh9DOVBVYQzp2LRuyUPLeaijIJCGzwX96sO8FKdptQQJBALk+VCXwyYE8Tjym3TuS26Uy75XGW+5PrNphxMUVyFgHfPjZe1Nav1/8X+EpO5ycYQJuVXeNTnmFWbvoGWuIM1w=";
  @BcKey(name = "观远提供者")
  private String providerName = "ssotest";
  @BcKey(name = "观远租户ID")
  private String domainId = "ssotest";
}
