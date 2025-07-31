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
 *
 * 全件検索、単一検索によりコース情報の検索が行えるレポジトリです。
 */
@Mapper
public interface StudentRepository {

  /**
   *
   * @return 全件検索した受講生情報の一覧
   */
  @Select("SELECT * FROM students")
  List<Student> search();

  @Select("SELECT * FROM students WHERE id = #{id}")
  Student searchStudent(Integer id);

  @Select("SELECT * FROM students_courses")
  List<StudentCourses> searchStudentsCoursesList();

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

