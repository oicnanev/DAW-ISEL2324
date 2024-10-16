package isel.leic.daw.gomoku

object Environment {
    fun getDbUrl() = System.getenv(KEY_DB_URL) ?: throw Exception("Missing env var $KEY_DB_URL")
    private const val KEY_DB_URL = "DB_URL"

    // to run remotely on a postgresql server
    // fun getDbUrl() = "jdbc:postgresql://minedust.ddns.net:5432/postgres?user=postgres&password=4w5_Yd4xee35$"

    // to run locally on docker if not using environment variable
    // fun getDbUrl() = "jdbc:postgresql://db-tests:5432/db?user=dbuser&password=changeit"
}
