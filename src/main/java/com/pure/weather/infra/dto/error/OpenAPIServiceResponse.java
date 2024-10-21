package com.pure.weather.infra.dto.error;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "OpenAPI_ServiceResponse")
public class OpenAPIServiceResponse {
  private CmmMsgHeader cmmMsgHeader;

  @XmlElement(name = "cmmMsgHeader")
  public void setCmmMsgHeader(CmmMsgHeader cmmMsgHeader) {
    this.cmmMsgHeader = cmmMsgHeader;
  }

  @Getter
  @Setter
  public static class CmmMsgHeader {
    private String errMsg;
    private String returnAuthMsg;
    private String returnReasonCode;

    @XmlElement(name = "errMsg")
    public void setErrMsg(String errMsg) {
      this.errMsg = errMsg;
    }

    @XmlElement(name = "returnAuthMsg")
    public void setReturnAuthMsg(String returnAuthMsg) {
      this.returnAuthMsg = returnAuthMsg;
    }

    @XmlElement(name = "returnReasonCode")
    public void setReturnReasonCode(String returnReasonCode) {
      this.returnReasonCode = returnReasonCode;
    }
  }
}
