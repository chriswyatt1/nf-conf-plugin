package ecoflow.conf

import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.trace.TraceObserver

@Slf4j
class NfConfObserver implements TraceObserver {

    private Session session

    NfConfObserver(Session session) {
        this.session = session
    }

    @Override
    void onFlowBegin() {
        log.info "nf-conf: Generating configuration report..."
        generateReport()
    }

    void generateReport() {
        try {
            def config = session.config
            def outdir = session.config.navigate('params.outdir')?.toString() ?: '.'
            def reportFile = new File("${outdir}/pipeline_info/config_report.html")
            reportFile.parentFile.mkdirs()
            def configFiles = session.configFiles ?: []
            def processConfig = config.navigate('process') as Map ?: [:]
            def params = config.navigate('params') as Map ?: [:]
            def html = buildHtmlReport(configFiles, processConfig, params)
            reportFile.text = html
            log.info "nf-conf: Config report written to ${reportFile}"
            println "nf-conf: Config report written to --> ${reportFile}"
        } catch (Exception e) {
            log.error "nf-conf: Failed to generate config report: ${e.message}"
            e.printStackTrace()
        }
    }

    String buildHtmlReport(List configFiles, Map processConfig, Map params) {
        def sb = new StringBuilder()

        sb << """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Nextflow Config Report</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                h1 { color: #2c3e50; }
                h2 { color: #34495e; border-bottom: 1px solid #ccc; padding-bottom: 5px; }
                h3 { color: #7f8c8d; }
                table { border-collapse: collapse; width: 100%; margin-bottom: 30px; }
                th { background-color: #3498db; color: white; padding: 8px 12px; text-align: left; }
                td { padding: 6px 12px; border-bottom: 1px solid #ddd; font-family: monospace; }
                tr:nth-child(even) { background-color: #f2f2f2; }
                .section { margin-bottom: 40px; }
                code { background: #ecf0f1; padding: 2px 5px; border-radius: 3px; }
            </style>
        </head>
        <body>
        <h1>Nextflow Configuration Report</h1>
        <p>Generated: ${new Date()}</p>
        """

        // Config files loaded
        sb << "<div class='section'>"
        sb << "<h2>Config Files Loaded (in order)</h2>"
        sb << "<table><tr><th>#</th><th>File</th></tr>"
        configFiles.eachWithIndex { f, i ->
            sb << "<tr><td>${i+1}</td><td>${f}</td></tr>"
        }
        sb << "</table></div>"

        // Global params
        sb << "<div class='section'>"
        sb << "<h2>Parameters</h2>"
        sb << "<table><tr><th>Parameter</th><th>Value</th></tr>"
        params.sort().each { k, v ->
            def safeVal = v instanceof Map ? '{...}' : v?.toString() ?: 'null'
            sb << "<tr><td><code>--${k}</code></td><td>${safeVal}</td></tr>"
        }
        sb << "</table></div>"

        // Global process defaults
        sb << "<div class='section'>"
        sb << "<h2>Process Configuration</h2>"
        sb << "<h3>Global defaults</h3>"
        sb << "<table><tr><th>Setting</th><th>Value</th></tr>"
        processConfig.each { k, v ->
            if (k != 'withName' && k != 'withLabel') {
                def safeVal = v instanceof Map ? '{...}' : v?.toString() ?: 'null'
                sb << "<tr><td><code>${k}</code></td><td>${safeVal}</td></tr>"
            }
        }
        sb << "</table>"

        // withLabel overrides
        def withLabel = processConfig.get('withLabel') as Map ?: [:]
        if (withLabel) {
            sb << "<h3>Label overrides (withLabel)</h3>"
            sb << "<table><tr><th>Label</th><th>Setting</th><th>Value</th></tr>"
            withLabel.each { label, settings ->
                if (settings instanceof Map) {
                    settings.each { k, v ->
                        def safeVal = v instanceof Map ? '{...}' : v?.toString() ?: 'null'
                        sb << "<tr><td><code>${label}</code></td><td><code>${k}</code></td><td>${safeVal}</td></tr>"
                    }
                }
            }
            sb << "</table>"
        }

        // withName overrides
        def withName = processConfig.get('withName') as Map ?: [:]
        if (withName) {
            sb << "<h3>Per-process overrides (withName)</h3>"
            sb << "<table><tr><th>Process</th><th>Setting</th><th>Value</th></tr>"
            withName.each { procName, settings ->
                if (settings instanceof Map) {
                    settings.each { k, v ->
                        def safeVal = v instanceof Map ? '{...}' : v?.toString() ?: 'null'
                        sb << "<tr><td><code>${procName}</code></td><td><code>${k}</code></td><td>${safeVal}</td></tr>"
                    }
                }
            }
            sb << "</table>"
        }

        sb << "</div>"
        sb << "</body></html>"
        return sb.toString()
    }
}
