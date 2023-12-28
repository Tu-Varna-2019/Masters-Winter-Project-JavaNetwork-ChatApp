package controller.events.handlers;

import java.util.List;
import java.util.TreeMap;

import controller.events.SharedDataEventHandler;
import model.User;

public class RenameEmailEventHandler extends SharedDataEventHandler {

        @Override
        public String handleEvent(TreeMap<String, String> payload) {
                String newEmail = payload.get("email");
                String oldEmail = payload.get("username");
                logger.info("\nReceived old Email: {} \n New Email: {}", oldEmail, newEmail);

                List<User> dbRetrievedUser = chatDBManager.getUsersQuery(getRecord.getUserEQEmail(oldEmail));

                List<User> dbCheckUserEQEmail = chatDBManager.getUsersQuery(getRecord.getUserEQEmail(newEmail));

                // Check if email already exists
                if (!dbCheckUserEQEmail.isEmpty())
                        return "{\"response\":{\"status\":\"Failed\",\"message\":\"Email already exists\"}}";

                logger.info("Now changing {} -> {}" + dbRetrievedUser.get(0).getEmail(), newEmail);

                if (!dbRetrievedUser.isEmpty()) {
                        boolean isEmailUpdated = chatDBManager
                                        .updateRecordQuery(updateRecord.UpdateEmailEQID(
                                                        newEmail,
                                                        dbRetrievedUser.get(0).getId()));

                        String status = !isEmailUpdated ? "Failed" : "Success";
                        String message = !isEmailUpdated ? "Failed to update email. Please try again!"
                                        : "Successfully updated email!";

                        return String.format(
                                        "{\"response\":{\"status\":\"%s\",\"message\":\"%s\"}}",
                                        status,
                                        message);
                } else
                        return "{\"response\":{\"status\":\"Failed\",\"message\":\"Email not found, sorry for the inconvenience! \"}}";
        }
}
