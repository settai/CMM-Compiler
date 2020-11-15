public class exemple{
    static int hashcode(String str)
	{
		int c=0;
			 for(int i=0;i<str.length();i++){
					 c+= (str.charAt(i)*Math.pow(37,i))%300;
			 }
			 return c%300;
     }
    public static void main(String[] args) {
        int c = hashcode("x");
        for(int i='a';i<'z';i++){
            for(int j='a';j<'z';j++){
                if(c==(i+j*37)%300){
                    System.out.println("i="+(char)i+" j="+(char)j);
                }
            }
        }

        System.out.println(hashcode("x")+" = "+hashcode("bj")+" = "+hashcode("fr"));

    }
}