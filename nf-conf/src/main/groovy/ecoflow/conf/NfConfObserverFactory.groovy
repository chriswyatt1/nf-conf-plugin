package ecoflow.conf

import groovy.transform.CompileStatic
import nextflow.Session
import nextflow.trace.TraceObserver
import nextflow.trace.TraceObserverFactory

@CompileStatic
class NfConfObserverFactory implements TraceObserverFactory {

    @Override
    Collection<TraceObserver> create(Session session) {
        return [ new NfConfObserver(session) ]
    }
}