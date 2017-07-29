package tmall.bean;

/**
 * Created by 15001 on 2017/7/22.
 */
public class User {
    private String name;
    private String password;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnonymousName(){
        if( name == null){
            return null;
        }else if( name.length() == 1){
            return "*";
        }else{
            char []cs = name.toCharArray();
            for(int i=1; i < cs.length-1 ; i++){
                cs[i] =  '*';
            }
            return new String(cs);
        }
    }
}
