package org.dorum.automation.common.utils.database;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DBConfigData {

    private String host;
    private String schema;
    private String password;
    private String user;

    public DBConfigData(String host, String schema, String user, String password) {
        this.host = host;
        this.schema = schema;
        this.user = user;
        this.password = password;
    }
}
