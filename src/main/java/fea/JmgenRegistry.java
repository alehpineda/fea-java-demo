package fea;

import gener.connect;
import gener.copy;
import gener.genquad8;
import gener.readmesh;
import gener.rectangle;
import gener.sweep;
import gener.transform;
import gener.writemesh;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Explicit registry for mesh generator commands.
public final class JmgenRegistry {

    private static final Map<String, Runnable> COMMANDS = createCommands();

    private JmgenRegistry() {
    }

    private static Map<String, Runnable> createCommands() {
        Map<String, Runnable> commands = new LinkedHashMap<>();
        commands.put("genquad8", () -> { new genquad8(); });
        commands.put("connect", () -> { new connect(); });
        commands.put("copy", () -> { new copy(); });
        commands.put("readmesh", () -> { new readmesh(); });
        commands.put("rectangle", () -> { new rectangle(); });
        commands.put("sweep", () -> { new sweep(); });
        commands.put("transform", () -> { new transform(); });
        commands.put("writemesh", () -> { new writemesh(); });
        return Collections.unmodifiableMap(commands);
    }

    public static Map<String, Runnable> commands() {
        return COMMANDS;
    }

    public static Runnable command(String name) {
        return COMMANDS.get(name);
    }
}
