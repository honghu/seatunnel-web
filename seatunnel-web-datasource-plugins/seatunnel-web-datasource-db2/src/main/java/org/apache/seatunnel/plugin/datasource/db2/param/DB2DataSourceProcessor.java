package org.apache.seatunnel.plugin.datasource.db2.param;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.seatunnel.plugin.datasource.api.analysis.JobDefinitionAnalyzer;
import org.apache.seatunnel.plugin.datasource.api.hocon.DataSourceHoconBuilder;
import org.apache.seatunnel.plugin.datasource.api.hocon.DataSourceHoconBuilderFactory;
import org.apache.seatunnel.plugin.datasource.api.jdbc.*;
import org.apache.seatunnel.plugin.datasource.db2.analysis.DB2JobDefinitionAnalyzer;
import org.apache.seatunnel.plugin.datasource.db2.connection.DB2ConnectionProvider;
import org.apache.seatunnel.plugin.datasource.db2.metadata.DB2Catalog;
import org.apache.seatunnel.web.spi.datasource.BaseConnectionParam;
import org.apache.seatunnel.web.spi.enums.DbType;

@AutoService(DataSourceProcessor.class)
@Slf4j
public class DB2DataSourceProcessor extends AbstractDataSourceProcessor {

    private final JdbcConnectionProvider connectionManager = new DB2ConnectionProvider();
    private final JdbcParamConverter paramConverter = new DB2ParamConverter();
    private final JobDefinitionAnalyzer jobDefinitionAnalyzer = new DB2JobDefinitionAnalyzer();

    @Override
    public DataSourceHoconBuilder getQueryBuilder(String pluginName) {
        return DataSourceHoconBuilderFactory.getBuilder(pluginName);
    }

    @Override
    public JdbcConnectionProvider getConnectionManager() {
        return connectionManager;
    }

    @Override
    public JdbcParamConverter getParamConverter() {
        return paramConverter;
    }

    @Override
    public JdbcCatalog getMetadataService(BaseConnectionParam connectionParam) {
        return new DB2Catalog(connectionParam, connectionManager);
    }

    @Override
    public DbType getDbType() {
        return DbType.DB2;
    }

    @Override
    public boolean acceptsURL(String url) {
        return url.startsWith("jdbc:db2:");
    }

    @Override
    public DataSourceProcessor create() {
        return new DB2DataSourceProcessor();
    }

    @Override
    public JobDefinitionAnalyzer getJobDefinitionAnalyzer() {
        return jobDefinitionAnalyzer;
    }

    @Override
    public String connectivityCheckSql() {
        return "SELECT 1 FROM SYSIBM.SYSDUMMY1";
    }
}
