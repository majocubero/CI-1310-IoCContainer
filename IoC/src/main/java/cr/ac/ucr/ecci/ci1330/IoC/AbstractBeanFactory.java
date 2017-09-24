package cr.ac.ucr.ecci.ci1330.IoC;

import nu.xom.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by majo_ on 22/9/2017.
 */
public class AbstractBeanFactory implements BeanFactoryContainer {

    protected HashMap<String, Bean> beanHashMap;

    public AbstractBeanFactory() {
        this.beanHashMap = new HashMap<>();
    }

    public HashMap<String, Bean> getBeanHashMap() {
        return beanHashMap;
    }

    public Bean createBean(HashMap<String, Object> beanInformation) {
        Bean bean = null;
        Class definedClass = null;
        try {
            definedClass = Class.forName((String) beanInformation.get("className"));
        } catch (ClassNotFoundException e) {
            System.out.println("La clase no existe");
        }
        if (definedClass != null) {
            bean = new Bean();
            bean.setId((String) beanInformation.get("id"));
            bean.setClassName((String) beanInformation.get("className"));
            if (beanInformation.containsKey("initMethod")) {
                bean.setDestructMethod((String) beanInformation.get("initMethod"));
            }
            if (beanInformation.containsKey("destructMethod")) {
                bean.setDestructMethod((String) beanInformation.get("destructMethod"));
            }
            if (beanInformation.containsKey("autowiringMode")) {
                bean.setAutowiringMode(AutowiringMode.valueOf((String) beanInformation.get("autowiringMode")));
            }
            if (beanInformation.containsKey("scopeType")) {
                bean.setScopeType(ScopeType.valueOf((String) beanInformation.get("scopeType")));
            }
            bean.setBeanInstance(beanInformation.get("beanInstance"));
            if (!beanHashMap.containsKey(beanInformation.get("id")) && bean.getBeanInstance() != null) {
                System.out.println("agrego " + bean.getClassName());
                beanHashMap.put((String) beanInformation.get("id"), bean);
            }
            executeBeanInstanceMethod(bean, bean.getInitMethod());
        }
        return bean;
    }

    protected Object injectSetterDependencies(Class newClass, String setterName, Object parameter) {
        Object objectInstance = null;
        try {
            //ver que pasa si el constructor tiene parametros
            objectInstance = newClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Method[] methods = null;
        try {
            methods = newClass.getMethods();
        } catch (Exception e) {
            System.out.println("No se pudo recuperar el método: " + setterName);
            e.printStackTrace();
        }

        try {
            obtainMethodtoInvoke(methods, setterName).invoke(objectInstance, parameter);
        } catch (Exception e) {
            System.out.println("falla el invoke y no mete bien el parámetro del setter. M: createBeanInstance");
        }
        return objectInstance;
    }

    //devuelve la instancia con las dependencias agregadas
    protected Object injectConstructorDependencies(Class newClass, Object[] parameters) {
        Object objectInstance = new Object[]{new Object()};
        Constructor[] constructors = newClass.getConstructors();
        Constructor constructor = null;
        int constructorPosition = lookForConstructor(constructors, parameters);
        if(constructorPosition== -1){
            System.out.println("El constructor de la clase: "+newClass.getName()+" no hace match con los parámetros brindados.");
            return null;
        }
        constructor = constructors[constructorPosition];
        try {
            objectInstance = constructor.newInstance(parameters);
        } catch (NullPointerException e) {
            System.out.println("no se creó bien la instancia con el constructor. M: injectconstDep");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return objectInstance;
    }

    protected int lookForConstructor(Constructor[] constructors, Object[] parameters) {
        boolean found = false;
        boolean equal = true;
        int i = 0;
        int j;
        while (!found && i < constructors.length) {
            Class[] parameterTypes = constructors[i].getParameterTypes();
            if (constructors[i].getParameterCount() == parameters.length) {
                j = 0;
                equal = true;
                while (equal && j < parameters.length) {
                    String parameter = parameters[j].getClass().toString();
                    if (parameter.equals(parameterTypes[j].toString())) {
                        if (j == parameters.length - 1) {
                            found = true;
                        }
                    } else {
                        equal = false;
                    }
                    j++;
                }
            }
            i++;
        }
        if (!found) {
            return -1;
        }
        return i - 1;
    }

    //determina cual es el método que se va a invocar a partit del nombre del mismo
    protected Method obtainMethodtoInvoke(Method[] methods, String setterName) {
        boolean found = false;
        int i = 0;
        while (!found && i < methods.length) {
            if (methods[i].getName().equals(setterName)) {
                found = true;
            } else {
                i++;
            }
        }
        return methods[i];
    }


    public Object getBean(String id) {
        return null;
    }


    public void destroyBean(String id) {

    }

    public Bean findBean(String id) {
        return null;
    }

    public void executeBeanInstanceMethod(Bean bean, String methodName) {
        Class instance = null;
        try {
            instance = Bean.class.getDeclaredField("beanInstance").getType(); // Recupera el tipo de la instancia del Bean
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Method method = null;
        try {
            method = instance.getDeclaredMethod(methodName); // Recupera el metodo recibido por parametro
        } catch (NoSuchMethodException e) {
            // Si la instancia no tiene el metodo respectivo entonces no hace nada
        }

        if (method != null) { // Si existe el metodo respectivo
            try {
                method.invoke(bean.getBeanInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
