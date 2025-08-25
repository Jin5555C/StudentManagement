package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;

/*
* 受講生詳細を受講生と受講生コース情報、もしくはその逆の変換を行うコンバーターです
* */
@Component
public class StudentConverter {

  /*
  * 受講生に紐づく受講生コース情報をマッピングする。
  * 受講生コース情報は受講生に対して複数存在するので、ループをまわして受講生詳細情報を組み立てる。
  *
  * @param students 受講生一覧
  * @param StudentCourses 受講生コース情報のリスト
  * @return 受講生詳細情報のリスト
  * */
  public List<StudentDetail> convertStudentDetails(List<Student> students, List<StudentCourse> studentCours) {
    List<StudentDetail> studentDetails = new ArrayList<>();
    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);

      List<StudentCourse> convertStudentCours = studentCours.stream()
          .filter(studentCourse -> student.getId().equals(studentCourse.getStudentId()))
          .collect(Collectors.toList());
      studentDetail.setStudentCourseList(convertStudentCours);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }
}
