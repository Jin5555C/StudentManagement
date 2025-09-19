package raisetech.student.management.exception;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // 404 Not Found
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    String transactionId = UUID.randomUUID().toString();
    String errorCode = "ERR-404";

    logger.warn("[{}] ResourceNotFoundException: {}", transactionId, ex.getMessage(), ex);

    ErrorResponse error = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.getReasonPhrase(),
        "リクエストの処理に失敗しました。",
        transactionId,
        errorCode
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  // 400 Bad Request（バリデーションなど）
  @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      org.springframework.web.bind.MethodArgumentNotValidException ex) {

    String transactionId = UUID.randomUUID().toString();
    String errorCode = "ERR-400";

    String message = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .reduce("", (s1, s2) -> s1 + s2 + "; ");

    logger.info("[{}] Validation failed: {}", transactionId, message);

    List<ErrorResponse.FieldErrorDetail> details = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(err -> new ErrorResponse.FieldErrorDetail(err.getField(), err.getDefaultMessage()))
        .toList();

    ErrorResponse error = new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        "入力内容に誤りがあります。",
        transactionId,
        errorCode,
        details
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  // 予期せぬ例外
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    String transactionId = UUID.randomUUID().toString();
    String errorCode = "ERR-500";

    logger.error("[{}] Unexpected error occurred", transactionId, ex);

    ErrorResponse error = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
        "予期せぬエラーが発生しました",
        transactionId,
        errorCode
    );

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(error);
  }
}
