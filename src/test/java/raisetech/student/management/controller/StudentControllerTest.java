package raisetech.student.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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
    // when/verifyのロジックは同じ
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
}