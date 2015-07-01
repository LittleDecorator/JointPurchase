import com.acme.handlers.StringUUIDTypeHandler;

import java.util.List;

public class PackageName {

    public static void main(String[] args){
        String packageName = StringUUIDTypeHandler.class.getPackage().getName();
        System.out.println(packageName);
        /*List<Class<? extends Object>> allClasses = new ArrayList<>(new Reflections(packageName).getSubTypesOf(Object.class));
        System.out.println(allClasses.size());
        for(Object o : allClasses){
            System.out.println(o.getClass().getName());
        }*/

        List<Class<?>> classes = ClassFinder.find(packageName);
        for(Class c : classes){
            System.out.println(c.getName());
        }
    }



}
