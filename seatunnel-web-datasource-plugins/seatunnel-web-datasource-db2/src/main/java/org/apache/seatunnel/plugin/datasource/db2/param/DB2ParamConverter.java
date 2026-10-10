package org.apache.seatunnel.plugin.datasource.db2.param;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.seatunnel.plugin.datasource.api.constants.DataSourceConstants;
import org.apache.seatunnel.plugin.datasource.api.jdbc.JdbcParamConverter;
import org.apache.seatunnel.web.common.utils.JSONUtils;
import org.apache.seatunnel.web.spi.datasource.BaseConnectionParam;
import org.apache.seatunnel.web.spi.enums.DbType;

import java.util.Map;
import java.util.stream.Collectors;

public class DB2ParamConverter implements JdbcParamConverter {

    private static final String DEFAULT_DRIVER = "com.ibm.db2.jcc.DB2Driver";

    @Override
    public BaseConnectionParam createConnectionParams(String connectionJson) {
        DB2ConnectionParam db2ConnectionParam =
                JSONUtils.parseObject(connectionJson, DB2ConnectionParam.class);
        if (db2ConnectionParam == null) {
            throw new IllegalArgumentException("DB2 connection param must not be null");
        }

        db2ConnectionParam.setUrl(buildUrl(db2ConnectionParam));
        db2ConnectionParam.setDbType(DbType.DB2);

        if (StringUtils.isBlank(db2ConnectionParam.getDriver())) {
            db2ConnectionParam.setDriver(DEFAULT_DRIVER);
        }
        return db2ConnectionParam;
    }

    @Override
    public void checkDatasourceParam(BaseConnectionParam baseConnectionParam) {

    }

    private String buildUrl(DB2ConnectionParam connectionParam) {
        String base = String.format("%s%s:%s/%s",
                jdbcPrefix(),
                connectionParam.getHost(),
                connectionParam.getPort(),
                connectionParam.getDatabase());

        Map<String, String> other = connectionParam.getOtherAsMap();
        if (MapUtils.isEmpty(other)) {
            return base;
        }
        return base + ":" + buildQueryString(other)+";";
    }

    private String jdbcPrefix() {
        return DataSourceConstants.JDBC_DB2;
    }

    private String buildQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";"));
    }
}
