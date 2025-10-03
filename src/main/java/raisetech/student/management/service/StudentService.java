package raisetech.student.management.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

/*
 * 受講生情報を取り扱うサービスです。
 * 受講生の検索や登録・更新処理を行います。
 * */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {

    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 受講生詳細一覧検索。 全件検索を行うので、条件指定は行いません。
   * ＠return 受講生一覧（全件）
   */
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.search();
    List<StudentCourse> studentCoursesList = repository.searchStudentCourseList();
    List<ApplicationStatus> applicationStatusList = repository.searchApplicationStatusList();

    return converter.convertStudentDetails(studentList, studentCoursesList,applicationStatusList);
  }


  /**
   * 受講生検索。 IDに紐づく受講生情報を取得した後、その受講生に紐づく受講生コースの情報を取得して設定します。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  public StudentDetail searchStudent(Integer id) {
    Student student = repository.searchStudent(id);
    if (student == null) {
      return null;
    }
    List<StudentCourse> studentCourseList = repository.searchStudentCourse(student.getId());
    List<ApplicationStatus> applicationStatusList = repository.searchApplicationStatusList();

    List<StudentDetail> studentDetailList = converter.convertStudentDetails(
            List.of(student),
            studentCourseList,
            applicationStatusList
    );

    return studentDetailList.isEmpty() ? null : studentDetailList.getFirst();
  }

  /**
   * 受講生の条件検索。 検索条件に一致した受講生譲渡それに紐づくコース情報を返します。
   *
   * @param searchCondition 検索条件
   * @return 条件に一致した受講生詳細一覧
   */
  public List<StudentDetail> searchStudentList(Student searchCondition) {
    List<Student> studentList = repository.searchStudentList(searchCondition);
    if (studentList.isEmpty()) {
      return Collections.emptyList();
    }

    List<Integer> studentIdList = studentList
            .stream()
            .map(Student::getId)
            .collect(Collectors.toList());

    List<StudentCourse> studentCourseList = repository.searchStudentCoursesByStudentIdList(
            studentIdList);

    List<ApplicationStatus> applicationStatusList = repository.searchApplicationStatusList();

    return converter.convertStudentDetails(studentList, studentCourseList, applicationStatusList);
  }


  /**
   * 受講生詳細の登録を行います。 受講生と受講生コース情報を個別に登録し、受講生コース情報には受講生情報を紐づける値とコース開始日、コース終了日を設定します。
   *
   * @param studentDetail 受講生詳細
   * @return 登録情報を付与した受講生詳細
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();

    repository.registerStudent(student);
    studentDetail.getStudentCourseList().forEach(studentsCourse -> {
      initStudentsCourse(studentsCourse, student);
      repository.registerStudentCourse(studentsCourse);

      ApplicationStatus newStatus = new ApplicationStatus();
      newStatus.setCourseId(studentsCourse.getId());
      newStatus.setStatus("仮申込");
      newStatus.setCreateAt(LocalDateTime.now());
      newStatus.setUpdateAt(LocalDateTime.now());
      repository.registerApplicationStatus(newStatus);
    });
    return studentDetail;
  }

  /**
   * 受講生コース情報を登録する際の初期情報を設定する。
   *
   * @param studentCourse 　受講生コース情報
   * @param student       　受講生
   */
  private static void initStudentsCourse(StudentCourse studentCourse, Student student) {
    LocalDateTime now = LocalDateTime.now();

    studentCourse.setStudentId(student.getId());
    studentCourse.setCourseStartAt(now);
    studentCourse.setCourseEndAt(now.plusYears(1));
  }

  /**
   * 受講生詳細の更新を行います。 受講生と受講生コース情報をそれぞれ更新します。
   *
   * @param studentDetail 　受講生詳細
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    if (studentDetail.getStudentCourseList() != null) {
      studentDetail.getStudentCourseList().forEach(course -> {
        if (course.getId() == null) {
          // 新規追加（INSERT）
          initStudentsCourse(course, studentDetail.getStudent());
          repository.registerStudentCourse(course);

          ApplicationStatus newStatus = new ApplicationStatus();
          newStatus.setCourseId(course.getId());
          newStatus.setStatus("仮申込");
          newStatus.setCreateAt(LocalDateTime.now());
          newStatus.setUpdateAt(LocalDateTime.now());
          repository.registerApplicationStatus(newStatus);
        } else {
          // 既存更新（UPDATE）
          repository.updateStudentCourse(course);
          ApplicationStatus applicationStatus = course.getApplicationStatus();
          if (applicationStatus != null && applicationStatus.getId() != null) {
            applicationStatus.setUpdateAt(LocalDateTime.now());
            repository.updateApplicationStatus(applicationStatus);
          }
        }
      });
    }
  }

  /**
   * 全てのコースの申し込み状況を取得します。
   *
   * @return 申し込み状況の一覧
   */
  public List<ApplicationStatus> searchApplicationStatusList() {
    return repository.searchApplicationStatusList();
  }

  /**
   * IDに紐づくコースの申し込み状況を取得します。
   *
   * @param courseId コースID (students_courses.id)
   * @return 申し込み状況
   */
  public ApplicationStatus searchApplicationStatus(Integer courseId) {
    return repository.searchApplicationStatus(courseId);
  }
}