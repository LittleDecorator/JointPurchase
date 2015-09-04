/**
 * Created by kobzev on 04.09.2015.
 */
public class Bga {

    public static void main(String[] arg){
        String strings = "[1234, 123, 4124, 4112]";
        String[] arr = strings.replaceAll("\\[","").replaceAll("]","").split(",");
        for(int i=0;i<arr.length;i++){
            System.out.println(arr[i].trim());
        }
    }

}
