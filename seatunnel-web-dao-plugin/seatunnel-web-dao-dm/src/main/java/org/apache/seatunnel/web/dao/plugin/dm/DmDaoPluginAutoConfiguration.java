package org.apache.seatunnel.web.dao.plugin.dm;

import com.baomidou.mybatisplus.annotation.DbType;
import org.apache.seatunnel.web.dao.plugin.api.DaoPluginConfiguration;
import org.apache.seatunnel.web.dao.plugin.api.dialect.DatabaseDialect;
import org.apache.seatunnel.web.dao.plugin.api.monitor.DatabaseMonitor;
import org.apache.seatunnel.web.dao.plugin.dm.monitor.DmMonitor;
import org.apache.seatunnel.web.dao.plugin.dm.dialect.DmDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "seatunnel.web.database", name = "type", havingValue = "dm")
public class DmDaoPluginAutoConfiguration implements DaoPluginConfiguration {

    private final DatabaseMonitor databaseMonitor;
    private final DatabaseDialect databaseDialect;

    public DmDaoPluginAutoConfiguration(DataSource dataSource) {
        this.databaseMonitor = new DmMonitor(dataSource);
        this.databaseDialect = new DmDialect(dataSource);
    }

    @Override
    public DbType dbType() {
        return DbType.DM;
    }

    @Override
    public DatabaseMonitor databaseMonitor() {
        return databaseMonitor;
    }

    @Override
    public DatabaseDialect databaseDialect() {
        return databaseDialect;
    }
}
