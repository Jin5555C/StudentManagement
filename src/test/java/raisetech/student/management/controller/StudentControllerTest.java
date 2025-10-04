package raisetech.student.management.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private StudentService service;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
    // GET /students: 検索条件なしの場合、空のリストが返されること
  void getStudentList_shouldReturnEmptyList() throws Exception {
    when(service.searchStudentList(any(Student.class))).thenReturn(Collections.emptyList());

    // クエリパラメータを何もつけずに /students を実行
    mockMvc.perform(get("/students"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));

    verify(service, times(1)).searchStudentList(any(Student.class));
  }

  @Test
    // GET /students/{id}: ID指定で受講生詳細が返されること
  void getStudent_shouldReturnStudentDetail() throws Exception {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(null);
    studentDetail.setStudentCourseList(Collections.emptyList());

    when(service.searchStudent(1)).thenReturn(studentDetail);

    mockMvc.perform(get("/students/1"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(studentDetail)));

    verify(service, times(1)).searchStudent(1);
  }

  @Test
    // GET /students/{id}: 存在しないIDの場合、404 Not Foundが返されること
  void getStudent_shouldReturn404WhenNotFound() throws Exception {
    when(service.searchStudent(99)).thenReturn(null);

    mockMvc.perform(get("/students/99"))
            .andExpect(status().isNotFound());

    verify(service, times(1)).searchStudent(99);
  }

  @Test
  // GET /students?name=...&area=...: クエリパラメータによる条件検索の結果が返されること
  void searchStudentList_shouldReturnStudentDetailsByCondition() throws Exception {
    StudentDetail detail1 = new StudentDetail();
    List<StudentDetail> expectedDetails = List.of(detail1);
    when(service.searchStudentList(any(Student.class))).thenReturn(expectedDetails);

    mockMvc.perform(get("/students")
                    .param("name", "テスト")
                    .param("area", "東京"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(expectedDetails)));

    verify(service, times(1)).searchStudentList(any(Student.class));
  }

  @Test
  // GET /students?name=...: 条件に一致する受講生がいない場合、空のリストが返されること
  void searchStudentList_shouldReturnEmptyListWhenNoMatches() throws Exception {
    when(service.searchStudentList(any(Student.class))).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/students")
                    .param("name", "存在しない名前"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));

    verify(service, times(1)).searchStudentList(any(Student.class));
  }

  @Test
    // POST /students: 有効なデータで受講生を新規登録し、登録された情報が返されること
  void registerStudent_shouldReturnRegisteredStudent() throws Exception {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(null);
    studentDetail.setStudentCourseList(Collections.emptyList());

    when(service.registerStudent(any(StudentDetail.class))).thenReturn(studentDetail);

    mockMvc.perform(post("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(studentDetail)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(studentDetail)));

    verify(service, times(1)).registerStudent(any(StudentDetail.class));
  }


  @Test
    // PUT /students: 有効なデータで受講生情報を更新し、成功メッセージが返されること
  void updateStudent_shouldReturnSuccessMessage() throws Exception {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(null);
    studentDetail.setStudentCourseList(Collections.emptyList());

    mockMvc.perform(put("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(studentDetail)))
            .andExpect(status().isOk())
            .andExpect(content().string("success updating"));

    verify(service, times(1)).updateStudent(any(StudentDetail.class));
  }

  @Test
  // GET /application-statuses/options: 申し込み状況の選択肢一覧（日本語名）が返されること
  void getApplicationStatusOptions_shouldReturnAllStatusNames() throws Exception {
    mockMvc.perform(get("/application-statuses/options"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", is(4)))
            .andExpect(jsonPath("$[0]", is("仮申込")))
            .andExpect(jsonPath("$[1]", is("本申込")))
            .andExpect(jsonPath("$[2]", is("受講中")))
            .andExpect(jsonPath("$[3]", is("受講終了")));

    // 選択肢の取得はServiceを介さないため、ServiceのsearchStudentListは呼ばれない
    verify(service, never()).searchStudentList(any(Student.class));
  }

  @Test
  // GET /application-statuses: 申し込み状況の全件リストが返されること
  void getApplicationStatusList_shouldReturnStatusesFromService() throws Exception {
    // Arrange
    ApplicationStatus as1 = createApplicationStatus(1, "仮申込");
    ApplicationStatus as2 = createApplicationStatus(2, "本申込");

    List<ApplicationStatus> expectedList = List.of(as1, as2);
    when(service.searchApplicationStatusList()).thenReturn(expectedList);

    mockMvc.perform(get("/application-statuses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(2)))
            .andExpect(jsonPath("$[0].status", is("仮申込")))
            .andExpect(jsonPath("$[1].status", is("本申込")));

    verify(service, times(1)).searchApplicationStatusList();
  }

  @Test
  // GET /application-statuses/{id}: ID指定で申し込み状況が返されること
  void getApplicationStatus_shouldReturnSingleStatus_whenFound() throws Exception {
    Integer targetId = 1;
    ApplicationStatus as = createApplicationStatus(targetId, "受講中");

    when(service.searchApplicationStatus(targetId)).thenReturn(as);

    mockMvc.perform(get("/application-statuses/{id}", targetId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(targetId)))
            .andExpect(jsonPath("$.status", is("受講中")));

    verify(service, times(1)).searchApplicationStatus(targetId);
  }

  // ++++++++++++++ ここからバリデーションのテスト ++++++++++++++

  @Test
    // POST /students: 新規登録時、リクエストボディにIDが含まれている場合、400 Bad Requestが返されること
  void registerStudent_shouldReturnBadRequest_whenIdIsProvided() throws Exception {
    Student student = new Student();
    student.setId(99);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setEmail("test@example.com");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(Collections.emptyList());

    mockMvc.perform(post("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(studentDetail)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0].field", is("student.id")))
            .andExpect(jsonPath("$.details[0].message", is("IDは指定できません。")));
    // Verify: バリデーションで弾かれるので、Serviceのメソッドは呼ばれないはず
    verify(service, times(0)).registerStudent(any(StudentDetail.class));
  }

  @Test
    // POST /students: 新規登録時、名前(name)がnullの場合、400 Bad Requestが返されること
  void registerStudent_shouldReturnBadRequest_whenNameIsNull() throws Exception {
    // Arrange: 必須項目であるnameがnullの受講生オブジェクトを作成
    Student student = new Student();
    student.setId(null);
    student.setName(null);
    student.setKanaName("テストタロウ");
    student.setEmail("test@example.com");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(Collections.emptyList());

    mockMvc.perform(post("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(studentDetail)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0].field", is("student.name")))
            .andExpect(jsonPath("$.details[0].message", is("入力必須の項目です。入力して下さい")));

    verify(service, times(0)).registerStudent(any(StudentDetail.class));
  }

  @Test
    // PUT /students: 更新時、IDがnullの場合、400 Bad Requestが返されること
  void updateStudent_shouldReturnBadRequest_whenIdIsNull() throws Exception {
    // Arrange: IDがnullの受講生オブジェクトを作成
    Student student = new Student();
    student.setId(null);
    student.setName("テスト太郎");
    student.setKanaName("テストタロウ");
    student.setEmail("test@example.com");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(Collections.emptyList());

    mockMvc.perform(put("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(studentDetail)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is("ERR-400")))
            .andExpect(jsonPath("$.details[0].field", is("student.id")))
            .andExpect(jsonPath("$.details[0].message", is("IDを指定してください。")));

    verify(service, times(0)).updateStudent(any(StudentDetail.class));
  }

  @Test
    // PUT /students: 更新時、有効なリクエストであれば更新が成功し、200 OKが返されること
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

    mockMvc.perform(put("/students")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(studentDetail)))
            .andExpect(status().isOk());

    verify(service, times(1)).updateStudent(any(StudentDetail.class));
  }
}