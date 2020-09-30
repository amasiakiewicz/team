package com.casinoroyale.team.infrastructure;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.List;
import java.util.Map;

import me.alidg.errors.Argument;
import me.alidg.errors.HandledException;
import me.alidg.errors.WebErrorHandler;
import org.springframework.http.HttpStatus;

class IllegalExceptionHandler implements WebErrorHandler {

    @Override
    public boolean canHandle(final Throwable exception) {
        return exception instanceof IllegalArgumentException || exception instanceof IllegalStateException;
    }

    @Override
    public HandledException handle(final Throwable exception) {
        final String errorCode = "validation.error";
        final List<Argument> arguments = getArguments(exception);
        final Map<String, List<Argument>> argumentsMap = singletonMap(errorCode, arguments);

        return new HandledException(errorCode, HttpStatus.BAD_REQUEST, argumentsMap);
    }

    private List<Argument> getArguments(final Throwable exception) {
        final String exceptionMessage = defaultString(exception.getMessage());
        final Argument argument = Argument.arg("msg", exceptionMessage);

        return singletonList(argument);
    }

}
