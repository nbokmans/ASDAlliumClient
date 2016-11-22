package nl.han.asd.project.client.commonclient.store;

import java.util.List;

/**
 * Scriptstore provides functionality to read from and write to persistence.
 * The persistence class is used to achieve this.
 *
 * @version 1.0
 */
public interface IScriptStore {

    /**
     * Adds a new script into the scriptstore.
     *
     * @param scriptName name of the to be created script.
     */
    void addScript(String scriptName);

    /**
     * Removes a script from the scriptStore.
     *
     * @param scriptName name of the script that will be deleted.
     */
    void removeScript(String scriptName);

    /**
     * Gets a list of all scriptnames.
     *
     * @return <tt>List<String></tt>.
     */
    List<String> getAllScriptNames();

    /**
     * Gets the content of a script
     *
     * @param scriptName name of the script of which the content will be fetched.
     * @return <tt>String</tt> containing the content of a script
     */
    String getScriptContent(String scriptName);

    /**
     * Updates the content of a script
     *
     * @param scriptName name of the script to be updated.
     * @param scriptContent the content of the script that will be replaced.
     */
    void updateScript(String scriptName, String scriptContent);
}
