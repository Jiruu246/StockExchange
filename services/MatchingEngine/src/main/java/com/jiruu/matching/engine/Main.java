package com.jiruu.matching.engine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jiruu.matching.engine.model.Exchange;
import com.jiruu.matching.engine.model.Transaction;
import com.jiruu.matching.engine.net.ServerConnection;
import com.jiruu.net.Message;
import com.jiruu.net.MsgFlag;
import com.jiruu.net.ReqType;
import com.jiruu.net.Request;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String MULTICAST_GROUP = "4";
    private static final int SERVER_PORT = 4446;
    private static final int MULTICAST_PORT = 4447;
    private static final BlockingQueue<Request> orderQueue = new LinkedBlockingQueue<>();
    private static final Exchange exchange = new Exchange();

    @SuppressWarnings("InfiniteLoopStatement") // Temporary until we find a better solution
    public static void main(String[] args) {

        try (ServerConnection connection =
                     new ServerConnection(SERVER_PORT, MULTICAST_GROUP, MULTICAST_PORT)) {

            Thread exchangeThread = new Thread(() -> runExchange(connection));
            exchangeThread.setDaemon(true);
            exchangeThread.start();

            while (true) {
                Message message = connection.receive();
                Request request = Request.fromBytes(message.payload);
                System.out.println("Received request: " + request);

                // Ignore messages that are not BUY or SELL
                if (request.RequestType != ReqType.BUY & request.RequestType != ReqType.SEL) {
                    continue;
                }

                orderQueue.put(request);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error: ", e);
        }
    }

    private static void runExchange(ServerConnection connection) {
        while (true) {
            try {
                Request request = orderQueue.take();

                String orderId = request.OrderId.toString();
                boolean isBuy = request.RequestType == ReqType.BUY;
                int unit = request.Units;
                double limit = request.Limit;

                Transaction[] transactions;
                if (limit > 0) {
                    transactions = exchange.placeLimitOrder(orderId, isBuy, unit, limit);
                } else {
                    transactions = exchange.placeMarketOrder(orderId, isBuy, unit);
                }

                System.out.println(Arrays.toString(transactions));

                // send out transactions result
                for (Transaction transaction : transactions) {
                    List<Message> messages = new ArrayList<>();
                    Request BotReq = new Request(ReqType.BOT, UUID.fromString(transaction.getToId()), transaction.getEffectivePrice(), transaction.getFilledUnits());
                    try {
                        messages.add(new Message(
                                MsgFlag.ACK,
                                1,
                                UUID.randomUUID(),
                                -1, //TODO: This needs a correct seq number
                                0L,
                                BotReq.toBytes(),
                                InetAddress.getByName(MULTICAST_GROUP),
                                MULTICAST_PORT)
                        );
                        Request SldReq = new Request(ReqType.SLD, UUID.fromString(transaction.getFromId()), transaction.getEffectivePrice(), transaction.getFilledUnits());
                        messages.add(new Message(
                                MsgFlag.ACK,
                                1,
                                UUID.randomUUID(),
                                -1, //TODO: This needs a correct seq number
                                0L,
                                SldReq.toBytes(),
                                InetAddress.getByName(MULTICAST_GROUP),
                                MULTICAST_PORT));
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Error creating message: ", e);
                    }
                    for (Message msg : messages) {
                        Thread sendThread = new Thread(() -> {
                            try {
                                connection.sendMulticast(msg);
                            } catch (IOException e) {
                                LOGGER.log(Level.SEVERE, "Error sending message: ", e);
                            }
                        });
                        sendThread.setDaemon(true);
                        sendThread.start();
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.SEVERE, "Error taking request from queue", e);
            }
        }
    }
}
