package org.katia.managers;

import lombok.Getter;
import org.katia.Logger;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.List;


public class LuaConsole extends LuaFunction {

    @Getter
    static LuaConsole instance = new LuaConsole();

    List<String> logs;

    /**
     * Lua console constructor.
     */
    public LuaConsole() {
        Logger.log(Logger.Type.INFO, "Creating lua console!");
        logs = new ArrayList<>();
    }

    @Override
    public LuaValue call(LuaValue luaValue) {
        StringBuilder message = new StringBuilder();
        message.append(luaValue.tojstring()).append(" ");
        logs.add(message.toString());
        Logger.log(Logger.Type.LUA, message.toString());
        return LuaValue.NIL;
    }

    @Override
    public LuaValue call(LuaValue luaValue, LuaValue luaValue1) {
        StringBuilder message = new StringBuilder();
        message.append(luaValue.tojstring())
                .append(" ")
                .append(luaValue1.tojstring())
                .append(" ");
        logs.add(message.toString());
        Logger.log(Logger.Type.LUA, message.toString());
        return LuaValue.NIL;
    }

    @Override
    public LuaValue call(LuaValue luaValue, LuaValue luaValue1, LuaValue luaValue2) {
        StringBuilder message = new StringBuilder();
        message.append(luaValue.tojstring())
                .append(" ")
                .append(luaValue1.tojstring())
                .append(" ")
                .append(luaValue2.tojstring())
                .append(" ");
        logs.add(message.toString());
        Logger.log(Logger.Type.LUA, message.toString());
        return LuaValue.NIL;
    }

    @Override
    public Varargs invoke(Varargs args) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i <= args.narg(); i++) {
            message.append(args.arg(i).tojstring()).append(" ");
        }
        logs.add(message.toString());
        Logger.log(Logger.Type.LUA, message.toString());
        return LuaValue.NIL;
    }
}
