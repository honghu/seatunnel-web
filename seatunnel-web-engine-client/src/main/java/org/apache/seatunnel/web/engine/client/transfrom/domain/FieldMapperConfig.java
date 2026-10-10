package org.apache.seatunnel.web.engine.client.transfrom.domain;

import lombok.Data;
import org.apache.seatunnel.web.common.enums.ConvertCase;

import java.util.List;

@Data
public class FieldMapperConfig {

    /**
     * Upstream plugin id.
     */
    private String pluginInput;

    /**
     * Downstream plugin id.
     */
    private String pluginOutput;

    /**
     * Field mapping rules from frontend.
     */
    private List<FieldMapperMapping> mappings;

    /**
     * Whether to pass through unmapped fields.
     */
    private Boolean passThroughUnmapped;

    private ConvertCase convertCase;

    private String prefix;
    private String suffix;
}