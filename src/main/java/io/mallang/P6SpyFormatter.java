package io.mallang;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;

@Configuration
public class P6SpyFormatter implements MessageFormattingStrategy {

    private static final String BASE_PACKAGE = "io.mallang";
    private static final Formatter SQL_FORMATTER = new BasicFormatterImpl();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String formatMessage(
            int connectionId,
            String now,
            long elapsed,
            String category,
            String prepared,
            String sql,
            String url
    ) {
        StringBuilder sb = new StringBuilder();

        String callStack = buildCallStack();

        sb.append("\n+==============================================================");
        sb.append("\n|  실행시각    ").append(LocalDateTime.now().format(DATE_FORMATTER));
        sb.append("\n|  실행시간    ").append(elapsed).append("ms");
        sb.append("\n|  커넥션 ID   ").append(connectionId);
        sb.append("\n|  카테고리    ").append(category);

        if (!callStack.isBlank()) {
            sb.append("\n+--------------------------------------------------------------");
            sb.append("\n|  호출 위치");
            sb.append(callStack);
        }

        sb.append("\n+--------------------------------------------------------------");
        sb.append("\n|  실행 SQL");
        sb.append("\n|    ");
        sb.append(SQL_FORMATTER.format(sql));
        sb.append("\n|");
        sb.append("\n+==============================================================\n");

        return sb.toString();
    }

    private String buildCallStack() {
        StackTraceElement[] trace = new Throwable().getStackTrace();
        Deque<String> stack = new ArrayDeque<>();

        for (StackTraceElement el : trace) {
            String s = el.toString();
            if (s.startsWith(BASE_PACKAGE) && !s.contains("P6SpyFormatter")) {
                stack.push(s);
            }
        }

        if (stack.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        int order = 1;
        while (!stack.isEmpty()) {
            sb.append("\n|     ")
              .append(order++)
              .append(". ")
              .append(stack.pop());
        }
        return sb.toString();
    }
}
