package com.herry.server.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    private Set<String> members;
    public static final Group EMPTY_GROUP = new Group();
}
