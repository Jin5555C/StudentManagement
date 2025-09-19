package raisetech.student.management.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {

  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String transactionId;
  private String errorCode;
  private List<FieldErrorDetail> details;

  public ErrorResponse(int status, String error, String message, String transactionId,
      String errorCode) {
    this(status, error, message, transactionId, errorCode, null);
  }

  public ErrorResponse(int status, String error, String message, String transactionId,
      String errorCode, List<FieldErrorDetail> details) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.message = message;
    this.transactionId = transactionId;
    this.errorCode = errorCode;
    this.details = details;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public List<FieldErrorDetail> getDetails() {
    return details;
  }

  public static class FieldErrorDetail {

    private final String field;
    private final String message;

    public FieldErrorDetail(String field, String message) {
      this.field = field;
      this.message = message;
    }

    public String getField() {
      return field;
    }

    public String getMessage() {
      return message;
    }
  }

}
