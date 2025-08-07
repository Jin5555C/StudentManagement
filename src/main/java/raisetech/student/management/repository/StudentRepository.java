package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourses;

/**
 * 受講生情報を扱うレポジトリ
 *　受講生テーブルと受講生コース情報テーブルに紐づくRepositoryです。
 *
 */
@Mapper
public interface StudentRepository {

  /**
   *受講生の全件検索を行います。
   *
   * @return 受講生一覧（全件）
   */
  @Select("SELECT * FROM students")
  List<Student> search();

  /**
   *受講生の検索を行います。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student searchStudent(Integer id);

  /**
   *受講生のコース情報の全件検索を行います。
   *
   * @return 受講生のコース情報一覧（全件）
   */
  @Select("SELECT * FROM students_courses")
  List<StudentCourses> searchStudentsCoursesList();


  /**
   *受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param student_id 受講生ID
   * @return 受講生IDに紐づく受講コース情報
   */
  @Select("SELECT * FROM students_courses WHERE student_id = #{student_id}")
  List<StudentCourses> searchStudentsCourses(Integer student_id);

  @Insert("INSERT INTO students(name, kana_name, nickname, email, area, age, sex, remark, isDeleted) " +
      "VALUES(#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudent(Student student);

  @Insert("INSERT INTO students_courses (student_id, course_name, course_start_at, course_end_at) " +
      "VALUES(#{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudentCourse(StudentCourses studentCourse);

  @Update("UPDATE students SET " +
      "name = #{name}, " +
      "kana_name = #{kanaName}, " +
      "nickname = #{nickname}, " +
      "email = #{email}, " +
      "area = #{area}, " +
      "age = #{age}, " +
      "sex = #{sex}, " +
      "remark = #{remark}, " +
      "isDeleted = #{isDeleted} " +
      "WHERE id = #{id}")
  void updateStudent(Student student);

  @Update("UPDATE students_courses SET course_name = #{courseName} WHERE id = #{id}")
  void updateStudentCourse(StudentCourses studentCourse);


}

