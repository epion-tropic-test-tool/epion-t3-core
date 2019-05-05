package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScenarioManageFileSystem {

    LOCAL_FILESYSTEM("filesystem"),

    S3("aws-s3");

    private String value;

}
