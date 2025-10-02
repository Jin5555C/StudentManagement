package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

/**
 * 受講生情報を扱うレポジトリ 　受講生テーブルと受講生コース情報テーブルに紐づくRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 受講生の全件検索を行います。
   *
   * @return 受講生一覧（全件）
   */
  List<Student> search();

  /**
   * 受講生の検索を行います。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  Student searchStudent(Integer id);

  /**
   * 受講生を条件で検索します。
   *
   * @param student 検索条件
   * @return 条件に一致した受講生一覧
   */
  List<Student> searchStudentList(Student student);

  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return 受講生のコース情報一覧（全件）
   */
  List<StudentCourse> searchStudentCourseList();


  /**
   * 受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param student_id 受講生ID
   * @return 受講生IDに紐づく受講コース情報
   */
  List<StudentCourse> searchStudentCourse(Integer student_id);

  /**
   * 受講生IDのリストに紐づく受講生コース情報を検索します。
   *
   * @param student_id_list 受講生IDのリスト
   * @return 受講生IDに紐づく受講コース情報
   */
  List<StudentCourse> searchStudentCoursesByStudentIdList(
      @Param("studentIdList") List<Integer> student_id_list);

  /**
   * 受講生を新規登録します。IDに関しては自動採番を行う。
   *
   * @param student 　受講生
   */
  void registerStudent(Student student);

  /**
   * 受講生コース情報を登録する際の初期情報を設定する。
   *
   * @param studentCourse 　受講生コース情報
   */
  void registerStudentCourse(StudentCourse studentCourse);


  /**
   * 受講生を更新します。
   *
   * @param student 　受講生
   */
  void updateStudent(Student student);

  /**
   * 受講生コース情報のコース名を更新します。
   *
   * @param studentCourse 　受講生コース情報
   */
  void updateStudentCourse(StudentCourse studentCourse);

  /**
   * 全てのコースの申し込み状況（仮申込、本申込、受講中、受講終了）を取得します。
   *
   * @return 申し込み状況の一覧
   */
  List<ApplicationStatus> searchApplicationStatusList();

  /**
   * IDに紐づくコースの申し込み状況を取得します。
   *
   * @param id 申し込み状況ID
   * @return 申し込み状況
   */
  ApplicationStatus searchApplicationStatus(Integer id);


}

