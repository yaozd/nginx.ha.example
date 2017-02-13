package nginx.ha.example.Util;

/**
 * Created by Administrator on 2017/2/10.
 */
public class ArgumentsUtil {
    public static String getArgsValue(String[] args,String key) {
        String value="";
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                String argsStr=args[i];
                if(argsStr.contains(key)){
                    value=argsStr.replace(key,"");
                    System.out.println(key+value);
                    break;
                }
            }
        }
        return value;
    }
}
