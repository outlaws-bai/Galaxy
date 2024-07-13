package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.Galaxy;
import org.m2sec.core.common.*;
import org.m2sec.core.enums.Protocol;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;

import javax.swing.*;
import java.util.InputMismatchException;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/7/13 11:02
 * @description:
 */
@Slf4j
public class ParseSwaggerApiDocItem extends IItem {
    public ParseSwaggerApiDocItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Parse Swagger Api Doc";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.invocationType().containsHttpMessage()
            && event.messageEditorRequestResponse().isPresent()
            && event.messageEditorRequestResponse().get().selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.RESPONSE;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        Request originRequest = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        Response originResponse = Response.of(messageEditorHttpRequestResponse.requestResponse().response());
        action(api, originRequest, originResponse);
    }

    public void action(MontoyaApi montoyaApi, Request originRequest, Response originResponse) {
        String userInput = JOptionPane.showInputDialog("Please input url or relative path");
        if (userInput == null || (!userInput.startsWith("/") && !userInput.startsWith(Protocol.HTTP.toRaw()))) {
            SwingTools.showException(new InputMismatchException("Please input url or relative path!"));
            return;
        }
        List<ApiInfo> apiInfoList = SwaggerParser.parseSwaggerDoc(new String(originResponse.getContent()));
        List<Runnable> workRunnables = apiInfoList.stream().map(apiInfo -> (Runnable) () -> run(montoyaApi,
            originRequest, originResponse, apiInfo, userInput)).toList();
        WorkExecutor.INSTANCE.beyondBatchExecute(
            () -> before(montoyaApi, apiInfoList.size()),
            () -> after(montoyaApi),
            workRunnables.toArray(Runnable[]::new));
    }

    public void run(MontoyaApi montoyaApi, Request originRequest, Response response, ApiInfo apiInfo,
                    String userInput) {
        if (Galaxy.isInBurp()) {
            Request request = apiInfo.generateRequest(originRequest, userInput);
            HttpRequest httpRequest = request.toBurp();
            Annotations annotations = Annotations.annotations(apiInfo.getNoteString());
            try {
                HttpRequestResponse requestResponse;
                if (config.getSetting().isParsedSwaggerApiDocRequestAutoSend()) {
                    requestResponse = montoyaApi.http().sendRequest(httpRequest);
                    requestResponse = requestResponse.withAnnotations(annotations);
                } else {
                    requestResponse = HttpRequestResponse.httpRequestResponse(httpRequest,
                        HttpResponse.httpResponse(), annotations);
                }
                montoyaApi.organizer().sendToOrganizer(requestResponse);
            } catch (Exception e) {
                log.error("send request fail. request: {}, message: {}", request, e.getMessage(), e);
            }
        } else {
            log.debug("test send request to organizer.");
        }
    }

    public void before(MontoyaApi montoyaApi, int size) {
        String message = String.format("%s get %d url. Please wait for execution.", displayName(), size);
        if (Galaxy.isInBurp()) {
            montoyaApi.logging().raiseInfoEvent(message);
        } else {
            log.info(message);
        }
    }

    public void after(MontoyaApi montoyaApi) {
        String message = "Fuzz Swagger Docs execute complete. You can view it in tab Organizer.";
        if (Galaxy.isInBurp()) {
            montoyaApi.logging().raiseInfoEvent(message);
        } else {
            log.info(message);
        }
    }

}
