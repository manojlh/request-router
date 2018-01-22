package com.wso2telco.gateway.requestRouter;

import com.wso2telco.gateway.requestRouter.model.HeaderModel;
import com.wso2telco.gateway.requestRouter.model.HeaderModes;
import com.wso2telco.gateway.requestRouter.model.ReplaceBodyModel;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by WSO2telco
 */
public class DatabaseLibrary {

    private static DataSource DatabaseSource;
    private static Logger logger;

    public DataSource getDatabaseSource() {
        return DatabaseSource;
    }

    public void setDatabaseSource(DataSource databaseSource) {
        DatabaseSource = databaseSource;
    }

    public DatabaseLibrary() {
        logger = Logger.getLogger(DatabaseLibrary.class);
    }

    public static ArrayList<HeaderModel> getHeaders(String domain) {


        ArrayList<HeaderModel> headers = new ArrayList<HeaderModel>();
        String sql = "SELECT * FROM `headers` WHERE `domain` = ?;";
        Connection connection = null;

        try {

            connection = DatabaseSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, domain);
            ResultSet result = preparedStatement.executeQuery();


            while (result.next()) {
                HeaderModel header = new HeaderModel();

                header.setId(result.getInt("id"));
                header.setDomain(result.getString("domain"));
                header.setUrlPrefix(result.getString("urlPrefix"));
                header.setHeader(result.getString("header"));
                header.setHeaderValue(result.getString("headerValue"));

                try {
                    header.setMode(HeaderModes.valueOf(result.getString("mode")));
                } catch (Exception et) {
                    header.setMode(HeaderModes.ADD);
                }
                headers.add(header);
            }
        } catch (SQLException e) {

            logger.error("DB getting HeaderModel SQL Error : " + e.getMessage());

        } catch (Exception e) {
            logger.error("DB getting HeaderModel Error : " + e.getMessage());

        } finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException e) {
            }
        }

        return headers;

    }

    public static ArrayList<ReplaceBodyModel> getReplaceBodyList(String urlKey) {


        ArrayList<ReplaceBodyModel> replaceBodyModels = new ArrayList<ReplaceBodyModel>();
        String sql = "SELECT * FROM `replacebody` WHERE `urlKey` = ?;";
        Connection connection = null;

        try {

            connection = DatabaseSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, urlKey);
            ResultSet result = preparedStatement.executeQuery();


            while (result.next()) {
                ReplaceBodyModel replacement = new ReplaceBodyModel();

                replacement.setId(result.getInt("id"));
                replacement.setUrlKey(result.getString("urlKey"));
                replacement.setJsonPath(result.getString("jsonPath"));
                replacement.setFind(result.getString("find"));
                replacement.setReplace(result.getString("replace"));
                replacement.setNeedURLDecodeRest(result.getInt("needURLDecodeRest") == 1 ? true : false);

                replaceBodyModels.add(replacement);
            }
        } catch (SQLException e) {

            logger.error("DB getting ReplaceBodyModel SQL Error : " + e.getMessage());

        }catch(Exception e){
            logger.error("DB getting ReplaceBodyModel Error : " + e.getMessage());
        }finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException e) {
            }
        }

        return replaceBodyModels;

    }


}
