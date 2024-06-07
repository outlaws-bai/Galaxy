package org.m2sec.modules.fuzz.menu;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.GalaxyMain;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.Log;
import org.m2sec.common.models.ApiInfo;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.utils.AsyncExecuteUtil;
import org.m2sec.common.utils.SwaggerUtil;

import javax.swing.*;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class FuzzSwaggerDocsMenuItem extends AbstractMenuItem {

    private static final Log log = new Log(FuzzSwaggerDocsMenuItem.class);

    @Override
    public String displayName() {
        return "FuzzSwaggerDocs";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.invocationType().containsHttpMessage()
                && event.messageEditorRequestResponse().isPresent()
                && event.messageEditorRequestResponse().get().selectionContext()
                        == MessageEditorHttpRequestResponse.SelectionContext.RESPONSE;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse =
                event.messageEditorRequestResponse().get();
        Request originRequest =
                Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        Response originResponse =
                Response.of(messageEditorHttpRequestResponse.requestResponse().response());

        String userInput = JOptionPane.showInputDialog("Enter prefix url or path: ");
        if (userInput == null || (!userInput.startsWith("/") && !userInput.startsWith("http"))) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please input url(http.*) or path(/.*)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<ApiInfo> apiInfoList =
                SwaggerUtil.extractApiInfoFromSwagger(new String(originResponse.getContent()));
        log.infoEvent(
                "%s get %d url. Please wait for execution.", displayName(), apiInfoList.size());
        List<Runnable> workRunnables =
                apiInfoList.stream()
                        .map(
                                apiInfo ->
                                        (Runnable)
                                                () -> {
                                                    Request request =
                                                            apiInfo.generateRequest(
                                                                    originRequest, userInput);
                                                    HttpRequest httpRequest = request.toBurp();
                                                    try {
                                                        HttpRequestResponse requestResponse =
                                                                GalaxyMain.burpApi
                                                                        .http()
                                                                        .sendRequest(httpRequest);
                                                        Annotations annotations =
                                                                Annotations.annotations(
                                                                        apiInfo.getNoteString());
                                                        requestResponse.withAnnotations(
                                                                annotations);
                                                        GalaxyMain.burpApi
                                                                .organizer()
                                                                .sendToOrganizer(
                                                                        requestResponse
                                                                                .withAnnotations(
                                                                                        annotations));
                                                    } catch (Exception e) {
                                                        log.exception(
                                                                e,
                                                                "send request fail. request: %s, message: %s",
                                                                request,
                                                                e.getMessage());
                                                    }
                                                })
                        .toList();
        AsyncExecuteUtil.execute(
                workRunnables, () -> log.infoEvent("Fuzz Swagger Docs execute complete."));
    }
}
