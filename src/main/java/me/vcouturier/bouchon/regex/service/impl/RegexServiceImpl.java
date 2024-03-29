package me.vcouturier.bouchon.regex.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.regex.model.ITypeRegex;
import me.vcouturier.bouchon.regex.service.RegexService;
import me.vcouturier.bouchon.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RegexServiceImpl implements RegexService {

    @Autowired
    private MessageService messageService;

    @Override
    public List<String> getRegexNameFromString(String string) {
        Pattern p = Pattern.compile("\\{.*?}");
        Matcher m = p.matcher(string);
        return m.results()
                .map(matchResult -> {
                        String result = matchResult.group();
                        if(log.isDebugEnabled()){
                            log.debug(messageService.formatMessage(MessageEnum.DEBUG_REGEX_EXTRACTION_FOUND, result));
                        }

                        return matchResult.group().substring(1, result.length()-1);
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getRegexFormatedString(String string, Map<String, ITypeRegex> parameters) {
        String newRegex = string;
        for(Map.Entry<String, ITypeRegex> param : parameters.entrySet()){
            String toReplace = "\\{" + param.getKey() + "}";
            String replaceWith = "(?<" + param.getKey() + ">" + param.getValue().getRegex() + ")";
            log.debug(messageService.formatMessage(MessageEnum.DEBUG_REGEX_REPLACING_VALUE, toReplace, replaceWith, string));
            newRegex = newRegex.replaceAll(toReplace, replaceWith);
        }
        return newRegex;
    }
}
