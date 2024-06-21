package org.m2sec.modules.bypass.intruder;

import burp.api.montoya.http.HttpService;
import burp.api.montoya.intruder.AttackConfiguration;
import org.m2sec.burp.intruder.AbstractPayloadGeneratorProvider;
import org.m2sec.common.Constants;
import org.m2sec.common.Render;
import org.m2sec.common.utils.BurpUtil;
import org.m2sec.common.utils.FileUtil;
import org.m2sec.common.utils.HttpUtil;

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
public class BypassUrlGeneratorProviderProvider extends AbstractPayloadGeneratorProvider {

    @Override
    public String displayName() {
        return "BypassUrl";
    }

    @Override
    public Set<String> generatePayloadSet(AttackConfiguration attackConfiguration) {
        byte[] message = attackConfiguration.requestTemplate().content().getBytes();
        String selectUrl = BurpUtil.getIntruderWrappedText(message);
        try {
            if (attackConfiguration.httpService().isPresent()) {
                HttpService httpService = attackConfiguration.httpService().get();
                HashMap<String, Object> env = new HashMap<>();
                env.put("originUrl", new URL(HttpUtil.getDomainUrl(httpService.secure(), httpService.host(),
                    httpService.port())));
                env.put("evilUrl", new URL(selectUrl));
                return FileUtil.readFileAsStringArray(Constants.BYPASS_URL_DICT_FILE_PATH).stream().map(x -> Render.renderTemplate(x, env)).collect(Collectors.toSet());
            } else {
                throw new RuntimeException("please input target.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("select text is incorrect url.");
        }
    }
}
