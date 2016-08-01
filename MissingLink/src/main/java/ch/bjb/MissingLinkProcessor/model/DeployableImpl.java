package ch.bjb.MissingLinkProcessor.model;

import ch.bjb.MissingLinkProcessor.model.Deployable;

import java.util.List;

/**
 * Created by u36342 on 4/10/16.
 */
public class DeployableImpl extends Deployable {
    public DeployableImpl(int id, String groupid, String tag, String file, String name, String version, Boolean scanPlaceholders, List<Integer> configurationset,String extraSteps) {
        super(id, groupid, tag, file, name, version, scanPlaceholders, configurationset, extraSteps);
    }

    public DeployableImpl(int id, String groupid, String file, String name, String version, List<Integer> configurationset) {
        super(id, groupid, file, name, version,configurationset);
    }

    @Override
    public String getType() {
        return Deployable.class.getTypeName();

    }

    @Override
    public String toString() {
        return super.toString();
    }
}
