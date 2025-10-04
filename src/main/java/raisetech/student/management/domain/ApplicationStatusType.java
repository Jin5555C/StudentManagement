package raisetech.student.management.domain;

/**
 * コースの申し込み状況を定義するEnum
 * （仮申込、本申込、受講中、受講終了の4種類）
 */
public enum ApplicationStatusType {

    TEMPORARY_APPLICATION("仮申込"),
    FULL_APPLICATION("本申込"),
    IN_PROGRESS("受講中"),
    COMPLETED("受講終了");

    private final String japaneseName;

    ApplicationStatusType(String japaneseName) {
        this.japaneseName = japaneseName;
    }

    /**
     * 日本語名を取得します。
     */
    public String getJapaneseName() {
        return japaneseName;
    }
}