package controller.events.handlers.message;

import java.util.List;

import controller.events.handlers.shared.SharedEventHandler;
import model.Message;
import model.dataclass.ClientRequest;

public class GetMessagesEventHandler extends SharedEventHandler {

        @Override
        public String handleEvent(ClientRequest payload) {
                message = "No messages found!";

                String groupid = payload.data.id;

                List<Message> dbMessages = sharedMessage.getMessages(groupid);

                if (!dbMessages.isEmpty()) {
                        status = "Success";
                        message = "Messages found for the group id: " + groupid + " !";
                        dataResponse.messages = dbMessages;
                }

                return sendPayloadToClient();

        }
}
