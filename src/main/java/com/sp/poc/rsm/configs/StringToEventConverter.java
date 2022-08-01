package com.sp.poc.rsm.configs;

import com.sp.poc.rsm.enums.Event;
import org.springframework.core.convert.converter.Converter;

public class StringToEventConverter implements Converter<String, Event> {
    @Override
    public Event convert(String source) {
        try {
            return Event.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
