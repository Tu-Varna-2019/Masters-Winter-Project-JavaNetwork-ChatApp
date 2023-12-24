package model.database;

import model.FriendRequest;
import model.GroupChat;
import model.User;
import model.database.sql_statements.CreateTable;
import model.database.sql_statements.GetRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDBManager {
    private static final Logger logger = LogManager.getLogger(ChatDBManager.class.getName());

    private final String URL = System.getenv("POSTGRE_URL");
    private final String USERNAME = System.getenv("POSTGRE_USERNAME");
    private String PASSWORD = System.getenv("POSTGRE_PASSWORD");

    private final GetRecord getRecord = new GetRecord();

    // singleton
    private static ChatDBManager instance;
    private Connection connection;

    // Define the constructor as private to prevent direct instantiation of the
    // class (aka singleton)
    private void executeStatement(String tableQuery) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(tableQuery)) {
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ChatDBManager() {
        try {
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            executeStatement(CreateTable.createUserTableQuery);
            executeStatement(CreateTable.createFriendRequestTableQuery);
            executeStatement(CreateTable.createGroupChatTableQuery);
            executeStatement(CreateTable.createMessageTableQuery);

        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
    }

    public static ChatDBManager getInstance() {
        if (instance == null) {
            instance = new ChatDBManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public List<String> executeQuery(String query, String... columns) {
        List<String> queryResultList = new ArrayList<String>();

        try (PreparedStatement pst = connection.prepareStatement(query);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {

                // TODO: check for cases to getInt, getDouble, etc.
                // Example: int id = rs.getInt("id");
                StringBuilder row = new StringBuilder();

                for (String column : columns) {
                    row.append(rs.getString(column)).append(" ");
                }

                String rowResult = row.toString().trim();
                queryResultList.add(rowResult);
                logger.info("Received row -> {}", rowResult);
            }
            return queryResultList;
        } catch (SQLException ex) {
            logger.info(ex.getMessage());
        }
        return null;
    }

    public List<User> getUsersQuery(String query) {
        List<User> queryResultList = new ArrayList<User>();

        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {

                User user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("email"),
                        rs.getString("password"));

                queryResultList.add(user);
            }
            return queryResultList;

        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    public List<GroupChat> getGroupChatQuery(String query) {
        List<GroupChat> queryResultList = new ArrayList<GroupChat>();

        try (PreparedStatement pst = connection.prepareStatement(query);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {

                Array userIdsArray = rs.getArray("userids");
                Integer[] userIds = (Integer[]) userIdsArray.getArray();

                GroupChat groupChat = new GroupChat(rs.getInt("id"), rs.getString("name"), userIds);

                queryResultList.add(groupChat);
            }
            return queryResultList;
        } catch (SQLException ex) {
            logger.info(ex.getMessage());
        }
        return null;
    }

    public List<FriendRequest> getFriendRequestQuery(String query) {
        List<FriendRequest> queryResultList = new ArrayList<FriendRequest>();

        try (PreparedStatement pst = connection.prepareStatement(query);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {

                Integer userIdsArray = rs.getInt("recipientid");

                List<User> dbRetrievedUser = getUsersQuery(getRecord.getUserEQID(userIdsArray));

                FriendRequest friendRequest = new FriendRequest(rs.getInt("id"), rs.getString("status"),
                        dbRetrievedUser.get(0));

                queryResultList.add(friendRequest);
            }
            return queryResultList;
        } catch (SQLException ex) {
            logger.info(ex.getMessage());
        }
        return null;
    }

    public boolean updateRecordQuery(String query) {

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0)
                return true;

        } catch (SQLException ex) {
            logger.info(ex.getMessage());
        }
        return false;
    }

    public void insertQuery(String query, String... columns) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int index = 0; index < columns.length; index++) {
                preparedStatement.setString(index + 1, columns[index]);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertQueryFriendRequest(String query, String status, int senderID, int receiverID) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set values for the parameters
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, senderID);
            preparedStatement.setInt(3, receiverID);

            // Execute the update
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
