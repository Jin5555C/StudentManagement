package raisetech.student.management.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.repository.StudentRepository;

@Service
public class StudentService {
  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  // 年齢が30代の人のみを抽出する
  public List<Student> searchStudentList() {
    List<Student> allStudents = repository.searchStudentList();

    return allStudents.stream()
        .filter(student -> {
          int age = student.getAge();
          return age >= 10 && age < 20;
        })
        .collect(Collectors.toList());
  }

  public List<StudentCourse> searchStudentCourseList() {
    //絞り込み検索で「Javaコース」のコース情報のみを抽出する。
    //抽出したリストをコントローラーに返す。
    return repository.searchStudentCourses();
  }
}
