package ecoflow.plugin

import ecoflow.conf.NfConfObserverFactory
import ecoflow.conf.NfConfObserver
import spock.lang.Specification

class NfConfObserverTest extends Specification {

    def 'should create observer'() {
        given:
        def factory = new NfConfObserverFactory()

        expect:
        factory != null
    }
}
