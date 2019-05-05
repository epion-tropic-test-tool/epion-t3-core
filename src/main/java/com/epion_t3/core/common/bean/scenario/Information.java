package com.epion_t3.core.common.bean.scenario;

import lombok.*;

import java.io.Serializable;


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
    private String id;

    private String version;

    private String summary;

    private String description;

}
