import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class ListaInvitados{

	public static List Lista;
	
	public static int numInvitados(){
		return Lista.size();
	}
	
	public static void registrar(AID guest){
		Lista.add(guest);
	}

}
