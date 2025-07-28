package raisetech.student.management.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.PrefectureList;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourses;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

@Controller
public class StudentController {

  private StudentService service;
  private StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  @GetMapping("/studentList")
  public String getStudentList(Model model) {
    List<Student> students = service.searchStudentList();
    List<StudentCourses> studentCourses = service.searchStudentCourseList();

    model.addAttribute("studentList", converter.convertStudentDetails(students, studentCourses));

    return "studentList";
  }


  @GetMapping("/studentCourseList")
  public List<StudentCourses> getStudentCourseList() {
    return service.searchStudentCourseList();
  }

  @GetMapping("/newStudent")
  public String newStudent(Model model){
    model.addAttribute("studentDetail", new StudentDetail());
    model.addAttribute("prefectureList", PrefectureList.getAll());
    model.addAttribute("genderList", List.of("男性","女性","その他"));
    return "registerStudent";
  }

  @PostMapping("/registerStudent")
  public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result){
    if (result.hasErrors()){
      return "registerStudent";
    }
    // Student を保存
    Student student = studentDetail.getStudent();
    service.insertStudent(student);

    // StudentCourses を保存（コースが1つの場合でもListで扱う）
    List<StudentCourses> courseList = studentDetail.getStudentCourses();
    if (courseList != null && !courseList.isEmpty()) {
      for (StudentCourses course : courseList) {
        course.setStudentId(student.getId()); // 外部キーとして student_id を設定
        service.insertStudentCourse(course);
      }
    }

    return "redirect:/studentList";
  }
}