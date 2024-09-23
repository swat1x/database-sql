package ru.swat1x.database.sql.driver;

import ru.swat1x.database.sql.SQLDriver;

public class Drivers {

  public static final SQLDriver MYSQL = new BaseSQLDriver("mysql", "com.mysql.jdbc.Driver");

  public static final SQLDriver MARIADB = new BaseSQLDriver("mariadb", "org.mariadb.jdbc.Driver");

}
