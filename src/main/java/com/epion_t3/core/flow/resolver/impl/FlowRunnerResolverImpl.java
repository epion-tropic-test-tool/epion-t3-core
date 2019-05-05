package com.epion_t3.core.flow.resolver.impl;

import com.epion_t3.core.flow.resolver.FlowRunnerResolver;
import com.epion_t3.core.common.bean.FlowInfo;
import com.epion_t3.core.exception.FlowNotFoundException;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.flow.runner.FlowRunner;
import com.epion_t3.core.custom.holder.CustomPackageHolder;
import com.epion_t3.core.message.impl.CoreMessages;
import org.apache.commons.lang3.StringUtils;

/**
 * @author takashno
 */
public final class FlowRunnerResolverImpl implements FlowRunnerResolver {

    /**
     * インスタンス.
     */
    private static final FlowRunnerResolverImpl instance = new FlowRunnerResolverImpl();

    /**
     * プライベートコンストラクタ.
     */
    private FlowRunnerResolverImpl() {
        // Do Nothing...
    }

    /**
     * インスタンス取得.
     *
     * @return
     */
    public static FlowRunnerResolverImpl getInstance() {
        return instance;
    }

    /**
     * {@inheritDoc}
     *
     * @param type
     * @return
     */
    @Override
    public FlowRunner getFlowRunner(String type) {

        if (StringUtils.isEmpty(type)) {
            // 不正
            throw new SystemException(CoreMessages.CORE_ERR_0001);
        }

        FlowInfo flowInfo = CustomPackageHolder.getInstance().getCustomFlowInfo(type);

        if (flowInfo == null) {
            // コマンド解決が出来ない場合
            throw new FlowNotFoundException(type);
        }

        // 実行クラスを取得
        Class runnerClass = flowInfo.getRunner();

        if (runnerClass == null) {
            // クラスが設定されていない場合（コンパイルが通らないレベルのため通常発生しない）
            throw new SystemException(CoreMessages.CORE_ERR_0001);
        }

        try {
            // インスタンス生成＋返却
            return FlowRunner.class.cast(runnerClass.newInstance());
        } catch (Exception e) {
            throw new SystemException(e, CoreMessages.CORE_ERR_0001);
        }
    }
}
