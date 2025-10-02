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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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

  // ✅ Springが管理するObjectMapperを注入する
  @Autowired
  private ObjectMapper objectMapper;

  // 手動でのセットアップは不要になるので @BeforeEach は削除

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
