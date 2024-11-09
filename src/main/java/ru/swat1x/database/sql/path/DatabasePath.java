package ru.swat1x.database.sql.path;

/**
 * @see DedicatedDatabaseServerPath DedicatedDatabaseServerPath - dedicated server path
 * @see LocalFileDatabasePath LocalFileDatabasePath - file server path
 */
public interface DatabasePath {

  String getPath();

}
