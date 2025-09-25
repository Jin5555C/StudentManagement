package raisetech.student.management.data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * コースの申込状況を扱うオブジェクトです。
 */
@Getter
@Setter
public class ApplicationStatus {

  private Integer id;
  private Integer courseId;
  private String status;
  private LocalDateTime createAt;
  private LocalDateTime updateAt;
}
