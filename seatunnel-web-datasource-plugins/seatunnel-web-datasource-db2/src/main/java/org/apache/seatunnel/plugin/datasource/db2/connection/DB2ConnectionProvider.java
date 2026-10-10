package org.apache.seatunnel.plugin.datasource.db2.connection;

import org.apache.seatunnel.plugin.datasource.api.constants.DataSourceConstants;
import org.apache.seatunnel.plugin.datasource.api.jdbc.AbstractJdbcConnectionProvider;
import org.apache.seatunnel.plugin.datasource.db2.param.DB2ConnectionParam;

public class DB2ConnectionProvider
        extends AbstractJdbcConnectionProvider<DB2ConnectionParam> {

    @Override
    protected String defaultDriverClass() {
        return DataSourceConstants.COM_DB2_JDBC_DRIVER;
    }

    @Override
    protected String resolveDriverLocation(DB2ConnectionParam t) {
        return defaultBaseUrl() + t.getDriverLocation();
    }
}
