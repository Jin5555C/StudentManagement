package raisetech.student.management.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestDataFactory.createApplicationStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;


@WebMvcTest(StudentController.class)
class StudentControllerTest {

  // Springが自動設定したMockMvcを注入する
  @Autowired
  private MockMvc mockMvc;

  // SpringのコンテキストにServiceの「モック」を登録する
  // これでControllerに自動で注入される
  @MockitoBean
  private StudentService service;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void getStudentList_shouldReturnEmptyList() throws Exception {
    when(service.searchStudentList()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/studentList")) // MockMvcRequestBuildersはstatic importすると綺麗
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).searchStudentList();
  }

  @Test
  void getStudent_shouldReturnStudentDetail() throws Exception {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(null);
    studentDetail.setStudentCourseList(Collections.emptyList());

    when(service.searchStudent(1)).thenReturn(studentDetail);

    mockMvc.perform(get("/student/1"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(studentDetail)));

    verify(service, times(1)).searchStudent(1);
  }

  @Test
  void getStudent_shouldReturn404WhenNotFound() throws Exception {
    when(service.searchStudent(99)).thenReturn(null);

    mockMvc.perform(get("/student/99"))
        .andExpect(status().isNotFound());

    verify(service, times(1)).searchStudent(99);
  }

  @Test
  void registerStudent_shouldReturnRegisteredStudent() throws Exception {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(null);
    studentDetail.setStudentCourseList(Collections.emptyList());

    when(service.registerStudent(any(StudentDetail.class))).thenReturn(studentDetail);

    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(studentDetail)));

    verify(service, times(1)).registerStudent(any(StudentDetail.class));
  }


  @Test
  void updateStudent_shouldReturnSuccessMessage() throws Exception {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(null);
    studentDetail.setStudentCourseList(Collections.emptyList());

    mockMvc.perform(put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isOk())
        .andExpect(content().string("success updating"));

    verify(service, times(1)).updateStudent(any(StudentDetail.class));
  }

  @Test
    //  受講生を条件で検索し、結果が返されること
  void searchStudentList_shouldReturnStudentDetailsByCondition() throws Exception {
    StudentDetail detail1 = new StudentDetail();
    List<StudentDetail> expectedDetails = List.of(detail1);
    when(service.searchStudentList(any(Student.class))).thenReturn(expectedDetails);

    mockMvc.perform(get("/student/search")
            .param("name", "テスト")
            .param("area", "東京"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedDetails)));

    verify(service, times(1)).searchStudentList(any(Student.class));
  }

  @Test
    //  受講生を条件で検索し、結果が0件の場合に空のリストが返されること
  void searchStudentList_shouldReturnEmptyListWhenNoMatches() throws Exception {
    when(service.searchStudentList(any(Student.class))).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/student/search")
            .param("name", "存在しない名前"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).searchStudentList(any(Student.class));
  }

  // 申し込み状況の選択肢一覧を取得する
  @Test
  void getApplicationStatusOptions_shouldReturnAllStatusNames() throws Exception {
    // Act & Assert
    // ControllerがEnumを直接利用する場合、Serviceのモックは不要。
    mockMvc.perform(get("/applicationStatuses/options"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", is(4)))
            .andExpect(jsonPath("$[0]", is("仮申込")))
            .andExpect(jsonPath("$[1]", is("本申込")))
            .andExpect(jsonPath("$[2]", is("受講中")))
            .andExpect(jsonPath("$[3]", is("受講終了")));

    verify(service, times(0)).searchStudentList();
  }

  // 申し込み状況の全件リストを取得する
  @Test
  void getApplicationStatusList_shouldReturnStatusesFromService() throws Exception {
    // Arrange
    ApplicationStatus as1 = createApplicationStatus(1, "仮申込");
    ApplicationStatus as2 = createApplicationStatus(2, "本申込");

    List<ApplicationStatus> expectedList = List.of(as1, as2);
    when(service.searchApplicationStatusList()).thenReturn(expectedList);

    mockMvc.perform(get("/applicationStatuses/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(2)))
            .andExpect(jsonPath("$[0].status", is("仮申込")))
            .andExpect(jsonPath("$[1].status", is("本申込")));

    verify(service, times(1)).searchApplicationStatusList();
  }

  // IDを指定して申し込み状況を取得するAPIのテスト
  @Test
  void getApplicationStatus_shouldReturnSingleStatus_whenFound() throws Exception {
    Integer targetId = 1;
    ApplicationStatus as = createApplicationStatus(targetId, "受講中");

    when(service.searchApplicationStatus(targetId)).thenReturn(as);

    mockMvc.perform(get("/applicationStatuses/{id}", targetId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(targetId)))
            .andExpect(jsonPath("$.status", is("受講中")));

    verify(service, times(1)).searchApplicationStatus(targetId);
  }

  // ++++++++++++++ ここからバリデーションのテスト ++++++++++++++

  @Test
  void registerStudent_shouldReturnBadRequest_whenIdIsProvided() throws Exception {
    // Arrange: IDを含む受講生オブジェクトを作成
    Student student = new Student();
    student.setId(99); // @Null(groups = CreateValidationGroup.class) に違反
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setEmail("test@example.com");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(Collections.emptyList());

    // Act & Assert
    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isBadRequest()) // バリデーションエラーなので 400 Bad Request を期待
        .andExpect(jsonPath("$.details[0].field", is("student.id")))
        .andExpect(jsonPath("$.details[0].message", is("IDは指定できません。")));
    // Verify: バリデーションで弾かれるので、Serviceのメソッドは呼ばれないはず
    verify(service, times(0)).registerStudent(any(StudentDetail.class));
  }

  @Test
  void registerStudent_shouldReturnBadRequest_whenNameIsNull() throws Exception {
    // Arrange: 必須項目であるnameがnullの受講生オブジェクトを作成
    Student student = new Student();
    student.setId(null);
    student.setName(null); // @NotBlank(groups = CreateValidationGroup.class) に違反
    student.setKanaName("テストタロウ");
    student.setEmail("test@example.com");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(Collections.emptyList());

    // Act & Assert
    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details[0].field", is("student.name")))
        .andExpect(jsonPath("$.details[0].message", is("入力必須の項目です。入力して下さい")));

    // Verify
    verify(service, times(0)).registerStudent(any(StudentDetail.class));
  }

  @Test
  void updateStudent_shouldReturnBadRequest_whenIdIsNull() throws Exception {
    // Arrange: IDがnullの受講生オブジェクトを作成
    Student student = new Student();
    student.setId(null); // @NotNull(groups = UpdateValidationGroup.class) に違反
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setEmail("test@example.com");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(Collections.emptyList());

    // Act & Assert
    mockMvc.perform(put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode", is("ERR-400")))
        .andExpect(jsonPath("$.details[0].field", is("student.id")))
        .andExpect(jsonPath("$.details[0].message", is("IDを指定してください。")));

    // Verify: Serviceのメソッドは呼ばれない
    verify(service, times(0)).updateStudent(any(StudentDetail.class));
  }

  @Test
  void updateStudent_shouldSucceed_whenRequestIsValid() throws Exception {
    // Arrange: バリデーションを通過する正しい受講生オブジェクトを作成
    Student student = new Student();
    student.setId(1); // 更新なのでIDは必須
    student.setName("更新太郎");
    student.setKanaName("コウシンタロウ");
    student.setEmail("update@example.com");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(Collections.emptyList());

    // Act & Assert
    mockMvc.perform(put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isOk()); // 成功するので 200 OK を期待

    // Verify: バリデーションを通過するので、Serviceのメソッドが1回呼ばれるはず
    verify(service, times(1)).updateStudent(any(StudentDetail.class));
  }
}
