package com.epion_t3.core.common.util;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

/**
 * Thymeleafによるレポートユーティリティ.
 *
 * @author takashno
 */
@Slf4j
public final class ThymeleafReportUtils {

    /**
     * テンプレート接頭辞.
     */
    public static final String TEMPLATE_PREFIX = "/templates/";

    /**
     * テンプレート接尾辞
     */
    public static final String TEMPLATE_SUFFIX = ".html";

    /**
     * テンプレートエンコード.
     */
    public static final String TEMPLATE_ENCODING = "UTF-8";

    /**
     * シングルトンインスタンス.
     */
    private static final ThymeleafReportUtils instance = new ThymeleafReportUtils();

    /**
     * プライベートコンストラクタ.
     */
    private ThymeleafReportUtils() {
        // Do Nothing...
    }

    /**
     * シングルトンインスタンスを取得.
     *
     * @return
     */
    public static ThymeleafReportUtils getInstance() {
        return instance;
    }

    /**
     * エンジン生成.
     *
     * @return テンプレートエンジン
     */
    public TemplateEngine createEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        AbstractConfigurableTemplateResolver tr = new ClassLoaderTemplateResolver();
        tr.setCheckExistence(true);
        tr.setTemplateMode(TemplateMode.HTML);
        tr.setPrefix(TEMPLATE_PREFIX);
        tr.setSuffix(TEMPLATE_SUFFIX);
        templateEngine.setTemplateResolver(tr);
        return templateEngine;
    }

    /**
     * Thymeleafで利用するためのユーティリティを設定する.
     *
     * @param variable 変数
     */
    public static void setUtility(Map<String, Object> variable) {

        // DateTimeUtilsを利用できるように設定
        variable.put("dateTimeUtils", DateTimeUtils.getInstance());
        // YamlUtilsを利用できるように設定
        variable.put("yamlUtils", YamlUtils.getInstance());
        // JsonUtilsを利用できるように設定
        variable.put("jsonUtils", JsonUtils.getInstance());
        // StringUtilsを利用できるように設定
        variable.put("stringUtils", StringUtils.getInstance());

    }

}
