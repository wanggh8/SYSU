package com.wanggh8.puremvvm.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.List;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/12/24
 */
public class PureLogger {

    static boolean IS_LOGGER_TO_FILE = false;
    static boolean IS_LOGGER = false;

    public static void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(3)         // (Optional) How many method line to show. Default 2
                .methodOffset(5)        // (Optional) Skips some method invokes in stack trace. Default 5
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("Logger")   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
                .build();

        com.orhanobut.logger.Logger.addLogAdapter(new PureLogAdapter(formatStrategy));
        saveLogsToFile();
        d("PureLogger", "PureLogger init Success");
    }

    public static void initLogger(boolean isLog, boolean isLogToFile) {
        PureLogger.IS_LOGGER = isLog;
        PureLogger.IS_LOGGER_TO_FILE = isLogToFile;
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(2)         // (Optional) How many method line to show. Default 2
                .methodOffset(3)        // (Optional) Skips some method invokes in stack trace. Default 5
//                .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("Logger")   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
                .build();

        com.orhanobut.logger.Logger.addLogAdapter(new PureLogAdapter(formatStrategy));
        saveLogsToFile();
        d("PureLogger", "PureLogger init Success");
    }

    /**
     * Save logs to the file
     */
    private static void saveLogsToFile() {
        if (!IS_LOGGER_TO_FILE) {
            return;
        }
        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                .tag("LoggerFile")
                .build();
        com.orhanobut.logger.Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));
    }

    public static void w(String tag, String msg) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.t(tag).w(msg);
        }
    }

    public static void d(String tag, String msg) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.t(tag).d(msg);
        }
    }

    public static void d(String msg) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.d(msg);
        }
    }

    /**
     * 打印列表和对象时，对象需继承JSONBean
     * @param object
     */
    public static void d(Object object) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.d(object);
        }
    }


    public static void i(String tag, String msg) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.t(tag).i(msg);
        }
    }

    public static void v(String tag, String msg) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.t(tag).v(msg);
        }
    }

    public static void e(String tag, String msg) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.t(tag).e(msg);
        }
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.e(throwable, message, args);
        }
    }

    public static void json(String json) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.json(json);
        }
    }

    public static void xml(String xml) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.xml(xml);
        }
    }

    public static void json(String tag, String json) {
        if (IS_LOGGER) {
            com.orhanobut.logger.Logger.t(tag).json(json);
        }
    }

}
