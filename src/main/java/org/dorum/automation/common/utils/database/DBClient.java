package org.dorum.automation.common.utils.database;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Properties;

@Getter
@Log4j2
public class DBClient {

    private static DBClient instance;
    private static String schema;
    private Connection connection;

    @SneakyThrows
    public static DBClient getInstance(DBConfigData dbConfigData) {
        if ((instance == null)
                || (instance.getConnection() == null)
                || instance.getConnection().isClosed()) {
            instance = new DBClient(dbConfigData);
        }
        return instance;
    }

    @SneakyThrows
    public void closeDBConnection() {
        if (!Objects.requireNonNull(connection).isClosed()) {
            connection.close();
            log.info("DB connection is closed");
        } else {
            log.warn("DB connection is already closed");
        }
    }

    public int runCommand(String command) {
        log.info("Executing SQL query (schema: {}): {}", schema, command);
        int rowsAffected  = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            rowsAffected  = preparedStatement.executeUpdate();
            log.info("Updated: {} rows", rowsAffected );
            preparedStatement.close();
        } catch (Exception e) {
            log.error("FAILED - DB Utils run update command: {}\n{}", command, e);
        }
        return rowsAffected;
    }

    public int countResults(String command) {
        log.info("Executing SQL query (schema: {}): {}", schema, command);
        int result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            log.info("Count: {}", result);
        } catch (Exception e) {
            log.error("FAILED - DB Utils count results - {}\n{}", command, e);
        }
        return result;
    }

    public String runSelectQuery(String command) {
        log.info("Executing SQL query (schema: {}): {}", schema, command);
        String result = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
            log.info("Query result: {}", result);
            preparedStatement.close();
            resultSet.close();
        } catch (Exception e) {
            log.error("FAILED - DB Utils run select query: {}\n{}", command, e);
        }
        return result;
    }

    //--------------- Private Methods ---------------

    private DBClient(DBConfigData dbConfigData) {
        schema = dbConfigData.getSchema();
        Properties props = new Properties();
        props.setProperty("user", dbConfigData.getUser());
        props.setProperty("password", dbConfigData.getPassword());
        props.setProperty("options", "-c statement_timeout=15min");
        props.setProperty("connectTimeout", "600");
        props.setProperty("socketTimeout", "600");
        int attempt = 0;
        while (attempt != 2) {
            try {
                connection = DriverManager.getConnection(dbConfigData.getHost(), props);
                DriverManager.setLoginTimeout(15);
                connection.setSchema(schema);
                connection.setClientInfo("ApplicationName", "");
                if (instance == null) {
                    log.info("---------------------------- DATA BASE Info (Start) ----------------------------");
                    log.info("Connection timeout: {} ms" ,connection.getNetworkTimeout());
                    log.info("Driver name: {}", connection.getMetaData().getDriverName());
                    log.info("Application name: {}", connection.getClientInfo().getProperty("ApplicationName"));
                    log.info("Catalog term: {}", connection.getMetaData().getCatalogTerm());
                    log.info("Driver version: {}", connection.getMetaData().getDriverVersion());
                    log.info("Driver major version: {}", connection.getMetaData().getDriverMajorVersion());
                    log.info("Driver minor version: {}", connection.getMetaData().getDriverMinorVersion());
                    log.info("Driver max connections: {}", connection.getMetaData().getMaxConnections());
                    log.info("DB username: {}", connection.getMetaData().getUserName());
                    log.info("DB URL: {}", connection.getMetaData().getURL());
                    log.info("DB Schema: {}", schema);
                    log.info("DB Catalog: {}", connection.getCatalog());
                    log.info("DB Product name: {}", connection.getMetaData().getDatabaseProductName());
                    log.info("DB Product version: {}", connection.getMetaData().getDatabaseProductVersion());
                    log.info("DB Product major version: {}", connection.getMetaData().getDatabaseMajorVersion());
                    log.info("DB Product minor version: {}", connection.getMetaData().getDatabaseMinorVersion());
                    log.info("---------------------------- DATA BASE Info (End) ------------------------------");
                }
                break;
            } catch (Exception e) {
                attempt++;
                log.warn("FAILED - DB Utils initialization\n{}", e);
            }
        }
        if (attempt == 2) {
            log.error("FAILED - DB Utils initialization");
        }
    }
}
