package raisetech.student.management.data;

import lombok.Getter;
import lombok.Setter;

/**
 * 受講生を扱うオブジェクトです。
 */
@Getter
@Setter
public class Student {

  private Integer id;
  private String name;
  private String kanaName;
  private String nickname;
  private String email;
  private String area;
  private Integer age;
  private String sex;
  private String remark;
  private boolean isDeleted;

}
