package cr.ac.ucr.ecci.ci1330.IoC.XMLBased;

import cr.ac.ucr.ecci.ci1330.IoC.AbstractBeanFactory;
import cr.ac.ucr.ecci.ci1330.IoC.AutowiringMode;
import cr.ac.ucr.ecci.ci1330.IoC.Bean;
import nu.xom.Element;

import java.util.*;

/**
 * Created by majo_ on 22/9/2017.
 */
public class XMLBeanFactory extends AbstractBeanFactory {

    private XMLParser xmlParser;
    protected String path;

    public XMLBeanFactory(String path) {
        this.path = path;
        this.xmlParser = new XMLParser(path, this);
        xmlParser.readXML();
    }

    public Object createBeanInstance(nu.xom.Element beanTag) {
        Object beanInstance = null;
        Class newClass = null;
        try {
            newClass = Class.forName(beanTag.getAttributeValue("className"));
        } catch (ClassNotFoundException e) {
            System.out.println("No se encontró la clase");
            e.printStackTrace();
        }
        //hay que fijarse que el la referencia exista
        if (beanTag.getAttribute("injectConstructor") != null) {
            nu.xom.Elements parameters = beanTag.getChildElements();
            if (parameters.size() == 0) { //si el constructor no tiene parametros
                try {
                    beanInstance = newClass.newInstance();
                } catch (Exception e) {
                    System.out.println("no se creó bien la instancia de la clase");
                }
            } else { //si el constructor tiene parámetros
                beanInstance = injectDependencies(beanTag, newClass, false);
            }
        }
        //verificar que está llamando a un setter
        else if (beanTag.getAttribute("injectSetter") != null) {
            beanInstance = injectDependencies(beanTag, newClass, true);
        }
        // Crea una instancia con un constructor sin parámetros
        else {
            try {
                beanInstance = newClass.newInstance();
            } catch (Exception e) {
                System.out.println("no se creó bien la instancia de la clase");
            }
        }
        return beanInstance;
    }

    public Bean retrieveUncreatedBean(String ref, AutowiringMode autowiringMode){
        Bean foundBean = new Bean();
        HashMap<String, Object> beanAttributes;
        Element foundBeanTag = xmlParser.findBean(ref, autowiringMode);
        beanAttributes = xmlParser.obtainBeanAttributes(foundBeanTag);
        foundBean = createBean(beanAttributes);
        return foundBean;
    }

    private String obtainSetterName(nu.xom.Element setterTag) {
        String fieldName = setterTag.getAttributeValue("name");
        fieldName = fieldName.replace(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
        String setterName = "set" + fieldName;
        return setterName;
    }

    private Object[] obtainParameters(nu.xom.Element beanTag) {
        nu.xom.Elements injectionTags = beanTag.getChildElements();
        Object[] parameters = {new Object()};
        for (int i = 0; i < injectionTags.size(); i++) {
            nu.xom.Element parameter = injectionTags.get(i);
            if (parameter.getAttributeValue("autowiringMode")!= null){
                if(parameter.getAttributeValue("autowiringMode").equals("byName")){
                    String beanID = parameter.getAttributeValue("reference");
                    if (beanHashMap.containsKey(beanID)) {
                        System.out.println(i);
                        parameters[i] = beanHashMap.get(beanID).getBeanInstance();
                    } else { //si el bean al que se hace referencia aún no está creado
                        try {
                            parameters[i] = retrieveUncreatedBean(beanID, AutowiringMode.BYNAME);
                        } catch (NullPointerException e) {
                            return null;
                        }
                    }
                } else if (parameter.getAttributeValue("autowiringMode").equals("byType")) {
                    String classReference = parameter.getAttributeValue("reference");
                    parameters[i]= obtainParameterByType(classReference);
                }
            }
            else { //se considera que es byType
                String classReference = parameter.getAttributeValue("reference");
                parameters[i]= obtainParameterByType(classReference);
            }
        }
        return parameters;
    }

    private Object obtainParameterByType (String classReference){
        Object parameter= new Object[]{new Object()};
        boolean found= false;
        Iterator <Map.Entry<String, Bean>>entries = beanHashMap.entrySet().iterator();
        while (!found && entries.hasNext()) {
            Map.Entry<String, Bean> entry = entries.next();
            if(classReference.equals(entry.getValue().getClassName())){
                parameter= entry.getValue().getBeanInstance();
                found= true;
            }
        }
        if(!found) { //si el bean al que se hace referencia aún no está creado
            try {
                parameter = retrieveUncreatedBean(classReference, AutowiringMode.BYTYPE);
            } catch (NullPointerException e) {
                return null;
            }
        }
        return parameter;
    }

    public Object injectDependencies(nu.xom.Element beanTag, Class newClass, boolean isSetter) {
        if (isSetter) {
            Object parameter;
            try {
                parameter = (obtainParameters(beanTag))[0];
            } catch (NullPointerException e) {
                return null;
            }
            String setterName = obtainSetterName(beanTag.getChildElements().get(0));
            return injectSetterDependencies(newClass, setterName, parameter);
        } else {
            Object parameters[];
            try {
                parameters = (obtainParameters(beanTag));
            } catch (NullPointerException e) {
                return null;
            }
            return injectConstructorDependencies(newClass, parameters);
        }
    }


}
