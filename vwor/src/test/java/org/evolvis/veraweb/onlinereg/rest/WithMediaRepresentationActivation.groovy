package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.MediaRepresentativeActivation

/**
 * Created by Atanas Alexandrov <sirakov@gmail.com> on 13.05.18.
 */
trait WithMediaRepresentationActivation {
    MediaRepresentativeActivation createActiveActivation() {
        return new MediaRepresentativeActivation(
            activation_token: 'activationToken',
            email: 'email',
            fk_event: 1,
            gender: 'gender',
            address: 'address',
            city: 'city',
            country: 'country',
            firstname: 'firstname',
            lastname: 'lastname',
            zip: 53123,
            activated: 1
        )
    }

    MediaRepresentativeActivation createDisabledActivation() {
        return new MediaRepresentativeActivation(
                activation_token: 'activationToken',
                email: 'email',
                fk_event: 1,
                gender: 'gender',
                address: 'address',
                city: 'city',
                country: 'country',
                firstname: 'firstname',
                lastname: 'lastname',
                zip: 53123,
                activated: 0
        )
    }
}
