package com.kbhkn.dynamicjaxwsbeangenerator.autoconfigurer;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Auto-Configurations finds this class then loads this module automatically.
 *
 * @author Hakan KABASAKAL, 08-Aug-21
 */
@Configuration
@ComponentScan(basePackages = "com.kbhkn.dynamicjaxwsbeangenerator")
public class ModuleLoader {
}
