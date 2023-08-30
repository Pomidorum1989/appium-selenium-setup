package org.dorum.automation.common.utils.database;

import lombok.Getter;
import lombok.SneakyThrows;
import org.dorum.automation.common.utils.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Properties;

@Getter
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
            Log.info("DB connection is closed");
        } else {
            Log.warn("DB connection is already closed");
        }
    }

    public int runCommand(String command) {
        Log.info("Executing SQL query (schema: %s): %s", schema, command);
        int rowsAffected  = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            rowsAffected  = preparedStatement.executeUpdate();
            Log.info("Updated: %s rows", rowsAffected );
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.exception("FAILED - DB Utils run update command: %s\n%s", command, e);
        }
        return rowsAffected;
    }

    public int countResults(String command) {
        Log.info("Executing SQL query (schema: %s): %s", schema, command);
        int result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            Log.info("Count: %s", result);
        } catch (Exception e) {
            Log.exception("FAILED - DB Utils count results - %s\n%s", command, e);
        }
        return result;
    }

    public String runSelectQuery(String command) {
        Log.info("Executing SQL query (schema: %s): %s", schema, command);
        String result = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
            Log.info("Query result: %s", result);
            preparedStatement.close();
            resultSet.close();
        } catch (Exception e) {
            Log.exception("FAILED - DB Utils run select query: %s\n%s", command, e);
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
                    Log.info("---------------------------- DATA BASE Info (Start) ----------------------------");
                    Log.info("Connection timeout: %s ms" ,connection.getNetworkTimeout());
                    Log.info("Driver name: %s", connection.getMetaData().getDriverName());
                    Log.info("Application name: %s", connection.getClientInfo().getProperty("ApplicationName"));
                    Log.info("Catalog term: %s", connection.getMetaData().getCatalogTerm());
                    Log.info("Driver version: %s", connection.getMetaData().getDriverVersion());
                    Log.info("Driver major version: %s", connection.getMetaData().getDriverMajorVersion());
                    Log.info("Driver minor version: %s", connection.getMetaData().getDriverMinorVersion());
                    Log.info("Driver max connections: %s", connection.getMetaData().getMaxConnections());
                    Log.info("DB username: %s", connection.getMetaData().getUserName());
                    Log.info("DB URL: %s", connection.getMetaData().getURL());
                    Log.info("DB Schema: %s", schema);
                    Log.info("DB Catalog: %s", connection.getCatalog());
                    Log.info("DB Product name: %s", connection.getMetaData().getDatabaseProductName());
                    Log.info("DB Product version: %s", connection.getMetaData().getDatabaseProductVersion());
                    Log.info("DB Product major version: %s", connection.getMetaData().getDatabaseMajorVersion());
                    Log.info("DB Product minor version: %s", connection.getMetaData().getDatabaseMinorVersion());
                    Log.info("---------------------------- DATA BASE Info (End) ------------------------------");
                }
                break;
            } catch (Exception e) {
                attempt++;
                Log.warn("FAILED - DB Utils initialization\n%s", e);
            }
        }
        if (attempt == 2) {
            Log.exception("FAILED - DB Utils initialization");
        }
    }
}
