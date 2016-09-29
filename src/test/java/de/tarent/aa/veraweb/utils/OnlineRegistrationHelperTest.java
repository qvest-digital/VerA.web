package de.tarent.aa.veraweb.utils;

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
