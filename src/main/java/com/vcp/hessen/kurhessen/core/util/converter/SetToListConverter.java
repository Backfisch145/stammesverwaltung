package com.vcp.hessen.kurhessen.core.util.converter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetToListConverter<T> implements Converter<Set<T>, List<T>> {
    @Override
    public Result<List<T>> convertToModel(Set<T> set, ValueContext valueContext) {
        return Result.ok(new ArrayList<>(set));
    }

    @Override
    public Set<T> convertToPresentation(List<T> list, ValueContext valueContext) {
        return new HashSet<>(list);
    }
}
