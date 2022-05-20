package com.hd123.baas.sop.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qianfan123.baas.common.BaasException;

/**
 * @author W.J.H.7
 **/
public class StringVarUtil {
  public static void main(String[] args) throws BaasException {
    String content = "消费${amount}元，欢迎再次光临(${shopName}, {测试})";
    Map<String, String> params = new HashMap<>();
    params.put("amount", "10.00");
    params.put("shopName", "海鼎未来店");
    System.out.println(process(content, params));
  }

  public static String process(String template, Map<String, String> params) throws BaasException {
    if (params == null || params.size() == 0) {
      return template;
    }
    StringBuffer sb = new StringBuffer();
    Matcher m = Pattern.compile("\\$\\{\\w+\\}").matcher(template);
    while (m.find()) {
      String param = m.group();
      if (params == null) {
        throw new BaasException("缺失变量");
      }
      String paramVar = param.substring(2, param.length() - 1);
      String value = params.get(paramVar);
      if (value == null) {
        throw new BaasException("缺失变量{0}", paramVar);
      }
      m.appendReplacement(sb, value);
    }
    m.appendTail(sb);
    return sb.toString();
  }

}
