/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ScenarioManageFileSystem {

    LOCAL_FILESYSTEM("filesystem"),

    S3("aws-s3");

    private final String value;

}
