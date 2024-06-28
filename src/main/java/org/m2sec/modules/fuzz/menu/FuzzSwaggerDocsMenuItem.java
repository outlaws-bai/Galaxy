package org.m2sec.modules.fuzz.menu;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.GalaxyMain;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.WorkExecutor;
import org.m2sec.common.models.ApiInfo;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.utils.SwaggerUtil;

import javax.swing.*;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class FuzzSwaggerDocsMenuItem extends AbstractMenuItem {


    @Override
    public String displayName() {
        return "FuzzSwaggerDocs";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.invocationType().containsHttpMessage() && event.messageEditorRequestResponse().isPresent() && event.messageEditorRequestResponse().get().selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.RESPONSE;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        Request originRequest = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        Response originResponse = Response.of(messageEditorHttpRequestResponse.requestResponse().response());

        String userInput = JOptionPane.showInputDialog("Enter prefix url or " + "path: ");
        if (userInput == null || (!userInput.startsWith("/") && !userInput.startsWith("http"))) {
            JOptionPane.showMessageDialog(null, "Please input url(http.*) or " + "path(/.*)", "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<ApiInfo> apiInfoList = SwaggerUtil.extractApiInfoFromSwagger(new String(originResponse.getContent()));
        List<Runnable> workRunnables = apiInfoList.stream().map(apiInfo -> (Runnable) () -> {
            Request request = apiInfo.generateRequest(originRequest, userInput);
            HttpRequest httpRequest = request.toBurp();
            Annotations annotations = Annotations.annotations(apiInfo.getNoteString());
            try {
                HttpRequestResponse requestResponse;
                if (GalaxyMain.config.getFuzzConfig().isSwaggerGeneratedRequestAutoSend()) {
                    requestResponse = GalaxyMain.burpApi.http().sendRequest(httpRequest);
                    requestResponse = requestResponse.withAnnotations(annotations);
                } else {
                    requestResponse = HttpRequestResponse.httpRequestResponse(httpRequest,
                        HttpResponse.httpResponse(), annotations);
                }
                GalaxyMain.burpApi.organizer().sendToOrganizer(requestResponse);
            } catch (Exception e) {
                log.error("send request fail. request: {}, message: {}", request, e.getMessage(), e);
            }
        }).toList();
        WorkExecutor.INSTANCE.beyondBatchExecute(
            () -> GalaxyMain.burpApi.logging().raiseInfoEvent(String.format("%s get %d url. Please wait for execution" +
                ".", displayName(), apiInfoList.size())),
            () -> GalaxyMain.burpApi.logging().raiseInfoEvent("Fuzz Swagger Docs execute complete."),
            workRunnables.toArray(Runnable[]::new));
    }
}
