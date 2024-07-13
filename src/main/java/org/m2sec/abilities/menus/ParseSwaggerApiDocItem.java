package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.ApiInfo;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.SwaggerParser;
import org.m2sec.core.common.WorkExecutor;
import org.m2sec.core.enums.Protocol;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.m2sec.core.common.SwingTools;

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

        String userInput = JOptionPane.showInputDialog("Please input url or relative path");
        if (userInput == null || (!userInput.startsWith("/") && !userInput.startsWith(Protocol.HTTP.toRaw()))) {
            SwingTools.showException(new InputMismatchException("Please input url or relative path!"));
            return;
        }
        List<ApiInfo> apiInfoList = SwaggerParser.parseSwaggerDoc(new String(originResponse.getContent()));
        List<Runnable> workRunnables = apiInfoList.stream().map(apiInfo -> (Runnable) () -> {
            Request request = apiInfo.generateRequest(originRequest, userInput);
            HttpRequest httpRequest = request.toBurp();
            Annotations annotations = Annotations.annotations(apiInfo.getNoteString());
            try {
                HttpRequestResponse requestResponse;
                if (config.getSetting().isParsedSwaggerApiDocRequestAutoSend()) {
                    requestResponse = api.http().sendRequest(httpRequest);
                    requestResponse = requestResponse.withAnnotations(annotations);
                } else {
                    requestResponse = HttpRequestResponse.httpRequestResponse(httpRequest,
                        HttpResponse.httpResponse(), annotations);
                }
                api.organizer().sendToOrganizer(requestResponse);
            } catch (Exception e) {
                log.error("send request fail. request: {}, message: {}", request, e.getMessage(), e);
            }
        }).toList();
        WorkExecutor.INSTANCE.beyondBatchExecute(
            () -> api.logging().raiseInfoEvent(String.format("%s get %d url. Please wait for execution" +
                ".", displayName(), apiInfoList.size())),
            () -> api.logging().raiseInfoEvent("Fuzz Swagger Docs execute complete. You can view it in tab Organizer."),
            workRunnables.toArray(Runnable[]::new));
    }
}
