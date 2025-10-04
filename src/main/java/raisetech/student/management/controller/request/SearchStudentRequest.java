package raisetech.student.management.controller.request;

import lombok.Getter;
import lombok.Setter;
import raisetech.student.management.data.Student;

@Getter
@Setter
public class SearchStudentRequest {
    // Studentの検索に使用するフィールドを定義
    private Integer id;
    private String name;
    private String kanaName;
    private String nickname;
    private String email;
    private String area;
    private Integer age;
    private String sex;
    // isDeleted は検索条件として不適切なので省略（必要であれば追加）

    /**
     * リクエストDTOを検索条件として使用するデータオブジェクトに変換するヘルパーメソッド。
     * Service層以下で利用する Student オブジェクトに変換します。
     * @return 検索条件として設定された Student オブジェクト
     */
    public Student toSearchCondition() {
        Student student = new Student();
        student.setId(this.id);
        student.setName(this.name);
        student.setKanaName(this.kanaName);
        student.setNickname(this.nickname);
        student.setEmail(this.email);
        student.setArea(this.area);
        student.setAge(this.age);
        student.setSex(this.sex);
        // isDeletedなど、Requestにないフィールドはデフォルト値のまま
        return student;
    }
}