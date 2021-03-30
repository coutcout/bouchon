package me.vcouturier.bouchon.regex.service.impl;

import lombok.extern.log4j.Log4j2;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.regex.service.RegexService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Component
public class RegexServiceImpl implements RegexService {
    @Override
    public List<String> getRegexFromString(String string) {
        Pattern p = Pattern.compile("\\{.*?}");
        Matcher m = p.matcher(string);
        return m.results()
                .map(matchResult -> {
                        String result = matchResult.group();
                        if(log.isDebugEnabled()){
                            log.debug(MessageFormat.format(MessageEnum.DEBUG_REGEX_EXTRACTION_FOUND.getCode(), result));
                        }

                        return matchResult.group().substring(1, result.length()-1);
                })
                .collect(Collectors.toList());
    }
}
