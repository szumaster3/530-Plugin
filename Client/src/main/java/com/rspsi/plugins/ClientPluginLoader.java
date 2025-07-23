package com.rspsi.plugins;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientPluginLoader {

	private static int count = 0;

	private static ServiceLoader<ClientPlugin> serviceLoader;

	public static ServiceLoader<ClientPlugin> getServiceLoader(){
		if(serviceLoader == null)
			loadPlugins();
		return serviceLoader;
	}



    public static void loadPlugins() {
        File pluginPath = new File("plugins" + File.separator + "active");

        if (!pluginPath.exists() || !pluginPath.isDirectory()) {
            log.warn("Plugin directory does not exist or is not a directory: {}", pluginPath.getAbsolutePath());
            return;
        }

        File[] plugins = pluginPath.listFiles((File dir, String name) -> name.endsWith(".jar"));

        if (plugins == null || plugins.length == 0) {
            log.warn("No plugins found in directory: {}", pluginPath.getAbsolutePath());
            return;
        }

        List<URL> urls = Lists.newArrayList();
        for (File pluginFile : plugins) {
            try {
                URL url = pluginFile.toURI().toURL();
                urls.add(url);
                log.info("Added {} to plugin URL", url.toString());
            } catch (MalformedURLException e) {
                log.error("Malformed URL for file: {}", pluginFile.getAbsolutePath(), e);
            }
        }

        URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());

        serviceLoader = ServiceLoader.load(ClientPlugin.class, urlClassLoader);
        forEach(plugin -> {
            plugin.initializePlugin();
            count++;
        });
        log.info("Loaded {} client plugins!", count);

        try {
            urlClassLoader.close();
        } catch (IOException e) {
            log.error("Error closing URLClassLoader", e);
        }
    }

	public static void forEach(Consumer<ClientPlugin> consumer) {
		ServiceLoader<ClientPlugin> serviceLoader = getServiceLoader();

		for(ClientPlugin plugin : serviceLoader) {
			consumer.accept(plugin);
		}

	}
}
