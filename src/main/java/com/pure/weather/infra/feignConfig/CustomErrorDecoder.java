package com.pure.weather.infra.feignConfig;

import com.pure.weather.infra.dto.error.OpenAPIServiceResponse;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.springframework.http.HttpHeaders;

public class CustomErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, feign.Response response) {
    String contentType =
        response
            .headers()
            .getOrDefault(HttpHeaders.CONTENT_TYPE, Collections.singleton(""))
            .toString();
    if (contentType.contains("text/xml")) {
      try {
        InputStream body = response.body().asInputStream();
        JAXBContext jaxbContext = JAXBContext.newInstance(OpenAPIServiceResponse.class);
        OpenAPIServiceResponse apiErrorResponse =
            (OpenAPIServiceResponse) jaxbContext.createUnmarshaller().unmarshal(body);

        String errMsg = apiErrorResponse.getCmmMsgHeader().getErrMsg();
        String returnReasonCode = apiErrorResponse.getCmmMsgHeader().getReturnReasonCode();

        if (returnReasonCode.equals("04")) {
          return new RetryableException(
              response.status(),
              errMsg,
              response.request().httpMethod(),
              1000L,
              response.request());
        }

        return new RuntimeException(
            String.format("Feign Error: %s , Code: %s%n", errMsg, returnReasonCode));
      } catch (JAXBException | IOException e) {
        return new RuntimeException("Error decoding XML response", e);
      }
    }

    return new Default().decode(methodKey, response);
  }
}
