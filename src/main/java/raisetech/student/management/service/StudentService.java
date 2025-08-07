package raisetech.student.management.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourses;
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

  /*受講生一覧検索。
   * 全件検索を行うので、条件指定は行いません。
   *
   * ＠return 受講生一覧（全件）
   * */
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.search();
    List<StudentCourses> studentCoursesList = repository.searchStudentsCoursesList();
    return converter.convertStudentDetails(studentList, studentCoursesList);
  }


/*
* 受講生検索です。
* IDに紐づく受講生情報を取得した後、その受講生に紐づく受講生コースの情報を取得して設定します。
*
* @param id 受講生ID
* @return 受講生
* */
  public StudentDetail searchStudent(Integer id) {
    Student student = repository.searchStudent(id);
    List<StudentCourses> studentsCourses = repository.searchStudentsCourses(student.getId());
    return new StudentDetail(student, studentsCourses);
  }

  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail){
    repository.registerStudent(studentDetail.getStudent());
    for (StudentCourses studentsCourse : studentDetail.getStudentCourses()){
      studentsCourse.setStudentId(studentDetail.getStudent().getId());
      studentsCourse.setCourseStartAt(LocalDateTime.now());
      studentsCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));
      repository.registerStudentCourse(studentsCourse);
    }
    return studentDetail;
  }

  @Transactional
  public void updateStudent(StudentDetail studentDetail){
    repository.updateStudent(studentDetail.getStudent());
    if (studentDetail.getStudentCourses() != null) {
      for (StudentCourses course : studentDetail.getStudentCourses()) {
        if (course.getId() == null ) {
          // 新規追加（INSERT）
          course.setStudentId(studentDetail.getStudent().getId());
          course.setCourseStartAt(LocalDateTime.now());
          course.setCourseEndAt(LocalDateTime.now().plusYears(1));
          repository.registerStudentCourse(course);
        } else {
          // 既存更新（UPDATE）
          repository.updateStudentCourse(course);
        }
      }
      }
  }
}
