package org.apache.seatunnel.plugin.datasource.db2.metadata;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.seatunnel.plugin.datasource.api.jdbc.AbstractJdbcCatalog;
import org.apache.seatunnel.plugin.datasource.api.jdbc.JdbcConnectionProvider;
import org.apache.seatunnel.plugin.datasource.api.jdbc.TablePath;
import org.apache.seatunnel.plugin.datasource.api.modal.DataSourceTableColumn;
import org.apache.seatunnel.web.spi.datasource.BaseConnectionParam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DB2Catalog extends AbstractJdbcCatalog {

    private static final String SELECT_COLUMNS_SQL_TEMPLATE =
            "SELECT * FROM SYSCAT.COLUMNS WHERE TABSCHEMA = '%s' AND TABNAME = '%s' ORDER BY COLNO ASC;\n";

    private static final String SELECT_SPECIFIED_COLUMNS_SQL_TEMPLATE =
            "SELECT * FROM SYSCAT.COLUMNS WHERE TABSCHEMA = '%s' AND TABNAME = '%s' AND COLNAME IN ('%s') ORDER BY COLNO ASC";

    private final String schemaName;

    public DB2Catalog(BaseConnectionParam param, JdbcConnectionProvider connectionManager) {
        super(param, connectionManager);
        this.schemaName = StringUtils.defaultIfBlank(param.getSchemaName(), "public");
    }

    @Override
    protected String applyLimit(String sql, int limit) {
        return sql + " LIMIT " + limit;
    }

    @Override
    protected String getTableName(ResultSet rs) throws SQLException {
        return rs.getString(1);
    }

    @Override
    protected String getListTableSql(String databaseName) {
        return  "SELECT TABNAME AS TABLE_PATH " +
                "FROM SYSCAT.TABLES " +
                "WHERE TABSCHEMA = '" + schemaName + "' " +
                "AND TYPE = 'T' " +
                "ORDER BY TABNAME";

    }

    @Override
    protected DataSourceTableColumn buildColumn(Map<String, Object> item) {
        String columnName = item.get("column_name").toString();
        String dataType = item.get("data_type").toString();
        String isNullable = item.get("is_nullable").toString();
        String columnComment = item.get("column_comment") != null ? item.get("column_comment").toString() : null;
        String columnKey = item.get("column_key") != null ? item.get("column_key").toString() : null;
        int ordinalPosition = Integer.parseInt(item.get("ordinal_position").toString());

        return DataSourceTableColumn.builder()
                .isNullable(isNullable)
                .columnComment(columnComment)
                .columnKey(columnKey)
                .columnName(columnName)
                .sourceType(dataType.toUpperCase())
                .ordinalPosition(ordinalPosition)
                .build();
    }

    @Override
    protected String getSelectColumnsSql(TablePath tablePath) {
        return String.format(
                SELECT_COLUMNS_SQL_TEMPLATE, tablePath.getSchemaName(), tablePath.getTableName());
    }

    @Override
    protected String getSpecifiedColumnSql(TablePath tablePath, List<DataSourceTableColumn> columns) {
        List<String> columnNames = columns.stream()
                .map(DataSourceTableColumn::getColumnName)
                .collect(Collectors.toList());

        String quotedColumnNames = columnNames.stream()
                .map(name -> "'" + name + "'")
                .collect(Collectors.joining(", "));

        return String.format(SELECT_SPECIFIED_COLUMNS_SQL_TEMPLATE,
                tablePath.getSchemaName(),
                tablePath.getTableName(),
                quotedColumnNames);
    }
}
