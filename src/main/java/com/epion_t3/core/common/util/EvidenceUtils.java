/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.util;

import com.epion_t3.core.common.bean.EvidenceInfo;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.FileEvidenceInfo;
import com.epion_t3.core.common.bean.ObjectEvidenceInfo;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.FlowScopeVariables;
import com.epion_t3.core.common.type.ScenarioScopeVariables;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.LinkedList;

public final class EvidenceUtils {

    /**
     * 日時フォーマッター.
     */
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("uuuuMMddHHmmss")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * シングルトンインスタンス.
     */
    private static final EvidenceUtils instance = new EvidenceUtils();

    /**
     * プライベートコンストラクタ.
     */
    private EvidenceUtils() {
    }

    /**
     * シングルトンインスタンスを取得.
     *
     * @return シングルトンインスタンス
     */
    public static EvidenceUtils getInstance() {
        return instance;
    }

    /**
     * オブジェクトエビデンスを参照.
     *
     * @param executeContext
     * @param executeScenario
     * @param flowId
     * @param <O>
     * @return
     */
    public <O extends Serializable> O referObjectEvidence(ExecuteContext executeContext,
            ExecuteScenario executeScenario, String flowId) {
        if (executeScenario.getFlowId2EvidenceId().containsKey(flowId)) {
            String evidenceId = executeScenario.getFlowId2EvidenceId().get(flowId).getLast();
            EvidenceInfo evidenceInfo = executeScenario.getEvidences().get(evidenceId);
            if (evidenceInfo != null && ObjectEvidenceInfo.class.isAssignableFrom(evidenceInfo.getClass())) {
                ObjectEvidenceInfo objectEvidenceInfo = ObjectEvidenceInfo.class.cast(evidenceInfo);
                O object = (O) objectEvidenceInfo.getObject();
                return SerializationUtils.clone(object);
            } else {
                throw new SystemException(CoreMessages.CORE_ERR_0007, flowId);
            }
        } else {
            throw new SystemException(CoreMessages.CORE_ERR_0007, flowId);
        }
    }

    /**
     * オブジェクトエビデンスを登録.
     *
     * @param executeContext
     * @param executeScenario
     * @param executeFlow
     * @param evidence
     */
    public void registrationObjectEvidence(ExecuteContext executeContext, ExecuteScenario executeScenario,
            ExecuteFlow executeFlow, Object evidence) {
        ObjectEvidenceInfo evidenceInfo = new ObjectEvidenceInfo();
        // Full Query Scenario Name として現在実行シナリオ名を設定
        evidenceInfo.setFqsn(executeScenario.getScenarioVariables()
                .get(ScenarioScopeVariables.CURRENT_SCENARIO.getName())
                .toString());
        // Full Query Flow Name として現在実行Flow名を設定
        evidenceInfo.setFqfn(executeFlow.getFlowVariables().get(FlowScopeVariables.CURRENT_FLOW.getName()).toString());
        evidenceInfo.setName(getEvidenceBaseName(executeFlow, FlowScopeVariables.CURRENT_COMMAND.getName()).toString());
        evidenceInfo.setExecuteFlowId(executeFlow.getExecuteId().toString());
        evidenceInfo.setObject(evidence);
        String evidenceId = getEvidenceBaseName(executeFlow, FlowScopeVariables.CURRENT_COMMAND.getName()).toString();
        executeScenario.getEvidences().put(evidenceId, evidenceInfo);
        if (executeScenario.getFlowId2EvidenceId().containsKey(executeFlow.getFlow().getId())) {
            executeScenario.getFlowId2EvidenceId().get(executeFlow.getFlow().getId()).add(evidenceId);
        } else {
            executeScenario.getFlowId2EvidenceId().put(executeFlow.getFlow().getId(), new LinkedList<>());
            executeScenario.getFlowId2EvidenceId().get(executeFlow.getFlow().getId()).add(evidenceId);
        }
    }

    /**
     * @param executeFlow
     * @return
     */
    public String getEvidenceBaseName(ExecuteFlow executeFlow, String baseName) {
        return DTF.format(LocalDateTime.now()) + "_" + executeFlow.getFlow().getId() + "_" + baseName;
    }

    /**
     * FlowIDをもとにファイルエビデンスを参照.
     *
     * @param executeScenario
     * @param flowId
     * @return
     */
    public Path referFileEvidence(ExecuteScenario executeScenario, String flowId) {
        if (executeScenario.getFlowId2EvidenceId().containsKey(flowId)) {
            String evidenceId = executeScenario.getFlowId2EvidenceId().get(flowId).getLast();
            EvidenceInfo evidenceInfo = executeScenario.getEvidences().get(evidenceId);
            if (evidenceInfo != null && FileEvidenceInfo.class.isAssignableFrom(evidenceInfo.getClass())) {
                FileEvidenceInfo objectEvidenceInfo = FileEvidenceInfo.class.cast(evidenceInfo);
                return objectEvidenceInfo.getPath();
            } else {
                throw new SystemException(CoreMessages.CORE_ERR_0008, flowId);
            }
        } else {
            throw new SystemException(CoreMessages.CORE_ERR_0008, flowId);
        }
    }

    /**
     * @param executeScenario
     * @param executeFlow
     * @param evidence
     */
    public void registrationFileEvidence(ExecuteScenario executeScenario, ExecuteFlow executeFlow, Path evidence) {
        FileEvidenceInfo evidenceInfo = new FileEvidenceInfo();
        evidenceInfo.setFqsn(executeScenario.getScenarioVariables()
                .get(ScenarioScopeVariables.CURRENT_SCENARIO.getName())
                .toString());
        evidenceInfo.setFqfn(executeFlow.getFlowVariables().get(FlowScopeVariables.CURRENT_FLOW.getName()).toString());
        evidenceInfo.setName(evidence.getFileName().toString());
        evidenceInfo.setExecuteFlowId(executeFlow.getExecuteId().toString());
        evidenceInfo.setPath(evidence);
        evidenceInfo.setRelativePath("."
                + evidence.toString().replace(executeScenario.getResultPath().toString(), "").replaceAll("\\\\", "/"));
        String evidenceId = getEvidenceBaseName(executeFlow, evidence.getFileName().toString());
        executeScenario.getEvidences()
                .put(getEvidenceBaseName(executeFlow, evidence.getFileName().toString()), evidenceInfo);
        if (executeScenario.getFlowId2EvidenceId().containsKey(executeFlow.getFlow().getId())) {
            executeScenario.getFlowId2EvidenceId().get(executeFlow.getFlow().getId()).add(evidenceId);
        } else {
            executeScenario.getFlowId2EvidenceId().put(executeFlow.getFlow().getId(), new LinkedList<>());
            executeScenario.getFlowId2EvidenceId().get(executeFlow.getFlow().getId()).add(evidenceId);
        }
    }

}
