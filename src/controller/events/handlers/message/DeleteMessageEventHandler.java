package controller.events.handlers.message;

import java.util.List;

import controller.events.handlers.shared.SharedEventHandler;
import model.Message;
import model.dataclass.ClientResponse;
import model.storage.S3Manager;

public class DeleteMessageEventHandler extends SharedEventHandler {

        @Override
        public String handleEvent(ClientResponse payload) {

                String messageID = payload.data.id;
                try {

                        List<Message> dbMessage = chatDBManager
                                        .getMessagesQuery(getRecord.getMessageEQID(Integer.parseInt(messageID)));

                        S3Manager.deleteFile(dbMessage.get(0).getSender().getEmail() + "/"
                                        + dbMessage.get(0).getAttachmentURL());

                        boolean isDeleted = chatDBManager
                                        .updateRecordQuery(
                                                        deleteRecord.DeleteMessageEQID(Integer.parseInt(messageID)));

                        String status = !isDeleted ? "Failed" : "Success";
                        String message = !isDeleted ? "Unable to delete message!" : "Successfully deleted message!";

                        return String.format(
                                        "{\"response\":{\"status\":\"%s\",\"message\":\"%s\"}}",
                                        status,
                                        message);

                } catch (Exception e) {
                        logger.error("Error: {}", e.getMessage());
                }

                return (String.format(
                                "{\"response\":{\"status\":\"%s\",\"message\":\"%s\", \"message\":\"%s\"}}",
                                "Failed", "Error, please try again!"));
        }
}
