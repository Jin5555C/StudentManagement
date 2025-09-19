package raisetech.student.management.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import raisetech.student.management.validation.CreateValidationGroup;
import raisetech.student.management.validation.UpdateValidationGroup;

/**
 * 受講生を扱うオブジェクトです。
 */
@Getter
@Setter
public class Student {

  // 登録時はnull、更新時はnullでないことを指定
  @Null(groups = CreateValidationGroup.class, message = "IDは指定できません。")
  @NotNull(groups = UpdateValidationGroup.class, message = "IDを指定してください。")
  private Integer id;

  // 登録・更新の両方で必須
  @NotBlank(groups = {CreateValidationGroup.class,
      UpdateValidationGroup.class}, message = "入力必須の項目です。入力して下さい")
  private String name;

  // 登録・更新の両方で必須
  @NotBlank(groups = {CreateValidationGroup.class,
      UpdateValidationGroup.class}, message = "入力必須の項目です。入力して下さい")
  private String kanaName;

  private String nickname;

  // 登録・更新の両方で必須
  @NotBlank(groups = {CreateValidationGroup.class,
      UpdateValidationGroup.class}, message = "入力必須の項目です。入力して下さい")
  @Email(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
  private String email;

  private String area;

  private Integer age;

  private String sex;

  private String remark;

  private boolean isDeleted;
}
