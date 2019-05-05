package com.epion_t3.core.common.bean.spec;

import lombok.*;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Information implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    @NonNull
    private String name;

    private String customPackage;

    /**
     * 概要.
     */
    private List<Content> summary;

    /**
     * 詳細.
     */
    private List<Content> description;

}
