package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import de.tarent.aa.veraweb.worker.ActionWorker;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.server.OctopusContext;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OnlineRegistrationHelperTest {

    private OctopusContext prepare(final String text) {
        final OctopusContext context = mock(OctopusContext.class);
        final TcModuleConfig moduleConfig = mock(TcModuleConfig.class);
        when(context.moduleConfig()).thenReturn(moduleConfig);
        when(moduleConfig.getParam(ActionWorker.ONLINEREG_MANDANT_DEACTIVATION)).thenReturn(text);
        return context;
    }

    @Test
    public void getDeactivatedMandantsAsArrayEmptyString() {
        final OctopusContext context = prepare(" ");
        final int[] result = OnlineRegistrationHelper.getDeactivatedMandantsAsArray(context);

        assertThat(result.length, equalTo(1));
    }

    @Test
    public void getDeactivatedMandantsAsArrayTwoEntries() {
        final OctopusContext context = prepare("1,2");
        final int[] result = OnlineRegistrationHelper.getDeactivatedMandantsAsArray(context);

        assertThat(result.length, equalTo(2));
    }

    @Test
    public void getDeactivatedMandantsAsArrayNotNumber() {
        final OctopusContext context = prepare("1,B,4");
        final int[] result = OnlineRegistrationHelper.getDeactivatedMandantsAsArray(context);

        assertThat(result.length, equalTo(3));
    }
}
