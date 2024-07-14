package org.m2sec.abilities.payloads;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.intruder.AttackConfiguration;
import org.m2sec.core.common.BurpTools;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.FileTools;
import org.m2sec.core.common.Render;
import org.m2sec.core.utils.HttpUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class BypassUrlProviderProvider extends IPayloadProvider {


    public BypassUrlProviderProvider(MontoyaApi api) {
        super(api);
    }

    @Override
    public String displayName() {
        return "Bypass Host Check";
    }

    @Override
    public Set<String> generatePayloadSet(AttackConfiguration attackConfiguration) {
        byte[] message = attackConfiguration.requestTemplate().content().getBytes();
        String selectUrl = BurpTools.getIntruderWrappedText(message);
        try {
            if (attackConfiguration.httpService().isPresent()) {
                HttpService httpService = attackConfiguration.httpService().get();
                HashMap<String, Object> env = new HashMap<>();
                env.put("originUrl", new URL(HttpUtil.getDomainUrl(httpService.secure(), httpService.host(),
                    httpService.port())));
                env.put("evilUrl", new URL(selectUrl));
                return FileTools.readFileAsStringArray(Constants.BYPASS_HOST_CHECK_TEMPLATE_FILE_PATH).stream().map(x -> Render.renderTemplate(x,
                    env)).collect(Collectors.toSet());
            } else {
                throw new RuntimeException("please input target.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("select text is incorrect url.");
        }
    }
}
