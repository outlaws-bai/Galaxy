package org.m2sec.modules.fuzz.menu;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.InvocationType;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.Constants;
import org.m2sec.common.Log;
import org.m2sec.common.Tuple;
import org.m2sec.common.models.*;
import org.m2sec.common.parsers.YamlParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class ExtractInfoDictMenuItem extends AbstractMenuItem {

    private static final Log log = new Log(ExtractInfoDictMenuItem.class);

    @Override
    public String displayName() {
        return "ExtractInfo";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.isFrom(InvocationType.SITE_MAP_TABLE);
    }

    @Override
    public void action(ContextMenuEvent event) {
        List<HttpRequestResponse> httpRequestResponses = event.selectedRequestResponses();
        List<Tuple<Request, Response>> requestResponses =
            httpRequestResponses.stream().map(x -> new Tuple<>(Request.of(x.request()), x.response() != null ?
                Response.of(x.response()) : null)).toList();
        FuzzDict fuzzDict = FuzzDict.of(requestResponses);
        File fuzzDictFile = new File(Constants.EXTRACT_INFO_FILE_DIR + File.separator + fuzzDict.getHost() + ".yaml");
        if (fuzzDictFile.isFile() && fuzzDictFile.exists()) {
            try {
                FuzzDict originFuzzDict = YamlParser.fromYamlStr(Files.readString(Paths.get(fuzzDictFile.getPath())),
                    FuzzDict.class);
                fuzzDict.merge(originFuzzDict);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (FileWriter writer = new FileWriter(fuzzDictFile.getPath())) {
            writer.write(YamlParser.toYamlStr(fuzzDict));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.debug("extract fuzzDict success.");
    }
}
