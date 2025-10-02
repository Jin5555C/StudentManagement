package raisetech.student.management.controller;

import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import raisetech.student.management.controller.request.SearchStudentRequest;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.domain.ApplicationStatusType;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.exception.ResourceNotFoundException;
import raisetech.student.management.service.StudentService;
import raisetech.student.management.validation.CreateValidationGroup;
import raisetech.student.management.validation.UpdateValidationGroup;

/**
 * 受講生の検索や登録、更新などを行うREST　APIを受け付ける実行されるControllerです。
 */
@RestController
public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }


  /**
   * 受講生検索（ID指定）。
   * RESTful: GET /students/{id}
   * @param id 受講生ID
   * @return 受講生
   */
  @GetMapping("/students/{id}")
  public StudentDetail getStudent(@PathVariable @NotNull Integer id) {
    StudentDetail student = service.searchStudent(id);
    if (student == null) {
      throw new ResourceNotFoundException("指定されたIDの受講生が見つかりません: " + id);
    }
    return student;
  }

  /**
   * 受講生を条件で検索します。
   * RESTful: GET /students?name=...&age=...
   * @param request 検索条件を保持するDTO
   * @return 検索結果の受講生詳細一覧
   */
  @GetMapping("/students")
  public List<StudentDetail> searchStudentList(@ModelAttribute SearchStudentRequest request) {
    return service.searchStudentList(request.toSearchCondition());
  }

  /**
   * 受講生詳細の登録。
   * RESTful: POST /students
   */
  @PostMapping("/students")
  public ResponseEntity<StudentDetail> registerStudent(
          @RequestBody @Validated(CreateValidationGroup.class)
          StudentDetail studentDetail) {
    StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の更新を行います。
   * RESTful: PUT /students
   */
  @PutMapping("/students")
  public ResponseEntity<String> updateStudent(
          @RequestBody @Validated(UpdateValidationGroup.class)
          StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("success updating");
  }


  /**
   * 申し込み状況の選択肢一覧を取得します。
   * RESTful: GET /application-statuses/options
   * @return 申し込み状況の日本語名リスト
   */
  @GetMapping("/application-statuses/options")
  public List<String> getApplicationStatusOptions() {
    return Arrays.stream(ApplicationStatusType.values())
            .map(ApplicationStatusType::getJapaneseName)
            .collect(Collectors.toList());
  }

  /**
   * 全てのコースの申し込み状況一覧を取得します。
   * RESTful: GET /application-statuses
   * @return 申し込み状況一覧
   */
  @GetMapping("/application-statuses")
  public List<ApplicationStatus> getApplicationStatusList() {
    return service.searchApplicationStatusList();
  }

  /**
   * IDに紐づくコースの申し込み状況を取得します。
   * RESTful: GET /application-statuses/{id}
   * @param id 申し込み状況ID
   * @return 申し込み状況
   */
  @GetMapping("/application-statuses/{id}")
  public ApplicationStatus getApplicationStatus(@PathVariable @NotNull Integer id) {
    return service.searchApplicationStatus(id);
  }
}