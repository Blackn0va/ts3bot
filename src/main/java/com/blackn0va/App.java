package com.blackn0va;

import java.util.Random;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;

public class App {
    public static int ClientIDBlack = 0;
    public static int ClientIDFly = 0;
    public static int ClientIDChukky = 0;
    public static String randomName = "";
    public static String answer = "";

    public static final TS3Config config = new TS3Config();
    private static volatile int clientId;

    public static void main(String[] args) {

        GenerateNickname(randomName);

        config.setHost("ts.laonda-clan.eu");
        config.setEnableCommunicationsLogging(true);

        // Use default exponential backoff reconnect strategy
        config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());

        // Make stuff run every time the query (re)connects
        config.setConnectionHandler(new ConnectionHandler() {

            @Override
            public void onConnect(TS3Api api) {
                stuffThatNeedsToRunEveryTimeTheQueryConnects(api);
            }

            @Override
            public void onDisconnect(TS3Query ts3Query) {
                // Nothing
            }
        });

        final TS3Query query = new TS3Query(config);
        // Here "stuffThatNeedsToRunEveryTimeTheQueryConnects" will be run!
        // (And every time the query reconnects)
        query.connect();

        // Then do other stuff that only needs to be done once
        stuffThatOnlyEverNeedsToBeRunOnce(query.getApi());

        doSomethingThatTakesAReallyLongTime(query.getAsyncApi());

        // Disconnect once we're done
        // query.exit();

        /*
         * 
         * 
         * 
         * 
         * 
         * //Get all channel ID´s
         * //api.getChannels().forEach(channel -> System.out.println(channel.getName() +
         * " " + channel.getId()));
         * 
         * //get User ID´s
         * //api.getClients().forEach(client -> System.out.println(client.getNickname()
         * + " " + client.getId()));
         * 
         * 
         * //join channel
         * 
         * 
         * //api.moveClient(6353, 417);
         * 
         * 
         * 
         * 
         * api.getChannels().forEach(channel -> api.moveClient(ClientIDBlack,
         * channel.getId()));
         */

        /*
         * 
         * Zockerhallen 413
         * Zockerhalle I 412
         * Zockerhalle II 414
         * Zockerhalle III 415
         * Zockerhalle IV 416
         * Zockerhalle V 417
         */
    }

    // funktion return String
    public static String GenerateNickname(String nickname) {

        // Create Random Names
        Random rand = new Random();
        int randomNum = rand.nextInt((999 - 100) + 1) + 100;
        randomName = "Bot-" + randomNum;
        return randomName;
    }

    public static void BotStarten() {

        try {

        } catch (Exception e) {
        }

    }

    private static void stuffThatNeedsToRunEveryTimeTheQueryConnects(TS3Api api) {
        try {
            GenerateNickname(randomName);
            // Logging in, selecting the virtual server, selecting a channel
            // and setting a nickname needs to be done every time we reconnect
            api.login("OpenAIBot", "QueryPassword");
            api.selectVirtualServerById(1);

            // api.moveQuery(x);
            api.setNickname(randomName);

            api.moveQuery(415);

            // What events we listen to also resets
            // api.registerEvent(TS3EventType.TEXT_CHANNEL, 415);

            // Out clientID changes every time we connect and we need it
            // for our event listener, so we need to store the ID in a field
            clientId = api.whoAmI().getId();

            api.registerAllEvents();
            api.registerEvent(TS3EventType.SERVER);
            api.registerEvent(TS3EventType.TEXT_CHANNEL);
            api.registerEvent(TS3EventType.TEXT_PRIVATE);
            api.registerEvent(TS3EventType.TEXT_SERVER);

            api.addTS3Listeners(new TS3EventAdapter() {

                @Override
                public void onTextMessage(TextMessageEvent e) {

                    // if message is private then send message to user who send the message
                    if (e.getTargetMode() == TextMessageTargetMode.CLIENT) {
                        if (!e.getInvokerName().contains("Bot")) {

                            // call get answer and return answer
                            answer = getAnswer(e.getMessage());

                            // api.sendChannelMessage(answer);
                            api.sendPrivateMessage(e.getInvokerId(), answer);
                            System.out.println(answer);
                        }
                    } else {
                        if (!e.getInvokerName().contains("Bot")) {
                            // call get answer and return answer
                            answer = getAnswer(e.getMessage());

                            api.sendChannelMessage(answer);
                            System.out.println(answer);
                        }
                    }

                }

                @Override
                public void onClientMoved(ClientMovedEvent e) {
                    // System.out.println(e.getClientNickname() + " moved to channel " +
                    // e.getTargetChannelId());
                    // send message to user

                    // if channel is 415 then send message
                    if (e.getTargetChannelId() == 415) {
                        // call get answer and return answer
                        // answer = getAnswer("Stell dich in 50 worten vor. Du bist eine KI und stellst
                        // dich freundlich vor. Sag auch, dass sie bitte mit dir schreiben müssen, da du
                        // nichts hörst. Und bitte sag Du und nicht Sie");

                        api.sendPrivateMessage(e.getClientId(),
                                "Hallo, ich bin eine KI. Ich freue mich, dich kennenzulernen! Ich bin hier, um dir zu helfen und deine Fragen zu beantworten. Bitte schreib mir deine Fragen, da ich nichts hören kann. Ich werde mein Bestes geben, um dir so schnell wie möglich zu antworten. Wenn du noch etwas brauchst, lass es mich bitte wissen. Ich freue mich darauf, mit dir zu interagieren!");
                    }
                }

            });

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    // funktion return string antwort
    public static String getAnswer(String question) {
        OpenAiService service = new OpenAiService(
                "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");

        System.out.println("\nCreating completion... ");
        final CompletionRequest completionRequest = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt(question)
                .maxTokens(150)
                .temperature(0.5)
                .topP(0.3)
                .frequencyPenalty(0.5)
                .presencePenalty(0.0)
                .echo(true)
                .build();

        // String answer =
        // service.createCompletion(completionRequest).getChoices(completionResponse ->
        // completionResponse.getChoices().toString().replace("[CompletionChoice(text=",
        // "").replace(", index=)]", "").replace(", logprobs=", "").replace(",
        // finish_reason=", "").replace(", index=0nullstop)", "").replace("]", ""));

        // service.createCompletion(completionRequest).getChoices().forEach(System.out::print);
        answer = service.createCompletion(completionRequest).getChoices().toString()
                .replace("[CompletionChoice(text=", "").replace(", index=)]", "")
                .replace(", logprobs=", "").replace(", finish_reason=", "")
                .replace(", index=0nullstop)", "").replace("]", "")
                .replace("index=0nulllength)", "");

        return answer;
    }

    private static void stuffThatOnlyEverNeedsToBeRunOnce(final TS3Api api) {
        // We only want to greet people once
        // api.sendChannelMessage("PutPutBot is online!");

        GenerateNickname(randomName);

        // On the API side of things, you only need to register your TS3Listeners once!
        // These are not affected when the query disconnects.
        api.registerAllEvents();
        api.registerEvent(TS3EventType.SERVER);
        api.registerEvent(TS3EventType.TEXT_CHANNEL);
        api.registerEvent(TS3EventType.TEXT_PRIVATE);
        api.registerEvent(TS3EventType.TEXT_SERVER);

        api.addTS3Listeners(new TS3EventAdapter() {

            @Override
            public void onTextMessage(TextMessageEvent e) {

                // if message is private then send message to user who send the message
                if (e.getTargetMode() == TextMessageTargetMode.CLIENT) {
                    if (!e.getInvokerName().contains("Bot")) {
                        // call get answer and return answer
                        answer = getAnswer(e.getMessage());

                        // api.sendChannelMessage(answer);
                        api.sendPrivateMessage(e.getInvokerId(), answer);
                        System.out.println(answer);
                    }
                } else {
                    if (!e.getInvokerName().contains("Bot")) {
                        // call get answer and return answer
                        answer = getAnswer(e.getMessage());

                        api.sendChannelMessage(answer);
                        System.out.println(answer);
                    }
                }

            }

            @Override
            public void onClientMoved(ClientMovedEvent e) {
                // System.out.println(e.getClientNickname() + " moved to channel " +
                // e.getTargetChannelId());
                // send message to user

                // if channel is 415 then send message
                if (e.getTargetChannelId() == 415) {
                    // answer = getAnswer("Stell dich in 50 worten vor. Du bist eine KI und stellst
                    // dich freundlich vor. Sag auch, dass sie bitte mit dir schreiben müssen, da du
                    // nichts hörst. Und bitte sag Du und nicht Sie");

                    api.sendPrivateMessage(e.getClientId(),
                            "Hallo, ich bin eine KI. Ich freue mich, dich kennenzulernen! Ich bin hier, um dir zu helfen und deine Fragen zu beantworten. Bitte schreib mir deine Fragen, da ich nichts hören kann. Ich werde mein Bestes geben, um dir so schnell wie möglich zu antworten. Wenn du noch etwas brauchst, lass es mich bitte wissen. Ich freue mich darauf, mit dir zu interagieren!");
                }
            }

        });
    }

    private static void doSomethingThatTakesAReallyLongTime(TS3ApiAsync api) {

    }

}
