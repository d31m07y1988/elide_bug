package com.example.demo;

import com.yahoo.elide.Elide;

public interface CallElide {
    String call(final Elide elide, final String path);
}
