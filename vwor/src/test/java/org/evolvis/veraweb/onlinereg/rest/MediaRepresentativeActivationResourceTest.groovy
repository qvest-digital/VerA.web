package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.MediaRepresentativeActivation
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext

/**
 * Created by Atanas Alexandrov on 03.05.18.
 */
class MediaRepresentativeActivationResourceTest extends Specification implements WithMediaRepresentationActivation {

    MediaRepresentativeActivationResource subject
    SessionFactory sessionFactory
    Session session
    ServletContext context
    Transaction transaction

    def activationToken = 'activationToken'
    def email = 'email'
    def eventId = 1
    def gender = 'gender'
    def address = 'address'
    def city = 'city'
    def country = 'country'
    def firstname = 'firstname'
    def lastname = 'lastname'
    def zip = 53123

    def setup() {
        context = Mock(ServletContext)
        transaction = Mock(Transaction)
        sessionFactory = Mock(SessionFactory)
        session = Mock(Session)
        subject = new MediaRepresentativeActivationResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
    }

    def 'add media representative activation entry'() {
        when: 'add activation'
           subject.addMediaRepresentativeActivationEntry(activationToken, email, eventId, gender, address, city, country, firstname, lastname, zip)

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'transaction exists'
            1 * session.getTransaction() >> transaction
        and: 'transaction is started'
            1 * session.beginTransaction()
        and: 'transaction.commit is executed'
            1 * transaction.commit()
        and: 'session is closed'
            1 * session.close()
    }

    @Unroll
    def 'returns true if activation exists, otherwise false'() {
        given: 'mocked query'
            Query query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.getEntryByEmailAndEventId") >> query
        and: 'result from the query'
            query.uniqueResult() >> numberOfActivations

        when: 'invoke method to check, if activation exists'
            boolean activationExists = subject.existEventIdByDelegation('encodedAddress', '1')

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'session is closed'
            1 * session.close()
        and: 'activation exists == #expectedResult'
            activationExists == expectedResult

        where:
            numberOfActivations | expectedResult
            new BigInteger(0)   | false
            new BigInteger(1)   | true
            new BigInteger(2)   | true
            null                | false

    }

    @Unroll
    def 'get activation by token'() {
        given: 'mocked query'
            Query query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.getActivationByActivationToken") >> query
        and: 'result from the query'
            query.uniqueResult() >> stubbedActivation

        when: 'invoke method to get activation by token'
            MediaRepresentativeActivation activation = subject.getMediaRepresentativeActivationByToken('token')

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'session is closed'
            1 * session.close()
        and: 'activation status is correct'
            activation.activated == activationStatus

        where:
            stubbedActivation          | activationStatus
            createActiveActivation()   | 1
            createDisabledActivation() | 0
    }

    def 'activate press user'() {
        given: 'mocked query'
            Query query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.activate") >> query

        when: 'invoke method to activate user'
            subject.activatePressUser('email@email.com', 1)

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'transaction exists'
            1 * session.getTransaction() >> transaction
        and: 'transaction is started'
            1 * session.beginTransaction()
        and: 'transaction.commit is executed'
            1 * transaction.commit()
        and: 'session is closed'
            1 * session.close()
    }

}
