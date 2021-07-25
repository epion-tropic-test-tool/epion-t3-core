/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.initialize;

import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.Set;

/**
 * 初期化処理.
 *
 * @author takashno
 */
@Slf4j
public class InitializeEpion {

    private static final String TEMPLATE_SCENERIO_PACKAGE = "com.epion_t3.scenario.template";

    private String outputBasePath = null;

    /**
     * コンストラクタ.
     *
     * @param outputBasePath
     */
    public InitializeEpion(String outputBasePath) {
        this.outputBasePath = outputBasePath;
    }

    /**
     * 実行
     */
    public void execute() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        Set<ResourceInfo> targetResources = null;

        try {
            targetResources = getTopLevelResourcesRecursive(ClassPath.from(loader));
        } catch (IOException e) {
            throw new SystemException(e, CoreMessages.CORE_ERR_0023);
        }
        outputTemplate(targetResources);

        log.info(MessageManager.getInstance().getMessage(CoreMessages.CORE_INF_0001, outputBasePath));

    }

    /**
     * 指定されたクラスパスのJavaクラス以外のリソースを取得.
     *
     * @param classPath
     * @return
     * @throws IOException
     */
    private ImmutableSet<ResourceInfo> getTopLevelResourcesRecursive(ClassPath classPath) throws IOException {
        var packagePrefix = TEMPLATE_SCENERIO_PACKAGE.replaceAll("\\.", "/") + "/";
        ImmutableSet.Builder<ResourceInfo> builder = ImmutableSet.builder();
        for (ResourceInfo resourceInfo : classPath.getResources()) {
            if (resourceInfo.getResourceName().startsWith(packagePrefix)) {
                if (!resourceInfo.getResourceName().endsWith(".class")) {
                    builder.add(resourceInfo);
                }
            }
        }
        return builder.build();
    }

    /**
     * @param resourcesInfoList
     */
    private void outputTemplate(Set<ResourceInfo> resourcesInfoList) {
        var beforeDirName = (String) null;

        var templatePath = TEMPLATE_SCENERIO_PACKAGE.replaceAll("\\.", "/");

        for (ResourceInfo resourceInfo : resourcesInfoList) {
            var resourceName = resourceInfo.getResourceName().replaceAll(templatePath, "");
            var resourceDirName = resourceName.substring(0, resourceName.lastIndexOf("/"));

            if (beforeDirName == null || !beforeDirName.equals(resourceDirName)) {
                beforeDirName = resourceDirName;
                var dir = new File(outputBasePath, resourceDirName);
                dir.mkdirs();
            }
            var resourcePath = outputBasePath + resourceName;
            var file = new File(resourcePath);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.getChannel()
                        .transferFrom(Channels.newChannel(resourceInfo.asByteSource().openStream()), 0, Long.MAX_VALUE);
            } catch (IOException e) {
                throw new SystemException(e, CoreMessages.CORE_ERR_0023);
            }
        }
    }
}
