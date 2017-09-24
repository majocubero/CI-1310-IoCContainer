package cr.ac.ucr.ecci.ci1330.IoC.XMLBased;

import cr.ac.ucr.ecci.ci1330.IoC.AutowiringMode;
import cr.ac.ucr.ecci.ci1330.IoC.Bean;
import cr.ac.ucr.ecci.ci1330.IoC.ScopeType;
import nu.xom.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by majo_ on 22/9/2017.
 */
public class XMLParser {

    private String path;
    private XMLBeanFactory xmlBeanFactory;

    public XMLParser(String path, XMLBeanFactory xmlBeanFactory) {
        this.path= path;
        this.xmlBeanFactory= xmlBeanFactory;
    }

    public void readXML(){
        Builder builder = new Builder();
        Document xmlDoc = null;
        try {
            xmlDoc = builder.build(path);
        } catch (Exception e) {
            System.out.println("El path es incorrecto");
            e.printStackTrace();
        }
        Element root = xmlDoc.getRootElement();
        Elements beanTags = root.getChildElements();
        for (int i = 0; i < beanTags.size(); i++) {
            Element currentBeanTag = beanTags.get(i);
            if(!xmlBeanFactory.getBeanHashMap().containsKey(currentBeanTag.getAttributeValue("id"))){ //si el bean no existe
                xmlBeanFactory.createBean(obtainBeanAttributes(currentBeanTag));
            }
        }
    }

    public HashMap obtainBeanAttributes(Element beanTag){
        HashMap<String, Object> beanAttributes= new HashMap();
        beanAttributes.put("id", beanTag.getAttributeValue("id"));
        beanAttributes.put("className", beanTag.getAttributeValue("className"));
        if(beanTag.getAttribute("initMethod")!= null){
            beanAttributes.put("initMethod", beanTag.getAttributeValue("initMethod"));
        }
        if(beanTag.getAttribute("destructMethod")!= null){
            beanAttributes.put("destructMethod", beanTag.getAttributeValue("destructMethod"));
        }
        if(beanTag.getAttribute("autowiringMode")!= null){
            String autowiringMode= beanTag.getAttributeValue("autowiringMode");
           beanAttributes.put("autowiringMode", AutowiringMode.valueOf(autowiringMode));
        }
        if(beanTag.getAttribute("scopeType")!= null){
            String scopeType= beanTag.getAttributeValue("scopeType").toUpperCase();

            beanAttributes.put("scopeType", ScopeType.valueOf(scopeType).toString());
        }
        beanAttributes.put("beanInstance", xmlBeanFactory.createBeanInstance(beanTag));
        return beanAttributes;
    }

    public Element findBean(String ref, AutowiringMode autowiringMode){
        Builder builder = new Builder();
        Document xmlDoc = null;
        try {
            xmlDoc = builder.build(path);
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        nu.xom.Element root = xmlDoc.getRootElement();
        nu.xom.Elements beans = root.getChildElements();
        int i = 0;
        boolean found = false;
        while (!found && i < beans.size()) {
            nu.xom.Element currentBean = beans.get(i);
            String idValue = currentBean.getAttributeValue("id");
            String className= currentBean.getAttributeValue("className");
            if (autowiringMode.equals(AutowiringMode.BYNAME) && idValue.equals(ref)) { //realiza la comparación de clases
                return currentBean;
            }
            else if(autowiringMode.equals(AutowiringMode.BYTYPE) && className.equals(ref)){
                return currentBean;
            }
            i++;
        }
        if(!found) {
            System.out.println("ERROR: La clase '" + ref + "' no fue encontrada en la lista de beans. Revisar modo de autowiring.");
        }
        return null;
    }

}
