package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

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
  List<Student> searchStudentList();

  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchStudentCourses();

}

