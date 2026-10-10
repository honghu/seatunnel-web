package org.apache.seatunnel.plugin.datasource.db2.builder;

import com.google.auto.service.AutoService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.seatunnel.plugin.datasource.api.constants.DataSourceConstants;
import org.apache.seatunnel.plugin.datasource.api.hocon.AbstractJdbcBatchBuilder;
import org.apache.seatunnel.plugin.datasource.api.hocon.DataSourceHoconBuilder;
import org.apache.seatunnel.plugin.datasource.api.hocon.HoconBuildContext;
import org.apache.seatunnel.plugin.datasource.api.jdbc.JdbcConfigReaders;

import java.util.HashMap;
import java.util.Map;

import static org.apache.seatunnel.plugin.datasource.api.hocon.JdbcBatchConstants.TABLE;

@AutoService(DataSourceHoconBuilder.class)
public class DB2BatchBuilder extends AbstractJdbcBatchBuilder {

    private static final String POSTGRES_DIALECT = "DB2";

    @Override
    protected String defaultDriver() {
        return DataSourceConstants.COM_DB2_JDBC_DRIVER;
    }

    @Override
    public Config buildSinkHocon(HoconBuildContext context) {



        Config config = super.buildSinkHocon(context);


        Config newConfig = config.withValue("table", ConfigValueFactory.fromAnyRef(true));
        if (config.hasPath("dialect")) {
            return config;
        }

//        String table = JdbcConfigReaders.getString(config, TABLE, "");

        return config.withValue(
                "dialect",
                com.typesafe.config.ConfigValueFactory.fromAnyRef(POSTGRES_DIALECT));
    }

    @Override
    protected String buildTablePath(String database, String schemaName, String table) {
        String schema = StringUtils.isNotBlank(schemaName) ? schemaName : "public";
        return String.format("%s.%s.%s", database, schema, table);
    }

    @Override
    public String pluginName() {
        return "JDBC-DB2";
    }
}
