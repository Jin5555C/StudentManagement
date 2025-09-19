package raisetech.student.management.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.exception.ResourceNotFoundException;
import raisetech.student.management.service.StudentService;

/**
 * 受講生の検索や登録、更新などを置こうなREST　APIを受け付ける実行されるControllerです。
 */

@Validated
@RestController
public class StudentController {

  private StudentService service;


  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }


  /**
   * 受講生詳細一覧検索。 全件検索を行うので、条件指定は行いません。* ＠return 受講生詳細一覧（全件）
   */
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() {
    return service.searchStudentList();
  }

  /**
   * 受講生詳細の検索。 IDに基づく任意の受講生情報取得を行います。
   *
   * @param id 受講生ID ＠return 受講生
   */
  @GetMapping("/student/{id}")
  public StudentDetail getStudent(@PathVariable @NotNull Integer id) {
    StudentDetail student = service.searchStudent(id);
    if (student == null) {
      throw new ResourceNotFoundException("指定されたIDの受講生が見つかりません: " + id);
    }
    return student;
  }

  /**
   * 受講生詳細の登録。
   *
   * @ param studentDetail 受講生詳細
   * @ return　実行結果
   */
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の更新を行います。キャンセルフラグの更新もここで行います（理論削除）
   *
   * @ param studentDetail　受講生詳細
   * @ return　実行結果
   */
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("success updating");
  }
}