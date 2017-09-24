package cr.ac.ucr.ecci.ci1330.IoC;

import cr.ac.ucr.ecci.ci1330.IoC.XMLBased.XMLBeanFactory;

/**
 * Created by majo_ on 22/9/2017.
 */
public class Main {
    public static void main(String[] args) {
        String path = "./src/main/resources/beans.xml";
        AbstractBeanFactory abstractBeanFactory= new XMLBeanFactory(path);
        System.out.println("beanHashMap.size()= "+abstractBeanFactory.beanHashMap.size());

        //errores: al constructor no se le puede poner mas de un parametro
    }
}
