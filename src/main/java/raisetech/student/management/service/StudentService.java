package raisetech.student.management.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourses;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@Service
public class StudentService {
  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    return repository.search();
  }

  public StudentDetail searchStudent(Integer id) {
    Student student = repository.searchStudent(id);
    List<StudentCourses> studentsCourses = repository.searchStudentsCourses(student.getId());
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourses(studentsCourses);
    return studentDetail;
  }

  public List<StudentCourses> searchStudentCoursesList() {
    return repository.searchStudentsCoursesList();
  }

  @Transactional
  public void registerStudent(StudentDetail studentDetail){
    repository.registerStudent(studentDetail.getStudent());
    for (StudentCourses studentsCourse : studentDetail.getStudentCourses()){
      studentsCourse.setStudentId(studentDetail.getStudent().getId());
      studentsCourse.setCourseStartAt(LocalDateTime.now());
      studentsCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));
      repository.registerStudentCourse(studentsCourse);
    }
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
