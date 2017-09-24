package cr.ac.ucr.ecci.ci1330.IoC;

import java.util.List;

/**
 * Created by majo_ on 22/9/2017.
 */
public class Bean {
    private String id;
    private String className;
    private String initMethod;
    private String destructMethod;
    private Object beanInstance;
    private AutowiringMode autowiringMode;
    private ScopeType scopeType;




    public Bean(String id, String className, String initMethod, String destructMethod, Object beanInstance, AutowiringMode autowiringMode, ScopeType scopeType) {
        this.id = id;
        this.className = className;
        this.initMethod = initMethod;
        this.destructMethod = destructMethod;
        this.beanInstance = beanInstance;
        this.autowiringMode = autowiringMode;
        this.scopeType = scopeType;
    }

    public Bean(){
        this.initMethod = "initMethod";
        this.destructMethod = "destructMethod";
        this.autowiringMode = AutowiringMode.BYTYPE;
        this.scopeType = ScopeType.SINGLETON;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public String getDestructMethod() {
        return destructMethod;
    }

    public void setDestructMethod(String destructMethod) {
        this.destructMethod = destructMethod;
    }

    public Object getBeanInstance() { return beanInstance; }

    public void setBeanInstance(Object beanInstance) {
        this.beanInstance = beanInstance;
    }

    public AutowiringMode getAutowiringMode() { return autowiringMode; }

    public void setAutowiringMode(AutowiringMode autowiringMode) { this.autowiringMode = autowiringMode; }

    public ScopeType getScopeType() { return scopeType; }

    public void setScopeType(ScopeType scopeType) { this.scopeType = scopeType; }



}
