package spoon.common.utils;

public class ErrorUtils {

    public static String trace(StackTraceElement[] elements) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : elements) {
            if (!e.getClassName().startsWith("spoon")) continue;
            sb.append(System.getProperty("line.separator")).append("클래스: ").append(e.getClassName())
                    .append(", 메소드: ").append(e.getMethodName())
                    .append(", 라인: ").append(e.getLineNumber());
            break;
        }
        return sb.toString();
    }

    public static String traceAll(StackTraceElement[] elements) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : elements) {
            if (!e.getClassName().startsWith("spoon")) continue;
            sb.append(System.getProperty("line.separator")).append("클래스: ").append(e.getClassName())
                    .append(", 메소드: ").append(e.getMethodName())
                    .append(", 라인: ").append(e.getLineNumber());
        }
        return sb.toString();
    }
}
