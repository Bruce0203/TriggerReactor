/*
 * Copyright (C) 2022. TriggerReactor Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package js;

import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import io.github.wysohn.triggerreactor.core.manager.evaluable.JSExecutor;
import io.github.wysohn.triggerreactor.core.manager.js.executor.IJSExecutorFactory;
import io.github.wysohn.triggerreactor.tools.timings.Timings;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExecutorTest extends JsTest {
    public static final Map<String, Boolean> coverage = new TreeMap<>();
    private JSExecutor executor;

    public ExecutorTest(ScriptEngine engine, String name, String... directories) throws ScriptException, IOException {
        super(engine, name, "Executor", directories);
    }

    private JSExecutor getOrCreate() {
        if (executor == null) {
            List<Module> modules = new ArrayList<>(super.modules);
            modules.add(new FactoryModuleBuilder()
                    .implement(JSExecutor.class, JSExecutor.class)
                    .build(IJSExecutorFactory.class));
            executor = Guice.createInjector(modules)
                    .getInstance(IJSExecutorFactory.class)
                    .create(name, engine, stream);
        }
        return executor;
    }

    @Override
    public Object test() throws Exception {
        coverage.put(this.name, true);
        getOrCreate().evaluate(Timings.LIMBO, varMap, null, args);
        return null;
    }

    @Override
    public int getOverload(Object... args) {
        return getOrCreate().validate(args).getOverload();
    }
}