package fingerprintmodel.wiseasyfpmodel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import fingerprintmodel.Param;

@Root(name = "additional_info")
public class Aratek_additional_info {

    @ElementList(name = "Param", required = false, inline = true)
    public List<Param> params;
}
