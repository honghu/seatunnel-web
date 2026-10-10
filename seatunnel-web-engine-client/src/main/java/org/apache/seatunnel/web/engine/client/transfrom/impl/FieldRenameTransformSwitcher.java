package org.apache.seatunnel.web.engine.client.transfrom.impl;

import com.google.auto.service.AutoService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.seatunnel.web.common.enums.ConvertCase;
import org.apache.seatunnel.web.engine.client.transfrom.TransformConfigSwitcher;
import org.apache.seatunnel.web.engine.client.transfrom.domain.FieldRenameTransformOptions;
import org.apache.seatunnel.web.engine.client.transfrom.domain.SQLTransformOptions;
import org.apache.seatunnel.web.engine.client.transfrom.domain.Transform;
import org.apache.seatunnel.web.engine.client.transfrom.domain.TransformOptions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SQL Transform configuration switcher.
 */
@Slf4j
@AutoService(TransformConfigSwitcher.class)
public class FieldRenameTransformSwitcher implements TransformConfigSwitcher {

    @Override
    public Transform getTransform() {
        return Transform.FIELDRENAME;
    }

    @Override
    public Config transform(TransformOptions options) {
        if (!(options instanceof FieldRenameTransformOptions)) {
            throw new IllegalArgumentException(
                    "Invalid TransformOptions type for FieldRename: "
                            + (options == null
                            ? "null"
                            : options.getClass().getName())
            );
        }

        FieldRenameTransformOptions fieldRenameTransformOptions = (FieldRenameTransformOptions) options;

        validate(fieldRenameTransformOptions);

        String pluginInput = fieldRenameTransformOptions.getEffectivePluginInput();
        String pluginOutput = fieldRenameTransformOptions.getEffectivePluginOutput();





        Map<String, Object> fieldRenameConfig = new LinkedHashMap<>();
        fieldRenameConfig.put("plugin_input", pluginInput);
        fieldRenameConfig.put("plugin_output", pluginOutput);


        ConvertCase convertCase = fieldRenameTransformOptions.getConfig().getConvertCase();
        if (convertCase!=null) {
            fieldRenameConfig.put("convert_case", convertCase.toString());
        }
        String prefix = fieldRenameTransformOptions.getConfig().getPrefix();
        if (StringUtils.isNotBlank(prefix)) {
            fieldRenameConfig.put("prefix", prefix);
        }

        String suffix = fieldRenameTransformOptions.getConfig().getSuffix();
        if (StringUtils.isNotBlank(suffix)) {
            fieldRenameConfig.put("suffix", suffix);
        }



        Map<String, Object> root = new LinkedHashMap<>();
        root.put("FieldRename", fieldRenameConfig);

        Config config = ConfigFactory.parseMap(root);

        log.info(
                "Generating FieldRename transform config, plugin_input={}, plugin_output={}",
                pluginInput,
                pluginOutput
        );

        log.debug(
                "Generated FieldRename transform config: {}",
                config.root().render()
        );

        return config;
    }

    private void validate(FieldRenameTransformOptions options) {
        String pluginInput = options.getEffectivePluginInput();
        String pluginOutput = options.getEffectivePluginOutput();

        if (StringUtils.isBlank(pluginInput)) {
            throw new IllegalArgumentException(
                    "SQL transform plugin_input must not be empty"
            );
        }

        if (StringUtils.isBlank(pluginOutput)) {
            throw new IllegalArgumentException(
                    "SQL transform plugin_output must not be empty"
            );
        }

    }
}