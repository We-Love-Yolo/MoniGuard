package com.weloveyolo.moniguard.api;

public interface ICallback<T> {
    void onCallback(T result, boolean success);
}
