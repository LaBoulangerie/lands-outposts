package net.laboulangerie.landsoutposts.database;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.laboulangerie.landsoutposts.LandsOutposts;

public class LandsOutpostsDatabase {

    private JdbcPooledConnectionSource connection;
    private Dao<LandOutpost, Long> outpostsDao; 
    private Logger logger;

    public LandsOutpostsDatabase(LandsOutposts landsOutposts) throws SQLException {
        this.logger = landsOutposts.getLogger();

        File db = new File(landsOutposts.getDataFolder(), landsOutposts.getName());
        this.connection = new JdbcPooledConnectionSource("jdbc:h2:./" + db.toPath());
        this.logger.fine("Opened database successfully");

        this.outpostsDao = DaoManager.createDao(this.connection, LandOutpost.class);

        setup();
    }

    public void close() throws Exception {
        this.connection.close();
        this.logger.fine("Closed database successfully");
    }

    private void setup() throws SQLException {
        this.logger.info("Initialize database.");
        TableUtils.createTableIfNotExists(this.connection, LandOutpost.class);
    }

    /*
     * GETTER
     */

    public JdbcPooledConnectionSource getConnection() {
        return connection;
    }

    public Dao<LandOutpost, Long> getOutpostsDao() {
        return outpostsDao;
    }

}
