package ecoflow.conf

import groovy.transform.CompileStatic
import nextflow.plugin.BasePlugin
import org.pf4j.PluginWrapper

@CompileStatic
class NfConfPlugin extends BasePlugin {

    NfConfPlugin(PluginWrapper wrapper) {
        super(wrapper)
    }
}