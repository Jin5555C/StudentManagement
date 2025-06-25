package raisetech.student.management;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourse {

  private String id;
  private String studentId;
  private String courseName;
  private Timestamp courseStartAt;
  private Timestamp courseEndAt;

}
