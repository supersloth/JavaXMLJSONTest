/* File name : MammalInt.java */
public class MammalInit implements Animal{

   public void eat(){
      System.out.println("Mammal eats");
   }

   public void travel(){
      System.out.println("Mammal travels");
   } 

   public int noOfLegs(){
      return 0;
   }

   public static void main(String args[]){
      MammalInit m = new MammalInit();
      m.eat();
      m.travel();
   }
} 