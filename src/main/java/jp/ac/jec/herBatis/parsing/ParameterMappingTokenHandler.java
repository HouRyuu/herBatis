package jp.ac.jec.herBatis.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * HeBatis SQLの引き数名を保管する
 */
public class ParameterMappingTokenHandler implements TokenHandler {

    private List<ParameterMapping> parameterMappings = new ArrayList<>();

    @Override
    public String handleToken(String content) {
        parameterMappings.add(new ParameterMapping(content));
        return "?";
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }
}
