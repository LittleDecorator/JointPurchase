import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ParserTEst {

    public static void main(String[] args){
        JSONParser parser = new JSONParser();
        String arr = "[{\"id\":\"5e24b8ea-8a81-4984-ae92-fed630d5b859\",\"name\":\"7 друзей в стаканчиках\",\"companyId\":\"40636ca3-6c19-4ecb-83a2-9027dcd5b22f\",\"article\":\"10580\",\"description\":\"Seven Friends in 7 bowls: Sorting and matching peg dolls in wooden frame (for each day). Tip: You can style the peg dolls with cloth or tape. Our Bus (09480) fits perfectly with the peg dolls. Each hand-painted peg doll is unique! Materials: alder and maple wood, non-toxic water based color stain/non-toxic plant based oil finish. Size: frame diameter 19cm, peg dolls height 6cm, diameter 3cm.\",\"inStock\":null,\"price\":1000.00,\"dateAdd\":null,\"notForSale\":false,\"categories\":[{\"id\":\"a00ce058-a855-407c-ac37-641f8f46fa43\",\"name\":\"Сортеры\",\"parentId\":null,\"dateAdd\":null}]},{\"id\":\"b172e14f-33fd-4819-9831-09c54541feb4\",\"name\":\"Радуга (малая)\",\"companyId\":\"40636ca3-6c19-4ecb-83a2-9027dcd5b22f\",\"article\":\"10760\",\"description\":null,\"inStock\":null,\"price\":1650.00,\"dateAdd\":null,\"notForSale\":false,\"categories\":[{\"id\":\"876d17c2-647c-4b00-9f5e-195d5e57b0b7\",\"name\":\"Пирамидки\",\"parentId\":null,\"dateAdd\":null}]},{\"id\":\"72fab541-593e-48ef-882e-e226775f0b6f\",\"name\":\"Радуга (12 частей)\",\"companyId\":\"40636ca3-6c19-4ecb-83a2-9027dcd5b22f\",\"article\":\"10670\",\"description\":\"The large Rainbow is really versatile and ideal already for small children! Toddlers stack, sort and build and as the children get older they will use it as a cradle for dolls, as fence for animals, like a tunnel or bridge for vehicles, as house for dwarfs and dollhouse dolls, build amazing sculptures... this rainbow will always be integrated in playing with a lot of fantasy. Materials: lime wood, non-toxic water based color stain. Size: length 38cm, height 18cm.\",\"inStock\":null,\"price\":3500.00,\"dateAdd\":null,\"notForSale\":false,\"categories\":[{\"id\":\"34a56bf2-f7b5-4649-b087-cf1b34c5e739\",\"name\":\"Большие конструкторы\",\"parentId\":null,\"dateAdd\":null}]},{\"id\":\"6a20e159-7951-4f65-88f8-3b5696e411fd\",\"name\":\"Радуга (6 частей)\",\"companyId\":\"40636ca3-6c19-4ecb-83a2-9027dcd5b22f\",\"article\":\"10700\",\"description\":null,\"inStock\":null,\"price\":2000.00,\"dateAdd\":null,\"notForSale\":false,\"categories\":[{\"id\":\"a00ce058-a855-407c-ac37-641f8f46fa43\",\"name\":\"Сортеры\",\"parentId\":null,\"dateAdd\":null}]}]\n";
        try {
            JSONArray array = (JSONArray) parser.parse(arr);
            System.out.println(array);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
