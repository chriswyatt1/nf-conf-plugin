# nf-conf-plugin
A plugin to monitor configuration in nextflow

Work in progress. The idea with this plugin, is that we will take all the config files within a nextflow run, and output a list of where each module and golbally settings are being applied. So we can demystify the configuration, and give a quick check list to work out where best to change the configuration. 

Version 1 (current), only output the global settings from the nextflow.config that are used, or null if not. 
