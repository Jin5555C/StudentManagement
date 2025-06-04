package raisetech.student.management;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

	private Map<String, String> student = new HashMap<>();

	public Application() {
		student.put("name", "Enami Kouji");
		student.put("age", "37");
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	// 現在の学生情報を取得
	@GetMapping("/studentInfo")
	public String getStudentInfo() {
		return student.get("name") + " " + student.get("age") + "歳";
	}

	// 学生情報の設定
	@PostMapping("/studentInfo")
	public void setStudentInfo(@RequestParam String name, @RequestParam String age) {
		student.put("name", name);
		student.put("age", age);
	}

	// 名前を更新する
	@PostMapping("/updateStudentName")
	public void updateStudentName(@RequestParam String name) {
		student.put("name", name);
	}

	// 学生情報の一覧を取得
	@GetMapping("/list")
	public Map<String, String> getStudentList() {
		return student;
	}
}
