package org.katia.scripting;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.katia.Logger;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for storing logs from lua scripts with <code>print</code> function.
 * @see LuaScriptExecutioner
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LuaConsole extends LuaFunction {

    private final List<String> logs;

    /**
     * Lua console constructor.
     */
    public LuaConsole() {
        Logger.log(Logger.Type.INFO, "Creating lua console ...");
        logs = new ArrayList<>();
    }

    /**
     * Override print function in lua that accepts 1 function argument.
     * @param luaValue Argument 1.
     * @return LuaValue
     */
    @Override
    public LuaValue call(LuaValue luaValue) {
        StringBuilder message = new StringBuilder();
        message.append(luaValue.tojstring()).append(" ");
        logs.add(message.toString());
        Logger.log(Logger.Type.LUA, message.toString());
        return LuaValue.NIL;
    }

    /**
     * Override print function in lua that accepts 2 function arguments.
     * @param luaValue Argument 1.
     * @param luaValue1 Argument 2.
     * @return LuaValue
     */
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

    /**
     * Override print function in lua that accepts 3 function arguments.
     * @param luaValue Argument 1.
     * @param luaValue1 Argument 2.
     * @param luaValue2 Argument 3.
     * @return LuaValue
     */
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

    /**
     * Override print function in lua that accepts more than 3 function arguments.
     * @param args Arguments.
     * @return Varargs
     */
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
