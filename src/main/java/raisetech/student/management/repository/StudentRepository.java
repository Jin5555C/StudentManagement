package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

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
  List<Student> search();

  /**
   *受講生の検索を行います。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  Student searchStudent(Integer id);

  /**
   *受講生のコース情報の全件検索を行います。
   *
   * @return 受講生のコース情報一覧（全件）
   */
  List<StudentCourse> searchStudentCourseList();


  /**
   *受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param student_id 受講生ID
   * @return 受講生IDに紐づく受講コース情報
   */
  List<StudentCourse> searchStudentCourse(Integer student_id);

  /**
   *  受講生を新規登録します。IDに関しては自動採番を行う。
   *
   * @param student　受講生
   */
  void registerStudent(Student student);

  /**
   * 受講生コース情報を登録する際の初期情報を設定する。
   *
   * @param studentCourse　受講生コース情報
   */
  void registerStudentCourse(StudentCourse studentCourse);


  /**
   * 受講生を更新します。
   *
   * @param student　受講生
   */
  void updateStudent(Student student);

  /**
   * 受講生コース情報のコース名を更新します。
   *
   * @param studentCourse　受講生コース情報
   */
  void updateStudentCourse(StudentCourse studentCourse);


}

