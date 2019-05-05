package com.epion_t3.core.common.bean;

import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StageType;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ET3Notification implements Serializable {

    /**
     * ステージ.
     */
    @NonNull
    private StageType stage;

    /**
     * 通知レベル.
     */
    @NonNull
    private NotificationType level;

    /**
     * メッセージ.
     */
    private String message;

    /**
     * エラー.
     */
    private Throwable error;
//
//    /**
//     * プライベートコンストラクタ.
//     */
//    protected ET3Notification() {
//        // Do Nothing...
//    }
//
//
//    public ET3Notification stage(StageType stage) {
//        this.stage = stage;
//        return this;
//    }
//
//    public ET3Notification error(Throwable e) {
//        this.error = e;
//        return this;
//    }
//
//    public ET3Notification message(String message) {
//        this.message = message;
//        return this;
//    }
//
//    public ET3Notification message(Messages messageId) {
//        this.message = MessageManager.getInstance().getMessage(messageId);
//        return this;
//    }
//
//    public ET3Notification message(Messages messageId, Object... args) {
//        this.message = MessageManager.getInstance().getMessage(messageId, args);
//        return this;
//    }
//
//
//    public static ET3Notification info(ExecuteContext executeContext) {
//        ET3Notification info = new ET3Notification();
//        info.setStage(executeContext.getStage());
//        info.setLevel(NotificationType.INFO);
//        return info;
//    }
//
//    public static ET3Notification warn(ExecuteContext executeContext, Throwable e) {
//        ET3Notification warn = new ET3Notification();
//        warn.setStage(executeContext.getStage());
//        warn.setError(e);
//        warn.setLevel(NotificationType.WARN);
//        return warn;
//    }
//
//    public static ET3Notification warn(ExecuteContext executeContext) {
//        ET3Notification warn = new ET3Notification();
//        warn.setStage(executeContext.getStage());
//        warn.setLevel(NotificationType.WARN);
//        return warn;
//    }
//
//    public static ET3Notification error(ExecuteContext executeContext, Throwable e) {
//        ET3Notification error = new ET3Notification();
//        error.setStage(executeContext.getStage());
//        error.setError(e);
//        error.setLevel(NotificationType.ERROR);
//        return error;
//    }
//
//    public static ET3Notification error(ExecuteContext executeContext) {
//        ET3Notification error = new ET3Notification();
//        error.setStage(executeContext.getStage());
//        error.setLevel(NotificationType.ERROR);
//        return error;
//    }
//
//    public static ET3Notification fatal(ExecuteContext executeContext, Throwable e) {
//        ET3Notification fatal = new ET3Notification();
//        fatal.setStage(executeContext.getStage());
//        fatal.setError(e);
//        fatal.setLevel(NotificationType.FATAL);
//        return fatal;
//    }
//
//    public static ET3Notification fatal(ExecuteContext executeContext) {
//        ET3Notification fatal = new ET3Notification();
//        fatal.setStage(executeContext.getStage());
//        fatal.setLevel(NotificationType.FATAL);
//        return fatal;
//    }

}
