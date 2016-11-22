package nl.han.asd.project.client.commonclient.store;

import nl.han.asd.project.client.commonclient.persistence.IPersistence;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;

public class ScriptStoreTest {

    private IPersistence persistence;
    private IScriptStore scriptStore;

    @Before
    public void initialize() {
        persistence = Mockito.mock(IPersistence.class);
        scriptStore = new ScriptStore(persistence);
    }

    @Test
    public void addScript() {
        String scriptName = "script1";

        Mockito.when(persistence.addScript(any(String.class))).thenReturn(true);

        scriptStore.addScript(scriptName);

        Mockito.verify(persistence, Mockito.times(1)).addScript(any(String.class));
    }

    @Test
    public void removeScript() {
        String scriptName = "script1";

        Mockito.when(persistence.deleteScript(any(String.class))).thenReturn(true);

        scriptStore.removeScript(scriptName);

        Mockito.verify(persistence, Mockito.times(1)).deleteScript(any(String.class));
    }

    @Test
    public void getAllScriptNames() {
        String scriptName = "script1";

        List<String> scriptNames = new ArrayList<>();
        scriptNames.add(scriptName);

        Mockito.when(persistence.getAllScriptNames()).thenReturn(scriptNames);

        scriptStore.getAllScriptNames();

        Mockito.verify(persistence, Mockito.times(1)).getAllScriptNames();
    }

    @Test
    public void getScriptContent() {
        String scriptName = "script1";
        String scriptContent = "segment data begin text bericht value \"Dit is een bericht\" segment end segment main begin print bericht segment end";

        Mockito.when(persistence.getScriptContent(any(String.class))).thenReturn(scriptContent);

        scriptStore.getScriptContent(scriptName);

        Mockito.verify(persistence, Mockito.times(1)).getScriptContent(any(String.class));
    }

    @Test
    public void updateScript() {
        String scriptName = "script1";
        String scriptContent = "segment data begin text bericht value \"Dit is een bericht\" segment end segment main begin print bericht segment end";

        Mockito.doNothing().when(persistence).updateScript(any(String.class), any(String.class));

        scriptStore.updateScript(scriptName, scriptContent);

        Mockito.verify(persistence, Mockito.times(1)).updateScript(any(String.class), any(String.class));
    }
}
