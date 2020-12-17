package spoon.common.utils;

@SuppressWarnings("JavaDoc")
public abstract class StringUtils {

    private static final String[] ONE_TO_TEN = {"일", "이", "삼", "사", "오", "육", "칠", "팔", "구", "십"};
    private static final String[] TEN_TO_THOUSAND = {"", "십", "백", "천"};
    private static final String[] OVER_THOUSAND = new String[]{"", "만", "억", "조", "경"};

    /**
     * 값이 없는지 확인
     */
    public static boolean empty(String value) {
        return org.springframework.util.StringUtils.isEmpty(value);
    }

    /**
     * 값이 있는지 확인
     */
    public static boolean notEmpty(String value) {
        return !empty(value);
    }

    /**
     * 숫자를 한글로 바꿔 보여준다.
     *
     * @param num
     * @return
     */
    public static String num2han(Object num) {
        if (num == null) return "";
        return num2han(String.valueOf(num));
    }

    /**
     * 숫자를 한글로 바꿔서 보여준다.
     *
     * @param num
     * @return
     */
    public static String num2han(String num) {
        num = num.replaceAll("[^0-9]", "");

        StringBuilder sb = new StringBuilder();

        int pos = num.length() % 4;
        int unit = (pos == 0) ? num.length() / 4 - 1 : num.length() / 4;
        int op = 0;

        for (int i = 0; i < num.length(); i++) {
            if (pos == 0) pos = 4;
            int j = Integer.parseInt(num.substring(i, i + 1));

            if (j != 0) {
                sb.append(" ").append(ONE_TO_TEN[j - 1]);
                sb.append(TEN_TO_THOUSAND[pos - 1]);
                op = 1;
            }

            if (pos == 1) {
                if (op == 1) sb.append(OVER_THOUSAND[unit]);
                unit--;
                op = 0;
            }
            pos--;
        }

        return sb.toString();
    }

    /**
     * 파일 확장자를 제외한 이름을 가져온다.
     */
    public static String getFilename(String filename) {
        return org.springframework.util.StringUtils.getFilename(filename);
    }

    public static int lastNumber(double chart) {
        String num = String.format("%.2f", chart);
        return Integer.parseInt(num.substring(num.length() - 1), 10);
    }

}
