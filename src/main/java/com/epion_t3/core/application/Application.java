/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.application;

import com.epion_t3.core.application.runner.impl.ApplicationRunnerImpl;
import com.epion_t3.core.common.type.Args;
import com.epion_t3.core.common.type.ExitCode;
import com.epion_t3.core.common.type.InitializeArgs;
import com.epion_t3.core.initialize.InitializeEpion;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * epion-t3のメインアプリケーション.
 *
 * @author takashno
 */
@Slf4j
public final class Application {

    private static final String BASE_PACKAGE = "com.epion_t3";

    private static final Options OPTIONS = new Options();

    private static final Options INITIALIZE_OPTIONS = new Options();

    static {
        Arrays.stream(Args.values()).forEach(x -> {
            if (x.isRequired()) {
                OPTIONS.addRequiredOption(x.getShortName(), x.getLongName(), x.isHasArg(), x.getDescription());
            } else {
                OPTIONS.addOption(x.getShortName(), x.getLongName(), x.isHasArg(), x.getDescription());
            }
        });

        Arrays.stream(InitializeArgs.values()).forEach(x -> {
            if (x.isRequired()) {
                INITIALIZE_OPTIONS.addRequiredOption(x.getShortName(), x.getLongName(), x.isHasArg(),
                        x.getDescription());
            } else {
                INITIALIZE_OPTIONS.addOption(x.getShortName(), x.getLongName(), x.isHasArg(), x.getDescription());
            }
        });
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // バナー出力
        outputBanner();

        MessageManager messageManager = MessageManager.getInstance();

        // バージョンの解決
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        // 初期化オプションがあれば初期化処理を呼び出す
        if (ArrayUtils.contains(args, "-i") || ArrayUtils.contains(args, "--init")) {
            try {
                cmd = parser.parse(INITIALIZE_OPTIONS, args, true);
            } catch (ParseException e) {
                log.error("Error Occurred...", e);
                System.exit(ExitCode.ERROR.getExitCode());
            }
            InitializeEpion ie = new InitializeEpion(
                    cmd.getOptionValue(InitializeArgs.OUTPUT_ROOT_PATH.getShortName()));
            ie.execute();
            System.exit(ExitCode.NORMAL.getExitCode());
        }

        try {
            // この判定は、versionのみ取得できればよいため緩く解析する
            cmd = parser.parse(OPTIONS, args, true);
        } catch (ParseException e) {
            log.error("Error Occurred... See -> https://docs.epion-t3.com/#/pages/use/run", e);
            System.exit(ExitCode.ERROR.getExitCode());
        }

        // XXX: 暫定対処
        // 全てのApplicationRunnerを取得する
        /*
         * ClassLoader loader = Thread.currentThread().getContextClassLoader();
         * Map<String, Class<?>> applicationRunnerClasses = ClassPath.from(loader)
         * .getTopLevelClassesRecursive(BASE_PACKAGE) .stream() // パッケージ名にバージョンを含むこと or
         * base のみ許可する .filter(info -> { String[] packages =
         * info.getPackageName().replace(BASE_PACKAGE + ".", "").split("\\."); return
         * packages[0].matches("v[0-9]+") || "core".equals(packages[0]); }) //
         * ApplicationRunnerを実装していること かつ ApplicationVersionのアノテーションを付与していること
         * .filter(info -> { return
         * ApplicationRunner.class.isAssignableFrom(info.load()) &&
         * info.load().getDeclaredAnnotation(ApplicationVersion.class) != null; })
         * .map(info -> info.load()) .collect(Collectors.toMap(x ->
         * x.getDeclaredAnnotation(ApplicationVersion.class).version(), x -> x));
         */

        // XXX: 暫定対処
        // 指定されたツールバージョン
        // String version = cmd.getOptionValue(Args.VERSION.getShortName());

        // XXX: 暫定対処
        // ツールバージョンに対応したアプリケーション実行クラス
        // onRunnerClass = applicationRunnerClasses.get(version);
        var applicationRunnerClass = ApplicationRunnerImpl.class;

        if (applicationRunnerClass != null) {
            // 存在していればメソッド決め打ちで実行する
            try {
                Method execute = applicationRunnerClass.getDeclaredMethod("execute", String[].class);
                Object applicationInstance = applicationRunnerClass.newInstance();
                int exitCode = (int) execute.invoke(applicationInstance, new Object[] { args });
                System.exit(exitCode);
            } catch (NoSuchMethodException e) {
                log.error(messageManager.getMessage(CoreMessages.CORE_ERR_0001), e);
                System.exit(ExitCode.ERROR.getExitCode());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error(messageManager.getMessage(CoreMessages.CORE_ERR_0001), e);
                System.exit(ExitCode.ERROR.getExitCode());
            }
        } /*
           * else { // 存在していないならエラーとする
           * log.error(messageManager.getMessage(CoreMessages.CORE_ERR_0002, version));
           * System.exit(ExitCode.ERROR.getExitCode()); }
           */

    }

    /**
     * 標準出力に対してバナーを出力する.
     */
    private static void outputBanner() {
        ClassLoader classLoader = Application.class.getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("banners/banner.txt");
                InputStreamReader stream = new InputStreamReader(is, StandardCharsets.UTF_8);) {
            IOUtils.readLines(stream).forEach(System.out::println);
        } catch (IOException e) {
            // ignore
        }

    }
}
