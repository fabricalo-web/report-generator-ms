package pe.com.fabricaloweb.report.exception;

public class WebServiceException extends RuntimeException {

  public WebServiceException(String message, Throwable root) {
    super(message, root);
  }

}
